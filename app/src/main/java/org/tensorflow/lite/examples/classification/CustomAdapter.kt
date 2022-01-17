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
import android.view.View
import android.widget.ImageView
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

class CustomAdapter(private val arrayList: ArrayList<CustomData>?, private val mListener: OnListItemSelectedInterface) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {
    var selectedIdx = -1

    interface OnListItemSelectedInterface {
        fun onItemSelected(v: View?, position: Int)
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var iv_icon: ImageView
        var tv_name: TextView
        override fun onClick(v: View) {
            when (v.id) {
                R.id.item_custom -> selectItem(v, absoluteAdapterPosition)
            }
        }

        init {
            iv_icon = itemView.findViewById(R.id.iv_custom_item)
            tv_name = itemView.findViewById(R.id.tv_custom_item)
            itemView.setOnClickListener(this)
        }
    }

    fun selectItem(v: View?, position: Int) {
        selectedIdx = position
        mListener.onItemSelected(v, position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)

        // 아이템의 크기를 동적으로 변경
        val width = parent.measuredWidth / 2
        val height = parent.measuredHeight / 3
        view.minimumWidth = width
        view.minimumHeight = height
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.iv_icon.setImageResource(arrayList!![position].iv_icon)
        holder.tv_name.text = arrayList[position].tv_name
        if (selectedIdx == holder.absoluteAdapterPosition) {
            holder.itemView.setBackgroundResource(R.drawable.btn_custom_clicked)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.btn_custom_unclicked)
        }
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }
}