package com.example.practice.activities

import android.content.Intent
import android.os.Bundle
import com.example.practice.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MarketActivity : BaseActivity() {
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_market
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

    }
}