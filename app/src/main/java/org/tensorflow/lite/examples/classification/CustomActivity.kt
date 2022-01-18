package org.tensorflow.lite.examples.classification

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import java.util.ArrayList

class CustomActivity : AppCompatActivity(), CustomAdapter.OnListItemSelectedInterface {

    lateinit var recyclerView: RecyclerView
    var furniture: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        mContext = this
        recyclerView = findViewById<RecyclerView>(R.id.rv_custom)
        val btn_next: Button = findViewById(R.id.btn_next)

        val arrayList = ArrayList<CustomData>()
        arrayList.add(CustomData(R.drawable.ic_furnitures, "all"))
        arrayList.add(CustomData(R.drawable.ic_chair, "chair"))
        arrayList.add(CustomData(R.drawable.ic_bed, "bed"))
        arrayList.add(CustomData(R.drawable.ic_sofa, "sofa"))
        arrayList.add(CustomData(R.drawable.ic_dresser, "dresser"))
        arrayList.add(CustomData(R.drawable.ic_table, "table"))

        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.setLayoutManager(gridLayoutManager)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        recyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        recyclerView.adapter = CustomAdapter(arrayList, this)
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
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) as CustomAdapter.CustomViewHolder?
        furniture = viewHolder!!.tv_name.text.toString()
    }

    companion object {
        var mContext: Context? = null
    }
}