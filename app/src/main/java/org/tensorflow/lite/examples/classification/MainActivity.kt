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
import android.util.Log
import android.view.View
import android.widget.Button
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
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var dataList: ArrayList<ItemData?>? = null
    private var database: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var styleData: DatabaseReference? = null
    private var selectedStyle: View? = null
    private var selectedType: View? = null
    private var type_all: LinearLayout? = null
    private var type_chair: LinearLayout? = null
    private var type_bed: LinearLayout? = null
    private var type_sofa: LinearLayout? = null
    private var type_dresser: LinearLayout? = null
    private var type_table: LinearLayout? = null
    private var tv_title: TextView? = null
    private var prevBtn: Button? = null
    private var presBtn: Button? = null
    private var btn_custom: Button? = null
    private var btn_initial: Button? = null
    private var btn_favorites: Button? = null
    private var btn_style_all: Button? = null
    private var btn_style_natural: Button? = null
    private var btn_style_modern: Button? = null
    private var btn_style_classic: Button? = null
    private var btn_style_industrial: Button? = null
    private var btn_style_zen: Button? = null
    private var pickedStyle: String? = null
    private var pickedType: String? = null
    private val styles = arrayOf("natural", "modern", "classic", "industrial", "zen") // 스타일
    private val types = arrayOf("bed", "chair", "dresser", "sofa", "table") // 가구
    private var styleList: ArrayList<String?>? = null
    private var typeList: ArrayList<String?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_title = findViewById(R.id.tv_title)
        recyclerView = findViewById(R.id.list_furniture)
        type_all = findViewById(R.id.type_all)
        type_chair = findViewById(R.id.type_chair)
        type_bed = findViewById(R.id.type_bed)
        type_sofa = findViewById(R.id.type_sofa)
        type_dresser = findViewById(R.id.type_dresser)
        type_table = findViewById(R.id.type_table)
        btn_style_all = findViewById(R.id.btn_style_all)
        btn_style_natural = findViewById(R.id.btn_style_natural)
        btn_style_modern = findViewById(R.id.btn_style_modern)
        btn_style_classic = findViewById(R.id.btn_style_classic)
        btn_style_industrial = findViewById(R.id.btn_style_industrial)
        btn_style_zen = findViewById(R.id.btn_style_zen)

        // 버튼 설정
        btn_custom = findViewById(R.id.btn_bottom_custom)
        btn_initial = findViewById(R.id.btn_bottom_initial)
        btn_favorites = findViewById(R.id.btn_item_favorites)
        styleList = ArrayList()
        typeList = ArrayList()
        dataList = ArrayList()

        // 데이터 받아오기 : 사용자 지정
        pickedStyle = if (intent.getStringExtra("style") != null) intent.getStringExtra("style") else "all"
        pickedType = if (intent.getStringExtra("type") != null) intent.getStringExtra("type") else "all"
        selectedStyle = null
        selectedType = null

        // Firebase
        // 데이터베이스 연동
        database = FirebaseDatabase.getInstance()
        // 데이터베이스 테이블 연결
        databaseReference = database!!.getReference("all")

        // 타이틀 설정
        chgTitle(tv_title, pickedStyle)
        modifyList()
        loadData()
        btn_style_all.setOnClickListener(onClickListnerByStyle)
        btn_style_natural.setOnClickListener(onClickListnerByStyle)
        btn_style_modern.setOnClickListener(onClickListnerByStyle)
        btn_style_classic.setOnClickListener(onClickListnerByStyle)
        btn_style_industrial.setOnClickListener(onClickListnerByStyle)
        btn_style_zen.setOnClickListener(onClickListnerByStyle)
        type_all.setOnClickListener(onClickListenerByType)
        type_chair.setOnClickListener(onClickListenerByType)
        type_bed.setOnClickListener(onClickListenerByType)
        type_sofa.setOnClickListener(onClickListenerByType)
        type_dresser.setOnClickListener(onClickListenerByType)
        type_table.setOnClickListener(onClickListenerByType)
        when (pickedStyle) {
            "all" -> {
                btn_style_all.setBackground(getDrawable(R.drawable.btn_style_clicked))
                selectedStyle = btn_style_all
            }
            "natural" -> {
                btn_style_natural.setBackground(getDrawable(R.drawable.btn_style_clicked))
                selectedStyle = btn_style_natural
            }
            "modern" -> {
                btn_style_modern.setBackground(getDrawable(R.drawable.btn_style_clicked))
                selectedStyle = btn_style_modern
            }
            "classic" -> {
                btn_style_classic.setBackground(getDrawable(R.drawable.btn_style_clicked))
                selectedStyle = btn_style_classic
            }
            "industrial" -> {
                btn_style_industrial.setBackground(getDrawable(R.drawable.btn_style_clicked))
                selectedStyle = btn_style_industrial
            }
            "zen" -> {
                btn_style_zen.setBackground(getDrawable(R.drawable.btn_style_clicked))
                selectedStyle = btn_style_zen
            }
        }
        presBtn = findViewById(selectedStyle!!.id)
        presBtn.setTextColor(resources.getColor(R.color.white))
        when (pickedType) {
            "all" -> {
                type_all.setBackground(getDrawable(R.drawable.btn_custom_clicked))
                selectedType = type_all
            }
            "chair" -> {
                type_chair.setBackground(getDrawable(R.drawable.btn_custom_clicked))
                selectedType = type_chair
            }
            "bed" -> {
                type_bed.setBackground(getDrawable(R.drawable.btn_custom_clicked))
                selectedType = type_bed
            }
            "sofa" -> {
                type_sofa.setBackground(getDrawable(R.drawable.btn_custom_clicked))
                selectedType = type_sofa
            }
            "dresser" -> {
                type_dresser.setBackground(getDrawable(R.drawable.btn_custom_clicked))
                selectedType = type_dresser
            }
            "table" -> {
                type_table.setBackground(getDrawable(R.drawable.btn_custom_clicked))
                selectedType = type_table
            }
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        adapter = ItemAdapter(dataList)
        recyclerView.setAdapter(adapter)
    }

    var onClickListnerByStyle = View.OnClickListener { v ->
        if (selectedStyle !== v) {
            // 이전 버튼 설정
            selectedStyle!!.background = getDrawable(R.drawable.btn_style_unclicked) // 배경색상
            prevBtn = findViewById(selectedStyle!!.id) // 버튼 텍스트 색상
            prevBtn.setTextColor(resources.getColor(R.color.gray_dark))
        }
        selectedStyle = v
        v.background = getDrawable(R.drawable.btn_style_clicked)
        presBtn = findViewById(selectedStyle!!.id)
        presBtn.setTextColor(resources.getColor(R.color.white))

        // 모든 타입
        pickedType = "all"
        if (selectedType !== type_all) {
            selectedType!!.background = getDrawable(R.drawable.btn_custom_unclicked)
            selectedType = type_all
            selectedType!!.background = getDrawable(R.drawable.btn_custom_clicked)
        }
        when (v.id) {
            R.id.btn_style_natural -> pickedStyle = "natural"
            R.id.btn_style_modern -> pickedStyle = "modern"
            R.id.btn_style_classic -> pickedStyle = "classic"
            R.id.btn_style_industrial -> pickedStyle = "industrial"
            R.id.btn_style_zen -> pickedStyle = "zen"
            R.id.btn_style_all -> pickedStyle = "all"
        }
        modifyList()
        chgTitle(tv_title, pickedStyle)
        dataList!!.clear()
        loadData()
    }
    var onClickListenerByType = View.OnClickListener { v ->
        if (selectedType !== v) selectedType!!.background = getDrawable(R.drawable.btn_custom_unclicked)
        selectedType = v
        selectedType!!.background = getDrawable(R.drawable.btn_custom_clicked)
        when (v.id) {
            R.id.type_chair -> pickedType = "chair"
            R.id.type_bed -> pickedType = "bed"
            R.id.type_sofa -> pickedType = "sofa"
            R.id.type_dresser -> pickedType = "dresser"
            R.id.type_table -> pickedType = "table"
            R.id.type_all -> pickedType = "all"
        }
        modifyList()
        dataList!!.clear()
        loadData()
    }

    fun modifyList() {
        styleList!!.clear()
        typeList!!.clear()
        if (pickedStyle == "all") {
            for (style in styles) {
                styleList!!.add(style)
            }
        } else {
            styleList!!.add(pickedStyle)
        }
        if (pickedType == "all") {
            for (type in types) {
                typeList!!.add(type)
            }
        } else {
            typeList!!.add(pickedType)
        }
    }

    fun loadData() {
        for (i in styleList!!.indices) {
            styleData = databaseReference!!.child(styleList!![i]!!)
            for (j in typeList!!.indices) {
                styleData!!.child(typeList!![j]!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val itemData = postSnapshot.getValue(ItemData::class.java)
                            dataList!!.add(itemData)
                        }
                        adapter!!.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MainActivity", error.toException().toString())
                    }
                })
            }
        }
    }

    // 타이틀 변경
    fun chgTitle(title: TextView?, style: String?) {
        when (style) {
            "all" -> title!!.text = "모든 스타일"
            "natural" -> title!!.text = "내추럴"
            "modern" -> title!!.text = "모던"
            "classic" -> title!!.text = "클래식"
            "industrial" -> title!!.text = "인더스트리얼"
            "zen" -> title!!.text = "젠"
        }
    }

    // 하단의 buttom 클릭 이벤트 설정
    // 가구 지정 화면으로 이동
    fun goCustom(v: View?) {
        val setIntent = Intent(applicationContext, CustomActivity::class.java)
        startActivity(setIntent)
    }

    // 홈 화면으로 이동
    fun goInitial(v: View?) {
        val setIntent = Intent(applicationContext, InitialActivity::class.java)
        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(setIntent)
    }

    // 즐겨찾기 화면으로 이동
    fun goFavorites(v: View?) {
        val setIntent = Intent(applicationContext, FavoritesActivity::class.java)
        startActivity(setIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentToMain = Intent(this, InitialActivity::class.java)
        startActivity(intentToMain)
    }
}