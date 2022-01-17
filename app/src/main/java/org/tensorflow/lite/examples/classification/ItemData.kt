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
import android.os.Bundle
import android.hardware.Camera.CameraInfo
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

class ItemData {
    var image: String? = null
    var name: String? = null
    var price: String? = null
    var link: String? = null

    // 생성자
    // 기본
    constructor() {}

    // 사용자 지정
    constructor(image: String?, name: String?, price: String?, link: String?) {
        this.image = image
        this.name = name
        this.price = price
        this.link = link
    }
}