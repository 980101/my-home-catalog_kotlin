package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomActivity extends AppCompatActivity implements CustomAdapter.OnListItemSelectedInterface{

    private CustomAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<CustomData> arrayList;
    private GridLayoutManager gridLayoutManager;
    private Button btn_next;
    public String furniture;

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        mContext = this;

        recyclerView = findViewById(R.id.rv_custom);
        btn_next = findViewById(R.id.btn_next);

        arrayList = new ArrayList<>();

        arrayList.add(new CustomData(R.drawable.ic_furnitures, "all"));
        arrayList.add(new CustomData(R.drawable.ic_chair, "chair"));
        arrayList.add(new CustomData(R.drawable.ic_bed, "bed"));
        arrayList.add(new CustomData(R.drawable.ic_sofa, "sofa"));
        arrayList.add(new CustomData(R.drawable.ic_dresser, "dresser"));
        arrayList.add(new CustomData(R.drawable.ic_table, "table"));

        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_normal);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    public void goCamera(View v) {
        Intent intent = new Intent(getApplicationContext(), ClassifierActivity.class);
        intent.putExtra("type", furniture);

        // 가구 선택 여부를 확인
        if (furniture == null) {
            Toast.makeText(getApplicationContext(), "가구를 선택해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(View v, int position) {
        CustomAdapter.CustomViewHolder viewHolder = (CustomAdapter.CustomViewHolder)recyclerView.findViewHolderForAdapterPosition(position);
        furniture = viewHolder.tv_name.getText().toString();
    }
}