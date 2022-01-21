package org.tensorflow.lite.examples.classification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button

class InitialActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        val btn_favorites:Button = findViewById(R.id.btn_favorites)
        val btn_start:Button = findViewById(R.id.btn_start)
        val btn_custom:Button = findViewById(R.id.btn_custom)

        btn_favorites.setOnClickListener(this)
        btn_start.setOnClickListener(this)
        btn_custom.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        lateinit var intent: Intent
        when (v.id) {
            R.id.btn_favorites -> intent = Intent(applicationContext, FavoritesActivity::class.java)
            R.id.btn_start -> intent = Intent(applicationContext, MainActivity::class.java)
            R.id.btn_custom -> intent = Intent(applicationContext, CustomActivity::class.java)
        }
        startActivity(intent)
    }
}