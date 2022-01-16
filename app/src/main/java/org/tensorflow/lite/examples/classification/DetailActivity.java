package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailActivity extends AppCompatActivity {

    private MyJson item = new MyJson();
    private ImageView iv_image;
    private TextView tv_name, tv_price;
    private Button btn_save;
    private String image, name, price, link;
    private Boolean isExisted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        iv_image = findViewById(R.id.iv_detail);
        tv_name = findViewById(R.id.tv_detail_name);
        tv_price = findViewById(R.id.tv_detail_price);

        btn_save = findViewById(R.id.btn_detail);

        // Intent 데이터 받아오기
        image = getIntent().getStringExtra("image");
        name = getIntent().getStringExtra("name");
        price = getIntent().getStringExtra("price");
        link = getIntent().getStringExtra("link");

        // 이미지 설정
        Glide.with(iv_image).load(image).into(iv_image);
        tv_name.setText(name);
        tv_price.setText(price);

        // 즐겨찾기 여부 체크
        isExisted = item.checkData(this, name);

        if (isExisted) {
            btn_save.setBackgroundResource(R.drawable.ic_save_fill);
        }
    }

    // '구매하기' 버튼의 이벤트 함수
    public void goToBuy (View view) {
        goToUrl(link);
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    // '저장' 버튼의 이벤트 함수
    public void saveItem (View view) {

        view.setBackgroundResource(R.drawable.ic_save_fill);

        if (isExisted) {
            Toast.makeText(this, "이미 존재하는 아이템입니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 저장할 데이터 설정
            JSONObject jsonObject =  new JSONObject();

            try {
                jsonObject.put("Image", image);
                jsonObject.put("Name", name);
                jsonObject.put("Price", price);
                jsonObject.put("Link", link);
            } catch (JSONException e) {
                Log.e("TAG", "Error: " + e.getLocalizedMessage());
            }

            item.saveData(this, jsonObject);
            isExisted = true;
        }
    }
}