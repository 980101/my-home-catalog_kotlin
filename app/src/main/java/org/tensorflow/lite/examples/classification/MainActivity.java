package org.tensorflow.lite.examples.classification;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ItemData> dataList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference styleData;
    private View selectedStyle, selectedType;
    private LinearLayout type_all, type_chair, type_bed
            , type_sofa, type_dresser, type_table;
    private TextView tv_title;
    private Button prevBtn, presBtn;
    private Button btn_custom, btn_initial, btn_favorites;
    private Button btn_style_all, btn_style_natural, btn_style_modern
            , btn_style_classic, btn_style_industrial, btn_style_zen;
    private String pickedStyle, pickedType;

    private String styles[] = {"natural", "modern", "classic", "industrial", "zen"};    // 스타일
    private String types[] = {"bed", "chair", "dresser", "sofa", "table"};              // 가구

    private ArrayList<String> styleList, typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title = findViewById(R.id.tv_title);

        recyclerView = findViewById(R.id.list_furniture);

        type_all = findViewById(R.id.type_all);
        type_chair = findViewById(R.id.type_chair);
        type_bed = findViewById(R.id.type_bed);
        type_sofa = findViewById(R.id.type_sofa);
        type_dresser = findViewById(R.id.type_dresser);
        type_table = findViewById(R.id.type_table);

        btn_style_all = findViewById(R.id.btn_style_all);
        btn_style_natural = findViewById(R.id.btn_style_natural);
        btn_style_modern = findViewById(R.id.btn_style_modern);
        btn_style_classic = findViewById(R.id.btn_style_classic);
        btn_style_industrial = findViewById(R.id.btn_style_industrial);
        btn_style_zen = findViewById(R.id.btn_style_zen);

        // 버튼 설정
        btn_custom = findViewById(R.id.btn_bottom_custom);
        btn_initial = findViewById(R.id.btn_bottom_initial);
        btn_favorites = findViewById(R.id.btn_item_favorites);

        styleList = new ArrayList<>();
        typeList = new ArrayList<>();
        dataList = new ArrayList<>();

        // 데이터 받아오기 : 사용자 지정
        pickedStyle = getIntent().getStringExtra("style") != null ? getIntent().getStringExtra("style") : "all";
        pickedType = getIntent().getStringExtra("type") != null ? getIntent().getStringExtra("type") : "all";

        selectedStyle = null;
        selectedType = null;

        // Firebase
        // 데이터베이스 연동
        database = FirebaseDatabase.getInstance();
        // 데이터베이스 테이블 연결
        databaseReference = database.getReference("all");

        // 타이틀 설정
        chgTitle(tv_title, pickedStyle);

        modifyList();
        loadData();

        btn_style_all.setOnClickListener(onClickListnerByStyle);
        btn_style_natural.setOnClickListener(onClickListnerByStyle);
        btn_style_modern.setOnClickListener(onClickListnerByStyle);
        btn_style_classic.setOnClickListener(onClickListnerByStyle);
        btn_style_industrial.setOnClickListener(onClickListnerByStyle);
        btn_style_zen.setOnClickListener(onClickListnerByStyle);

        type_all.setOnClickListener(onClickListenerByType);
        type_chair.setOnClickListener(onClickListenerByType);
        type_bed.setOnClickListener(onClickListenerByType);
        type_sofa.setOnClickListener(onClickListenerByType);
        type_dresser.setOnClickListener(onClickListenerByType);
        type_table.setOnClickListener(onClickListenerByType);

        switch (pickedStyle) {
            case "all" :
                btn_style_all.setBackground(getDrawable(R.drawable.btn_style_clicked));
                selectedStyle = btn_style_all;
                break;
            case "natural" :
                btn_style_natural.setBackground(getDrawable(R.drawable.btn_style_clicked));
                selectedStyle = btn_style_natural;
                break;
            case "modern" :
                btn_style_modern.setBackground(getDrawable(R.drawable.btn_style_clicked));
                selectedStyle = btn_style_modern;
                break;
            case "classic" :
                btn_style_classic.setBackground(getDrawable(R.drawable.btn_style_clicked));
                selectedStyle = btn_style_classic;
                break;
            case "industrial" :
                btn_style_industrial.setBackground(getDrawable(R.drawable.btn_style_clicked));
                selectedStyle = btn_style_industrial;
                break;
            case "zen" :
                btn_style_zen.setBackground(getDrawable(R.drawable.btn_style_clicked));
                selectedStyle = btn_style_zen;
                break;
        }

        presBtn = findViewById(selectedStyle.getId());
        presBtn.setTextColor(getResources().getColor(R.color.white));

        // 초기 가구의 버튼 배경 설정
        switch (pickedType) {
            case "all" :
                type_all.setBackground(getDrawable(R.drawable.btn_custom_clicked));
                selectedType = type_all;
                break;
            case "chair" :
                type_chair.setBackground(getDrawable(R.drawable.btn_custom_clicked));
                selectedType = type_chair;
                break;
            case "bed" :
                type_bed.setBackground(getDrawable(R.drawable.btn_custom_clicked));
                selectedType = type_bed;
                break;
            case "sofa" :
                type_sofa.setBackground(getDrawable(R.drawable.btn_custom_clicked));
                selectedType = type_sofa;
                break;
            case "dresser" :
                type_dresser.setBackground(getDrawable(R.drawable.btn_custom_clicked));
                selectedType = type_dresser;
                break;
            case "table" :
                type_table.setBackground(getDrawable(R.drawable.btn_custom_clicked));
                selectedType = type_table;
                break;
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ItemAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }

    View.OnClickListener onClickListnerByStyle = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (selectedStyle != v) {
                // 이전 버튼 설정
                selectedStyle.setBackground(getDrawable(R.drawable.btn_style_unclicked)); // 배경색상
                prevBtn = findViewById(selectedStyle.getId()); // 버튼 텍스트 색상
                prevBtn.setTextColor(getResources().getColor(R.color.gray_dark));
            }

            selectedStyle = v;
            v.setBackground(getDrawable(R.drawable.btn_style_clicked));
            presBtn = findViewById(selectedStyle.getId());
            presBtn.setTextColor(getResources().getColor(R.color.white));

            // 모든 타입
            pickedType = "all";
            if (selectedType != type_all) {
                selectedType.setBackground(getDrawable(R.drawable.btn_custom_unclicked));
                selectedType = type_all;
                selectedType.setBackground(getDrawable(R.drawable.btn_custom_clicked));
            }

            switch (v.getId()) {
                case R.id.btn_style_natural:
                    pickedStyle = "natural";
                    break;
                case R.id.btn_style_modern:
                    pickedStyle = "modern";
                    break;
                case R.id.btn_style_classic:
                    pickedStyle = "classic";
                    break;
                case R.id.btn_style_industrial:
                    pickedStyle = "industrial";
                    break;
                case R.id.btn_style_zen:
                    pickedStyle = "zen";
                    break;
                case R.id.btn_style_all:
                    pickedStyle = "all";
                    break;
            }

            modifyList();
            chgTitle(tv_title, pickedStyle);
            dataList.clear();
            loadData();
        }
    };

    View.OnClickListener onClickListenerByType = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedType != v) selectedType.setBackground(getDrawable(R.drawable.btn_custom_unclicked));

            selectedType = v;
            selectedType.setBackground(getDrawable(R.drawable.btn_custom_clicked));

            switch (v.getId()) {
                case R.id.type_chair :
                    pickedType = "chair";
                    break;
                case R.id.type_bed:
                    pickedType = "bed";
                    break;
                case R.id.type_sofa:
                    pickedType = "sofa";
                    break;
                case R.id.type_dresser:
                    pickedType = "dresser";
                    break;
                case R.id.type_table:
                    pickedType = "table";
                    break;
                case R.id.type_all:
                    pickedType = "all";
                    break;
            }

            modifyList();
            dataList.clear();
            loadData();
        }
    };

    public void modifyList() {
        styleList.clear();
        typeList.clear();

        if (pickedStyle.equals("all")) {
            for (String style:styles) {
                styleList.add(style);
            }
        } else {
            styleList.add(pickedStyle);
        }

        if (pickedType.equals("all")) {
            for (String type:types) {
                typeList.add(type);
            }
        } else {
            typeList.add(pickedType);
        }
    }

    public void loadData() {

        for (int i = 0; i < styleList.size(); i++) {
            styleData = databaseReference.child(styleList.get(i));

            for (int j = 0; j < typeList.size(); j++) {
                styleData.child(typeList.get(j)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            ItemData itemData = postSnapshot.getValue(ItemData.class);
                            dataList.add(itemData);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("MainActivity", String.valueOf(error.toException()));
                    }
                });
            }
        }
    }

    // 타이틀 변경
    public void chgTitle(TextView title, String style) {
        switch (style) {
            case "all" :
                title.setText("모든 스타일");
                break;
            case "natural" :
                title.setText("내추럴");
                break;
            case "modern" :
                title.setText("모던");
                break;
            case "classic" :
                title.setText("클래식");
                break;
            case "industrial" :
                title.setText("인더스트리얼");
                break;
            case "zen" :
                title.setText("젠");
                break;
        }
    }

    // 하단의 buttom 클릭 이벤트 설정
    // 가구 지정 화면으로 이동
    public void goCustom(View v) {
        Intent setIntent = new Intent(getApplicationContext(), CustomActivity.class);
        startActivity(setIntent);
    }

    // 홈 화면으로 이동
    public void goInitial(View v) {
        Intent setIntent = new Intent(getApplicationContext(), InitialActivity.class);
        setIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(setIntent);
    }

    // 즐겨찾기 화면으로 이동
    public void goFavorites(View v) {
        Intent setIntent = new Intent(getApplicationContext(), FavoritesActivity.class);
        startActivity(setIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intentToMain = new Intent(this, InitialActivity.class);
        startActivity(intentToMain);
    }
}