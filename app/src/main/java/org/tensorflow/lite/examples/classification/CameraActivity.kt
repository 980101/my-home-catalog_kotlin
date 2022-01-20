/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensorflow.lite.examples.classification

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.media.ImageReader.OnImageAvailableListener
import android.hardware.Camera.PreviewCallback
import android.widget.AdapterView
import org.tensorflow.lite.examples.classification.tflite.Classifier
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.content.Intent
import org.tensorflow.lite.examples.classification.env.ImageUtils
import android.media.Image.Plane
import kotlin.jvm.Synchronized
import android.content.pm.PackageManager
import android.widget.Toast
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraAccessException
import androidx.annotation.UiThread
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition
import android.app.Fragment
import android.content.Context
import android.hardware.Camera
import android.media.ImageReader
import android.os.*
import android.util.Size
import android.view.*
import android.widget.Button
import org.tensorflow.lite.examples.classification.env.Logger
import java.lang.Exception
import java.util.ArrayList

abstract class CameraActivity : AppCompatActivity(), OnImageAvailableListener, PreviewCallback
    , View.OnClickListener, AdapterView.OnItemSelectedListener {
    protected var previewWidth = 0
    protected var previewHeight = 0
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var useCamera2API = false
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    protected var luminanceStride = 0
        private set
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null

    // Do nothing.
    protected var device = Classifier.Device.CPU
        private set(device) {
            // Do nothing.
        }

    // Do nothing.
    protected var numThreads = -1
        private set(numThreads) {
            // Do nothing.
        }
    val mPreferences: SharedPreferences? = getSharedPreferences("file", MODE_PRIVATE);
    private var adapter: HistoryAdapter? = null
    lateinit var recyclerView: RecyclerView
//    private var recyclerView: RecyclerView? = null
    private var arrayList: ArrayList<String?>? = null
    private var btn_capture: Button? = null
    private var recognitionStyle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LOGGER.d("onCreate $this")
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.tfe_ic_activity_camera)

        if (hasPermission()) {
            setFragment()
        } else {
            requestPermission()
        }

        recyclerView = findViewById(R.id.rv_history)
        btn_capture = findViewById(R.id.btn_capture)
        btn_capture!!.setOnClickListener(onClickListener)

        mContext = this

        // SharedPreferences 객체 생성
        arrayList = ArrayList()
