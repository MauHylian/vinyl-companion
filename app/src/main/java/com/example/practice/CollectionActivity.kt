package com.example.practice

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import org.json.JSONObject

class CollectionActivity : BaseActivity() {
    lateinit var recyclerView : RecyclerView

    /**
     * Get layout resource ID
     */
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_collection
    }

    /**
     * On create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val collection : ArrayList<JSONObject> = ArrayList()

        val album = getAlbum()
        if (album != null) collection.add(album)

        recyclerView = findViewById(R.id.albumCollection)
        recyclerView.adapter = CollectionRecyclerViewAdapter(applicationContext, collection)
    }

    /**
     * On start
     */
    override fun onStart() {
        super.onStart()
    }

    /**
     * Get album from intent extras
     */
    private fun getAlbum() : JSONObject? {
        var album : JSONObject? = null

        if(intent.extras != null) {
            var value = intent.extras?.getString("ALBUM")
            if(value != null) album = JSONObject(value)
        }

        return album
    }
}