package com.example.practice.activities

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import com.example.practice.utils.ItemTouchHelperCallback
import org.json.JSONObject
import java.util.*

/**
 * CollectionActivity class
 */
class CollectionActivity : BaseActivity() {
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : CollectionRecyclerViewAdapter

    companion object {
        var collection = LinkedList<JSONObject>()
    }

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

        val album = getAlbum()
        if (album != null) collection.add(album)

        adapter = CollectionRecyclerViewAdapter(applicationContext, collection)

        recyclerView = findViewById(R.id.albumCollection)
        recyclerView.adapter = adapter

        ItemTouchHelper(ItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView)
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