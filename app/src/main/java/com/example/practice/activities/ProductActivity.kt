package com.example.practice.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.practice.R

class ProductActivity : BaseActivity() {
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_product
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.postAlbum).setOnClickListener {
            Toast.makeText(applicationContext, "Publicado segment", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}