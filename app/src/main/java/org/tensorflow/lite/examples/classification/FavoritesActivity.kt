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
import android.util.Log
import android.view.View
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

class FavoritesActivity : AppCompatActivity(), FavoritesAdapter.OnListItemSelectedInterface {
    lateinit var recyclerView: RecyclerView
    private var adapter: RecyclerView.Adapter<*>? = null
    private var arrayList: ArrayList<ItemData>? = null
    private var selected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        recyclerView = findViewById(R.id.list_favorites)
        arrayList = ArrayList()

        // json 파일의 데이터 가져오기
        val data = MyJson.getData(this)
        try {
            // 데이터의 형변환 (String -> jsonArray)
            val dataArray = JSONArray(data)

            // 각 요소로 분리 ( jsonArray -> jsonObject )
            var image: String
            var name: String
            var price: String
            var link: String
            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                image = item.getString("Image")
                name = item.getString("Name")
                price = item.getString("Price")
                link = item.getString("Link")
                val itemData = ItemData(image, name, price, link)
                arrayList!!.add(itemData)
            }
        } catch (e: JSONException) {
            Log.e("TAG", "Error in Comparing: " + e.localizedMessage)
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        adapter = FavoritesAdapter(arrayList, this)
        recyclerView.setAdapter(adapter)
    }

    fun deleteItem(view: View?) {
        if (arrayList!!.size != 0 && selected > -1) {    // 아이템이 없는 경우, 예외처리
            MyJson.deleteData(this, selected)
            arrayList!!.removeAt(selected)
            adapter!!.notifyItemRemoved(selected)
        } else {
            Toast.makeText(applicationContext, "즐겨찾기한 항목이 없습니다 !", Toast.LENGTH_SHORT).show()
        }
        selected = -1
    }

    override fun onItemSelected(v: View?, position: Int) {
        selected = position
    }
}