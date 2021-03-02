package com.example.practice.activities

import android.os.Bundle
import com.example.practice.R
import com.example.practice.services.AlbumService
import com.example.practice.services.BaseService

/**
 * BrowserActivity class
 */
class BrowserActivity : BaseActivity() {
    var albumService = AlbumService()

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
    }

    /**
     * On start
     */
    override fun onStart() {
        super.onStart()

        findAlbums()
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
            }
        })
    }
}