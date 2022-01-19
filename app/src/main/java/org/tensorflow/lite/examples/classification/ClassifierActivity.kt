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

import android.media.ImageReader.OnImageAvailableListener
import org.tensorflow.lite.examples.classification.tflite.Classifier
import android.graphics.Bitmap
import org.tensorflow.lite.examples.classification.env.BorderedText
import android.util.TypedValue
import android.graphics.Typeface
import android.os.*
import android.util.Size
import org.tensorflow.lite.examples.classification.env.Logger
import java.io.IOException

class ClassifierActivity : CameraActivity(), OnImageAvailableListener {
    private var rgbFrameBitmap: Bitmap? = null
    private var lastProcessingTimeMs: Long = 0
    private var sensorOrientation: Int? = null
    private var classifier: Classifier? = null
    private var borderedText: BorderedText? = null

    /** Input image size of the model along x axis.  */
    private var imageSizeX = 0

    /** Input image size of the model along y axis.  */
    private var imageSizeY = 0

    protected override val layoutId: Int
        protected get() = R.layout.tfe_ic_camera_connection_fragment

    override val desiredPreviewFrameSize: Size
        get() = DESIRED_PREVIEW_SIZE

    public override fun onPreviewSizeChosen(size: Size?, rotation: Int) {
        val textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, resources.displayMetrics)
        borderedText = BorderedText(textSizePx)
        borderedText!!.setTypeface(Typeface.MONOSPACE)
        recreateClassifier(device, numThreads)
        if (classifier == null) {
            LOGGER.e("No classifier on preview!")
            return
        }
        previewWidth = size!!.width
        previewHeight = size.height
        sensorOrientation = rotation - screenOrientation
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation)
        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight)
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
    }

    override fun processImage() {
        rgbFrameBitmap!!.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight)
        val cropSize = Math.min(previewWidth, previewHeight)
        runInBackground {
            if (classifier != null) {
                val startTime = SystemClock.uptimeMillis()
                val results = classifier!!.recognizeImage(rgbFrameBitmap, sensorOrientation!!)
                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
                LOGGER.v("Detect: %s", results)
                runOnUiThread {
                    showResultsInBottomSheet(results)
                    showFrameInfo(previewWidth.toString() + "x" + previewHeight)
                    showCropInfo(imageSizeX.toString() + "x" + imageSizeY)
                    showCameraResolution(cropSize.toString() + "x" + cropSize)
                    showRotationInfo(sensorOrientation.toString())
                    showInference(lastProcessingTimeMs.toString() + "ms")
                }
            }
            readyForNextImage()
        }
    }

    override fun onInferenceConfigurationChanged() {
        if (rgbFrameBitmap == null) {
            // Defer creation until we're getting camera frames.
            return
        }
        val device = device
        val numThreads = numThreads
        runInBackground { recreateClassifier(device, numThreads) }
    }

    private fun recreateClassifier(device: Classifier.Device?, numThreads: Int) {
        if (classifier != null) {
            LOGGER.d("Closing classifier.")
            classifier!!.close()
            classifier = null
        }
        try {
            LOGGER.d(
                    "Creating classifier (device=%s, numThreads=%d)", device, numThreads)
            classifier = Classifier.create(this, device, numThreads)
        } catch (e: IOException) {
            LOGGER.e(e, "Failed to create classifier.")
        }

        // Updates the input image size.
        imageSizeX = classifier!!.imageSizeX
        imageSizeY = classifier!!.imageSizeY
    }

    companion object {
        private val LOGGER = Logger()
        protected val DESIRED_PREVIEW_SIZE = Size(640, 480)
        private const val TEXT_SIZE_DIP = 10f
    }
}