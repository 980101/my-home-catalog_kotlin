package org.tensorflow.lite.examples.classification

import androidx.appcompat.app.AppCompatActivity
import android.media.ImageReader.OnImageAvailableListener
import android.hardware.Camera.PreviewCallback
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
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import org.tensorflow.lite.examples.classification.CustomAdapter
import org.tensorflow.lite.examples.classification.CustomData
import androidx.recyclerview.widget.GridLayoutManager
import org.tensorflow.lite.examples.classification.CustomActivity
import org.tensorflow.lite.examples.classification.SpacesItemDecoration
import org.tensorflow.lite.examples.classification.MyJson
import com.bumptech.glide.Glide
import org.json.JSONObject
import org.json.JSONException
import org.tensorflow.lite.examples.classification.ItemData
import org.json.JSONArray
import org.tensorflow.lite.examples.classification.FavoritesAdapter
import org.tensorflow.lite.examples.classification.DetailActivity
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class DetailActivity : AppCompatActivity() {
    private val item = MyJson()
    private var iv_image: ImageView? = null
    private var tv_name: TextView? = null
    private var tv_price: TextView? = null
    private var btn_save: Button? = null
    private var image: String? = null
    private var name: String? = null
    private var price: String? = null
    private var link: String? = null
    private var isExisted: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        iv_image = findViewById(R.id.iv_detail)
        tv_name = findViewById(R.id.tv_detail_name)
        tv_price = findViewById(R.id.tv_detail_price)
        btn_save = findViewById(R.id.btn_detail)

        // Intent 데이터 받아오기
        image = intent.getStringExtra("image")
        name = intent.getStringExtra("name")
        price = intent.getStringExtra("price")
        link = intent.getStringExtra("link")

        // 이미지 설정
        Glide.with(iv_image).load(image).into(iv_image)
        tv_name.setText(name)
        tv_price.setText(price)

        // 즐겨찾기 여부 체크
        isExisted = MyJson.checkData(this, name)
        if (isExisted!!) {
            btn_save.setBackgroundResource(R.drawable.ic_save_fill)
        }
    }

    // '구매하기' 버튼의 이벤트 함수
    fun goToBuy(view: View?) {
        goToUrl(link)
    }

    private fun goToUrl(url: String?) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }

    // '저장' 버튼의 이벤트 함수
    fun saveItem(view: View) {
        view.setBackgroundResource(R.drawable.ic_save_fill)
        if (isExisted!!) {
            Toast.makeText(this, "이미 존재하는 아이템입니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 저장할 데이터 설정
            val jsonObject = JSONObject()
            try {
                jsonObject.put("Image", image)
                jsonObject.put("Name", name)
                jsonObject.put("Price", price)
                jsonObject.put("Link", link)
            } catch (e: JSONException) {
                Log.e("TAG", "Error: " + e.localizedMessage)
            }
            MyJson.saveData(this, jsonObject)
            isExisted = true
        }
    }
}