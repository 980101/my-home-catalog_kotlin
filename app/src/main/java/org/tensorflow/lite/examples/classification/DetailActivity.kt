package org.tensorflow.lite.examples.classification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import org.json.JSONObject
import org.json.JSONException

class DetailActivity : AppCompatActivity() {

    private var image: String? = null
    private var name: String? = null
    private var price: String? = null
    private var link: String? = null
    private var isExisted: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val iv_image:ImageView = findViewById(R.id.iv_detail)
        val tv_name:TextView = findViewById(R.id.tv_detail_name)
        val tv_price:TextView = findViewById(R.id.tv_detail_price)
        val btn_save:Button = findViewById(R.id.btn_detail)

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
        isExisted = MyJson.checkData(this, name.toString())
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