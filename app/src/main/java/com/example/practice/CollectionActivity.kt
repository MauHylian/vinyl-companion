package com.example.practice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject

class CollectionActivity : BaseActivity() {
    var album : JSONObject? = null

    lateinit var adapter : CollectionRecyclerViewAdapter
    lateinit var recyclerView : RecyclerView

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_collection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var collection : MutableList<JSONObject> = ArrayList()

        if (intent.extras != null) {
            var value = intent.extras?.getString("ALBUM")
            if (value != null) album = JSONObject(value)
        }

        if (album != null) collection.add(album!!)

        recyclerView = findViewById(R.id.albumCollection)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        adapter = CollectionRecyclerViewAdapter(applicationContext, collection)
        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

        Log.d("CollectionActivity", collection.size.toString())

    }
}