package org.tensorflow.lite.examples.classification

import org.tensorflow.lite.examples.classification.CameraConnectionFragment.Companion.chooseOptimalSize
import org.tensorflow.lite.examples.classification.ItemData
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import org.tensorflow.lite.examples.classification.R
import android.content.Intent
import org.tensorflow.lite.examples.classification.DetailActivity
import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.util.SparseIntArray
import org.tensorflow.lite.examples.classification.LegacyCameraConnectionFragment
import android.hardware.Camera.PreviewCallback
import org.tensorflow.lite.examples.classification.customview.AutoFitTextureView
import android.view.TextureView.SurfaceTextureListener
import android.graphics.SurfaceTexture
import org.tensorflow.lite.examples.classification.CameraConnectionFragment
import org.tensorflow.lite.examples.classification.env.ImageUtils
import android.os.HandlerThread
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.hardware.Camera.CameraInfo
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import org.tensorflow.lite.examples.classification.ItemAdapter
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import org.tensorflow.lite.examples.classification.CustomActivity
import org.tensorflow.lite.examples.classification.InitialActivity
import org.tensorflow.lite.examples.classification.FavoritesActivity
import org.tensorflow.lite.examples.classification.MyJson
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.io.*
import java.lang.StringBuilder

object MyJson {
    var fileName = "savedItem.json"
    fun checkData(context: Context, name: String): Boolean {
        val prev = getData(context) ?: return false // ?????? ?????????
        try {
            val prevArray = JSONArray(prev)
            for (i in 0 until prevArray.length()) {
                val `object` = prevArray.getJSONObject(i)
                val prevName = `object`.getString("Name") // ????????? ???????????? Name ???
                if (prevName == name) return true
            }
        } catch (e: JSONException) {
            Log.e("TAG", "Error in Loading: " + e.localizedMessage)
        }
        return false
    }

    fun saveData(context: Context, mJsonResponse: JSONObject) {
        var isExist = false

        // Json Array ??????
        var jsonArray: JSONArray? = JSONArray()

        // ?????? ???????????? ????????????
        val prev = getData(context)
        if (prev == null) {
            jsonArray!!.put(mJsonResponse)
        } else {
            var prevArray: JSONArray? = null
            try {
                // ?????? ???????????? Json Array ????????? ??????
                prevArray = JSONArray(prev)

                // ????????????
                val name = mJsonResponse.getString("Name") // ????????? ???????????? Name ???
                isExist = checkData(context, name)
            } catch (e: JSONException) {
                Log.e("TAG", "Error in Comparing: " + e.localizedMessage)
            }
            jsonArray = prevArray // ?????? ?????????
            if (!isExist) jsonArray!!.put(mJsonResponse) // ????????? ?????????
        }

        // jsonArray??? ????????? Json File(savedItem.json)??? ??????
        try {
            val fw = FileWriter(context.filesDir.path + "/" + fileName)
            fw.write(jsonArray.toString())
            fw.flush()
            fw.close()
        } catch (e: IOException) {
            Log.e("TAG", "Error in Writing: " + e.localizedMessage)
        }
    }

    fun getData(context: Context): String? {
        return try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                // ????????? ?????? ?????? ??????
                return null
            }
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            stringBuilder.toString()
        } catch (e: IOException) {
            Log.e("TAG", "Error in Reading: " + e.localizedMessage)
            null
        }
    }

    fun deleteData(context: Context, position: Int) {
        val saved = getData(context)
        val newArray = JSONArray()
        try {
            val prevArray = JSONArray(saved)
            val len = prevArray.length()
            if (prevArray != null) {
                for (i in 0 until len) {
                    if (i != position) newArray.put(prevArray[i])
                }
            }
        } catch (e: JSONException) {
            Log.e("TAG", "Error in Deleting: " + e.localizedMessage)
        }

        // ????????? ??????(newArray)??? Json File(savedItem.json)??? ??????
        try {
            val fw = FileWriter(context.filesDir.path + "/" + fileName)
            fw.write(newArray.toString())
            fw.flush()
            fw.close()
        } catch (e: IOException) {
            Log.e("TAG", "Error in Writing: " + e.localizedMessage)
        }
    }
}