package org.tensorflow.lite.examples.classification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InitialActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_favorites, btn_start, btn_custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        btn_favorites = findViewById(R.id.btn_favorites);
        btn_start = findViewById(R.id.btn_start);
        btn_custom = findViewById(R.id.btn_custom);

        btn_favorites.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_custom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btn_favorites:
                intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                break;
            case R.id.btn_start:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.btn_custom:
                intent = new Intent(getApplicationContext(), CustomActivity.class);
                break;
        }

        startActivity(intent);
    }
}