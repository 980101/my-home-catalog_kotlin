package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity implements FavoritesAdapter.OnListItemSelectedInterface {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ItemData> arrayList;
    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.list_favorites);

        arrayList = new ArrayList<>();

        // json 파일의 데이터 가져오기
        MyJson myJson = new MyJson();
        String data = myJson.getData(this);

        try {
            // 데이터의 형변환 (String -> jsonArray)
            JSONArray dataArray = new JSONArray(data);

            // 각 요소로 분리 ( jsonArray -> jsonObject )
            String image, name, price, link;

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.getJSONObject(i);

                image = item.getString("Image");
                name = item.getString("Name");
                price = item.getString("Price");
                link = item.getString("Link");

                ItemData itemData = new ItemData(image, name, price, link);
                arrayList.add(itemData);
            }

        } catch (JSONException e) {
            Log.e("TAG", "Error in Comparing: " + e.getLocalizedMessage());
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoritesAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    public void deleteItem(View view) {
        if (arrayList.size() != 0 && selected > -1) {    // 아이템이 없는 경우, 예외처리
            MyJson myJson = new MyJson();
            myJson.deleteData(this, selected);

            arrayList.remove(selected);
            adapter.notifyItemRemoved(selected);
        } else {
            Toast.makeText(getApplicationContext(), "즐겨찾기한 항목이 없습니다 !", Toast.LENGTH_SHORT).show();
        }
        selected = -1;
    }

    @Override
    public void onItemSelected(View v, int position) {
        selected = position;
    }
}