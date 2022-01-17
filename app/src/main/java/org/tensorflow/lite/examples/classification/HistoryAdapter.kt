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

class HistoryAdapter internal constructor(private val arrayList: ArrayList<String?>?) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tv_history: TextView
        var btn_delete: Button
        override fun onClick(v: View) {
            val pos = absoluteAdapterPosition
            val style = tv_history.text.toString()
            when (v.id) {
                R.id.item_history -> goMain(v, pos, style)
                R.id.btn_item_history -> removeAt(pos, style)
            }
        }

        init {
            tv_history = itemView.findViewById(R.id.tv_item_history)
            btn_delete = itemView.findViewById(R.id.btn_item_history)
            itemView.setOnClickListener(this)
            btn_delete.setOnClickListener(this)
        }
    }

    fun goMain(v: View, position: Int, style: String?) {
        if (position != RecyclerView.NO_POSITION) {
            // style 값 받아오기
            val furniture = (CustomActivity.Companion.mContext as CustomActivity).furniture
            val intent = Intent(v.context, MainActivity::class.java)
            intent.putExtra("style", style)
            intent.putExtra("type", furniture)
            v.context.startActivity(intent)
        }
    }

    fun removeAt(position: Int, style: String?) {
        // 데이터 삭제 : View 부분
        arrayList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arrayList.size)
        // 데이터 삭제 : Data 부분
        val mPreferences = (CameraActivity.Companion.mContext as CameraActivity).mPreferences
        val editor = mPreferences!!.edit()
        editor.remove(style)
        editor.commit()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = arrayList!![position]
        holder.tv_history.text = text
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }
}