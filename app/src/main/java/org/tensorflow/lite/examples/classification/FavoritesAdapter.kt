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
import java.util.ArrayList

class FavoritesAdapter(private val arrayList: ArrayList<ItemData>?, private val mListener: OnListItemSelectedInterface) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {
    interface OnListItemSelectedInterface {
        fun onItemSelected(v: View?, position: Int)
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var iv_photo: ImageView
        var tv_name: TextView
        var tv_price: TextView
        var btn_item: Button
        override fun onClick(v: View) {
            val pos = absoluteAdapterPosition
            when (v.id) {
                R.id.item_favorites -> goDetail(v, pos)
                R.id.btn_item_favorites -> removeItem(v, pos)
            }
        }

        init {

            // 뷰 객체에 대한 참조
            iv_photo = itemView.findViewById(R.id.iv_item_favorites)
            tv_name = itemView.findViewById(R.id.tv_item_favorites_name)
            tv_price = itemView.findViewById(R.id.tv_item_favorites_price)
            btn_item = itemView.findViewById(R.id.btn_item_favorites)
            itemView.setOnClickListener(this)
            btn_item.setOnClickListener(this)
        }
    }

    fun goDetail(v: View, position: Int) {
        val intent = Intent(v.context, DetailActivity::class.java)
        val data = arrayList!![position]
        intent.putExtra("image", data.image)
        intent.putExtra("name", data.name)
        intent.putExtra("price", data.price)
        intent.putExtra("link", data.link)
        v.context.startActivity(intent)
    }

    fun removeItem(v: View?, position: Int) {
        mListener.onItemSelected(v, position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = arrayList!![position]
        Glide.with(holder.iv_photo)
                .load(data.image)
                .into(holder.iv_photo)
        holder.tv_name.text = data.name
        holder.tv_price.text = data.price
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }
}