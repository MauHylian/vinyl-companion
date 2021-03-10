package com.example.practice.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import com.example.practice.services.CollectionService
import com.example.practice.utils.ItemTouchHelperCallback
import com.google.android.material.snackbar.Snackbar
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
     * Get album from intent extras
     */
    private fun getAlbum(): JSONObject? {
        var album: JSONObject? = null

        if (intent.extras != null) {
            val value = intent.extras?.getString("ALBUM")
            if (value != null) album = JSONObject(value)
        }

        return album
    }

    /**
     * Save album
     * @param album
     * @param position
     */
    private fun saveAlbum(album: JSONObject, position: Int = -1) {
        collectionService.saveForCurrentUser(album) { id, e ->
            if (e != null) {
                Log.e("CollectionActivity", "Failed to save album into collection", e)
                return@saveForCurrentUser
            }

            // TODO: Put id in service
            album.put("id", id)

            adapter.add(album, position);

            if(position != -1) recyclerView.scrollToPosition(position);
            else recyclerView.scrollToPosition(adapter.itemCount - 1);
        }
    }

    /**
     * Save album from intent extras
     */
    private fun saveNewAlbum() {
        val album = getAlbum() ?: return

        saveAlbum(album)
    }

    /**
     * Fill collection recycler view
     * @param collection
     */
    private fun fillCollection(collection: LinkedList<JSONObject>) {
        // TODO: Create collection setter
        adapter.collection = collection
        adapter.notifyDataSetChanged()

        saveNewAlbum()
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
     * On swiped item
     * @param position
     */
    override fun onSwiped(position: Int) {
        val album = adapter.get(position);

        val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.borrar_album))
                .setMessage(getString(R.string.realmente_borrar))

        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            adapter.notifyDataSetChanged();
        }

        builder.setPositiveButton(getString(R.string.si)) { _, _ ->
            collectionService.remove(album) { e ->
                if (e != null) {
                    // TODO: Handle error
                    Log.e("CollectionActivity", "Failed to remove album from collection", e)
                    return@remove
                }

                adapter.remove(position)

                Snackbar.make(recyclerView,
                        getString(R.string.album_eliminado),
                        Snackbar.LENGTH_SHORT).setAction(getString(R.string.deshacer))
                {
                    saveAlbum(album, position)
                }.show()
            }
        }

        builder.show()
    }
}