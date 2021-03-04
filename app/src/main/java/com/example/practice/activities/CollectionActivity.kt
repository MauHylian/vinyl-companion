package com.example.practice.activities

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import com.example.practice.services.CollectionService
import com.example.practice.utils.ItemTouchHelperCallback
import org.json.JSONObject
import java.util.*

/**
 * CollectionActivity class
 */
class CollectionActivity : BaseActivity() {
    var collectionService = CollectionService()

    lateinit var recyclerView : RecyclerView
    lateinit var adapter : CollectionRecyclerViewAdapter

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

        adapter = CollectionRecyclerViewAdapter(applicationContext)

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

        getCollection()
    }

    /**
     * Save album from intent extras
     */
    private fun saveAlbum() {
        val album = getAlbum() ?: return

        adapter.add(album)

        collectionService.saveAlbumForCurrentUser(album)
    }

    /**
     * Fill collection recycler view
     * @param colletion
     */
    private fun fillCollection(collection : LinkedList<JSONObject>) {
        // TODO: Create collection setter
        adapter.collection = collection
        adapter.notifyDataSetChanged()

        saveAlbum()
    }


    /**
     * Get collection
     */
    private fun getCollection() {
        collectionService.getForCurrentUser { collection, e ->
            if(e != null) return@getForCurrentUser // TODO: Handle error

            if(collection == null) return@getForCurrentUser // TODO: Handle null collection

            fillCollection(collection)
        }
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