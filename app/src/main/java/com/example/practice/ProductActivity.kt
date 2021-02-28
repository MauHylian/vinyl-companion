package com.example.practice

import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class ProductActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        loadDrawer()

        findViewById<Button>(R.id.postAlbum).setOnClickListener {
            Toast.makeText(applicationContext, "Publicado segment", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}