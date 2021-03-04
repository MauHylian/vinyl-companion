package com.example.practice.activities

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.CollectionRecyclerViewAdapter
import com.example.practice.services.AlbumService
import com.example.practice.services.BaseService
import com.example.practice.utils.ItemTouchHelperCallback
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * BrowserActivity class
 */
class BrowserActivity : BaseActivity(), CollectionRecyclerViewAdapter.Companion.OnItemClickListener {
    var albumService = AlbumService()

    lateinit var recyclerView : RecyclerView
    lateinit var adapter : CollectionRecyclerViewAdapter

    /**
     * Get layout resource ID
     */
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_browser
    }

    /**
     * On create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = CollectionRecyclerViewAdapter(applicationContext, LinkedList())
        adapter.onItemClickListener = this

        recyclerView = findViewById(R.id.albumsRecyclerView)
        recyclerView.adapter = adapter
    }

    /**
     * On start
     */
    override fun onStart() {
        super.onStart()

        clearAlbums()
        findAlbums()
    }

    /**
     * Fill albums recycler view
     * @param albums
     */
    private fun fillAlbums(albums : JSONArray) {
        for(i in 0 until albums.length())
            adapter.add(albums.getJSONObject(i))
    }

    /**
     * Clear albums recycler view
     */
    private fun clearAlbums() {
        adapter.removeAll()
    }

    /**
     * Find albums
     */
    private fun findAlbums() {
        val title = getExtra("TITLE")
        val year = getExtra("YEAR")
        val artist = getExtra("ARTIST")

        albumService.get(title, year, artist, object : BaseService.Companion.OnGetListener() {
            override fun onGet(data: Any?, e: java.lang.Exception?) {
                if(e != null) return  // TODO: Handle error

                this@BrowserActivity.runOnUiThread {
                    fillAlbums(data as JSONArray)
                }
            }
        })
    }

    /**
     * On album click
     * @param album
     */
    override fun onItemClick(album: JSONObject) {
        var extras = Bundle()
        extras.putString("ALBUM", album.toString())

        launchActivity(AlbumActivity::class.java, extras)
    }
}