//        mPreferences = getSharedPreferences("file", MODE_PRIVATE)

        val allValue = mPreferences?.getAll()
        if (allValue != null) {
            for ((key) in allValue) {
                arrayList!!.add(mPreferences?.getString(key, "no value"))
            }
        }
        recyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_small)
        recyclerView.addItemDecoration(HistoryItemDecoration(spacingInPixels))
        adapter = HistoryAdapter(arrayList)
        recyclerView.setAdapter(adapter)
    }

    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.btn_capture -> goMain()
        }
    }

    fun goMain() {
        if (!TextUtils.isEmpty(recognitionStyle)) {
            saveStyle()
            val furniture = intent.getStringExtra("type")
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("style", recognitionStyle)
            intent.putExtra("type", furniture)
            startActivity(intent)
        }
    }

    fun saveStyle() {
        val editor = mPreferences!!.edit()
        if (mPreferences!!.contains(recognitionStyle)) {  // 저장된 값이 있으면 삭제
            editor.remove(recognitionStyle)
            editor.commit()
        }
        editor.putString(recognitionStyle, recognitionStyle) // 파일에 저장될 형태 (별명, 값)
        editor.apply()
    }

    protected fun getRgbBytes(): IntArray? {
        imageConverter!!.run()
        return rgbBytes
    }

    protected val luminance: ByteArray?
        protected get() = yuvBytes[0]

    /** Callback for android.hardware.Camera API  */
    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        if (isProcessingFrame) {
            LOGGER.w("Dropping frame!")
            return
        }
        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                val previewSize = camera.parameters.previewSize
                previewHeight = previewSize.height
                previewWidth = previewSize.width
                rgbBytes = IntArray(previewWidth * previewHeight)
                onPreviewSizeChosen(Size(previewSize.width, previewSize.height), 90)
            }
        } catch (e: Exception) {
            LOGGER.e(e, "Exception!")
            return
        }
        isProcessingFrame = true
        yuvBytes[0] = bytes
        luminanceStride = previewWidth
        imageConverter = Runnable { ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes) }
        postInferenceCallback = Runnable {
            camera.addCallbackBuffer(bytes)
            isProcessingFrame = false
        }
        processImage()
    }

    /** Callback for Camera2 API  */
    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            Trace.beginSection("imageAvailable")
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            luminanceStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = object : Runnable {
                override fun run() {
                    ImageUtils.convertYUV420ToARGB8888(
                            yuvBytes[0],
                            yuvBytes[1],
                            yuvBytes[2],
                            previewWidth,
                            previewHeight,
                            luminanceStride,
                            uvRowStride,
                            uvPixelStride,
                            rgbBytes)
                }
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: Exception) {
            LOGGER.e(e, "Exception!")
            Trace.endSection()
            return
        }
        Trace.endSection()
    }

    @Synchronized
    public override fun onStart() {
        LOGGER.d("onStart $this")
        super.onStart()
    }

    @Synchronized
    public override fun onResume() {
        LOGGER.d("onResume $this")
        super.onResume()
        handlerThread = HandlerThread("inference")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    @Synchronized
    public override fun onPause() {
        LOGGER.d("onPause $this")
        handlerThread!!.quitSafely()
        try {
            handlerThread!!.join()
            handlerThread = null
            handler = null
        } catch (e: InterruptedException) {
            LOGGER.e(e, "Exception!")
        }
        super.onPause()
    }

    @Synchronized
    public override fun onStop() {
        LOGGER.d("onStop $this")
        super.onStop()
    }

    @Synchronized
    public override fun onDestroy() {
        LOGGER.d("onDestroy $this")
        super.onDestroy()
    }

    @Synchronized
    protected fun runInBackground(r: Runnable?) {
        if (handler != null) {
            handler!!.post(r!!)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
                setFragment()
            } else {
                requestPermission()
            }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
                Toast.makeText(
                        this@CameraActivity,
                        "Camera permission is required for this demo",
                        Toast.LENGTH_LONG)
                        .show()
            }
            requestPermissions(arrayOf(PERMISSION_CAMERA), PERMISSIONS_REQUEST)
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    private fun isHardwareLevelSupported(
            characteristics: CameraCharacteristics, requiredLevel: Int): Boolean {
        val deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)!!
        return if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            requiredLevel == deviceLevel
        } else requiredLevel <= deviceLevel
        // deviceLevel is not LEGACY, can use numerical sort
    }

    private fun chooseCamera(): String? {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        ?: continue

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL
                        || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL))
                LOGGER.i("Camera API lv2?: %s", useCamera2API)
                return cameraId
            }
        } catch (e: CameraAccessException) {
            LOGGER.e(e, "Not allowed to access camera")
        }
        return null
    }

    protected fun setFragment() {
        val cameraId = chooseCamera()
        val fragment: Fragment

        val obj = object : CameraConnectionFragment.ConnectionCallback {
                override fun onPreviewSizeChosen(size: Size?, rotation: Int) {
                    previewHeight = size!!.height
                    previewWidth = size.width
                    onPreviewSizeChosen(size, rotation)
                }
        }

        if (useCamera2API) {
            val camera2Fragment: CameraConnectionFragment = CameraConnectionFragment.Companion.newInstance(
                    obj,
                    this,
                    layoutId,
                    desiredPreviewFrameSize)
            camera2Fragment.setCamera(cameraId)
            fragment = camera2Fragment
        } else {
            fragment = LegacyCameraConnectionFragment(this, layoutId, desiredPreviewFrameSize)
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    protected fun fillBytes(planes: Array<Plane>, yuvBytes: Array<ByteArray?>) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity())
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]]
        }
    }

    protected fun readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback!!.run()
        }
    }

    protected val screenOrientation: Int
        protected get() = when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }

    @UiThread
    protected fun showResultsInBottomSheet(results: List<Recognition?>?) {
        if (results != null && results.size >= 3) {
            val recognition = results[0]
            if (recognition != null) {
                val confidence = recognition.confidence
                if (confidence >= 0.9) {
                    recognitionStyle = recognition.title
                }
            }
        }
    }

    protected fun showFrameInfo(frameInfo: String?) {
        // Do nothing.
    }

    protected fun showCropInfo(cropInfo: String?) {
        // Do nothing.
    }

    protected fun showCameraResolution(cameraInfo: String?) {
        // Do nothing.
    }

    protected fun showRotationInfo(rotation: String?) {
        // Do nothing.
    }

    protected fun showInference(inferenceTime: String?) {
        // Do nothing.
    }

    protected abstract fun processImage()
    protected abstract fun onPreviewSizeChosen(size: Size?, rotation: Int)
    protected abstract val layoutId: Int
    protected abstract val desiredPreviewFrameSize: Size
    protected abstract fun onInferenceConfigurationChanged()
    override fun onClick(v: View) {
        // Do nothing.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
        // Do nothing.
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing.
    }

    companion object {
        private val LOGGER = Logger()
        private const val PERMISSIONS_REQUEST = 1
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        var mContext: Context? = null
        private fun allPermissionsGranted(grantResults: IntArray): Boolean {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}