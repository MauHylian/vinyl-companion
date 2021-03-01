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

    var collection : ArrayList<JSONObject> = ArrayList()
    lateinit var adapter : CollectionRecyclerViewAdapter
    lateinit var recyclerView : RecyclerView

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_collection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.extras != null) {
            var value = intent.extras?.getString("ALBUM")
            if (value != null) album = JSONObject(value)
        }

        if (album != null) collection.add(album!!)
        //adapter.notifyDataSetChanged()

        adapter = CollectionRecyclerViewAdapter(this, collection)

        recyclerView = findViewById(R.id.albumCollection)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        Log.d("CollectionActivity", album.toString())

    }
}