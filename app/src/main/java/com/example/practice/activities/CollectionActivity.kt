package com.example.practice.activities

import android.os.Bundle
import android.util.Log
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
class CollectionActivity : BaseActivity(), CollectionRecyclerViewAdapter.Companion.OnSwipedListener {
    var collectionService = CollectionService()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CollectionRecyclerViewAdapter

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

        adapter.onSwipedListener = this
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

        collectionService.saveForCurrentUser(album) { id, e ->
            if (e != null) {
                Log.e("CollectionActivity", "Failed to save album into collection", e)
                return@saveForCurrentUser
            }

            album.put("id", id)
            adapter.add(album)
        }
    }

    /**
     * Fill collection recycler view
     * @param collection
     */
    private fun fillCollection(collection: LinkedList<JSONObject>) {
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
            if (e != null) {
                // TODO: Handle error
                Log.e("CollectionActivity", "Failed to get collection for current user", e)
                return@getForCurrentUser
            }

            if (collection != null) fillCollection(collection)
        }
    }

    /**
     * Get album from intent extras
     */
    private fun getAlbum(): JSONObject? {
        var album: JSONObject? = null

        if (intent.extras != null) {
            var value = intent.extras?.getString("ALBUM")
            if (value != null) album = JSONObject(value)
        }

        return album
    }

    /**
     * On swiped item
     * @param position
     */
    override fun onSwiped(position: Int) {
        collectionService.remove(adapter.get(position)) { e ->
            if (e != null) {
                // TODO: Handle error
                Log.e("CollectionActivity", "Failed to remove album from collection", e)
                return@remove
            }

            adapter.remove(position)
        }
    }
}