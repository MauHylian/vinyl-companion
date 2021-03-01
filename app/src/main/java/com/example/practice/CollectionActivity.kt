package com.example.practice

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import org.json.JSONObject


class CollectionActivity : BaseActivity() {
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : CollectionRecyclerViewAdapter

    companion object {
        var collection = ArrayList<JSONObject>()
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

        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean { return false }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }).attachToRecyclerView(recyclerView)
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