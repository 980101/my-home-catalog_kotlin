package org.tensorflow.lite.examples.classification

import androidx.appcompat.app.AppCompatActivity
import android.media.ImageReader.OnImageAvailableListener
import android.hardware.Camera.PreviewCallback
import android.widget.AdapterView
import android.os.HandlerThread
import org.tensorflow.lite.examples.classification.tflite.Classifier
import android.content.SharedPreferences
import org.tensorflow.lite.examples.classification.HistoryAdapter
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import org.tensorflow.lite.examples.classification.CameraActivity
import android.view.WindowManager
import org.tensorflow.lite.examples.classification.R
import androidx.recyclerview.widget.LinearLayoutManager
import org.tensorflow.lite.examples.classification.HistoryItemDecoration
import android.text.TextUtils
import android.content.Intent
import org.tensorflow.lite.examples.classification.MainActivity
import org.tensorflow.lite.examples.classification.env.ImageUtils
import android.os.Trace
import android.media.Image.Plane
import kotlin.jvm.Synchronized
import android.os.Build
import android.content.pm.PackageManager
import android.widget.Toast
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.hardware.camera2.CameraAccessException
import org.tensorflow.lite.examples.classification.CameraConnectionFragment
import org.tensorflow.lite.examples.classification.LegacyCameraConnectionFragment
import androidx.annotation.UiThread
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition
import android.annotation.SuppressLint
import android.util.SparseIntArray
import org.tensorflow.lite.examples.classification.CameraConnectionFragment.CompareSizesByArea
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import org.tensorflow.lite.examples.classification.customview.AutoFitTextureView
import android.hardware.camera2.CameraDevice
import android.view.TextureView.SurfaceTextureListener
import android.graphics.SurfaceTexture
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import org.tensorflow.lite.examples.classification.CameraConnectionFragment.ErrorDialog
import android.graphics.ImageFormat
import android.graphics.RectF
import android.content.DialogInterface
import android.graphics.Bitmap
import org.tensorflow.lite.examples.classification.env.BorderedText
import org.tensorflow.lite.examples.classification.ClassifierActivity
import android.util.TypedValue
import android.graphics.Typeface
import android.view.View
import android.widget.Button
import org.tensorflow.lite.examples.classification.CustomAdapter
import org.tensorflow.lite.examples.classification.CustomData
import androidx.recyclerview.widget.GridLayoutManager
import org.tensorflow.lite.examples.classification.CustomActivity
import org.tensorflow.lite.examples.classification.SpacesItemDecoration
import android.widget.TextView
import org.tensorflow.lite.examples.classification.MyJson
import com.bumptech.glide.Glide
import org.json.JSONObject
import org.json.JSONException
import org.tensorflow.lite.examples.classification.ItemData
import org.json.JSONArray
import org.tensorflow.lite.examples.classification.FavoritesAdapter
import org.tensorflow.lite.examples.classification.DetailActivity
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.util.ArrayList

class CustomActivity : AppCompatActivity(), CustomAdapter.OnListItemSelectedInterface {
    private var adapter: CustomAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var arrayList: ArrayList<CustomData>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var btn_next: Button? = null
    var furniture: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        mContext = this
        recyclerView = findViewById(R.id.rv_custom)
        btn_next = findViewById(R.id.btn_next)
        arrayList = ArrayList()
        arrayList!!.add(CustomData(R.drawable.ic_furnitures, "all"))
        arrayList!!.add(CustomData(R.drawable.ic_chair, "chair"))
        arrayList!!.add(CustomData(R.drawable.ic_bed, "bed"))
        arrayList!!.add(CustomData(R.drawable.ic_sofa, "sofa"))
        arrayList!!.add(CustomData(R.drawable.ic_dresser, "dresser"))
        arrayList!!.add(CustomData(R.drawable.ic_table, "table"))
        gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(gridLayoutManager)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        recyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        adapter = CustomAdapter(arrayList, this)
        recyclerView.setAdapter(adapter)
    }

    fun goCamera(v: View?) {
        val intent = Intent(applicationContext, ClassifierActivity::class.java)
        intent.putExtra("type", furniture)

        // 가구 선택 여부를 확인
        if (furniture == null) {
            Toast.makeText(applicationContext, "가구를 선택해주세요!", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(intent)
        }
    }

    override fun onItemSelected(v: View?, position: Int) {
        val viewHolder = recyclerView!!.findViewHolderForAdapterPosition(position) as CustomAdapter.CustomViewHolder?
        furniture = viewHolder!!.tv_name.text.toString()
    }

    companion object {
        var mContext: Context? = null
    }
}