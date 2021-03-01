package com.example.practice.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.practice.R
import com.example.practice.services.AlbumService
import com.example.practice.services.BaseService
import org.json.JSONObject
import com.squareup.picasso.Picasso

/**
 * AlbumActivity class
 */
class AlbumActivity : BaseActivity() {
    var albumService = AlbumService()

    lateinit var textAlbum : TextView
    lateinit var textArtist : TextView
    lateinit var textCountry : TextView
    lateinit var textYear : TextView

    lateinit var imageAlbum : ImageView

    var albumURI : String? = null

    var album : JSONObject? = null

    /**
     * Get layout resource ID
     */
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_album
    }

    /**
     * Find views and set listeners
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textAlbum = findViewById(R.id.albumInfoName)
        textCountry = findViewById(R.id.albumCountry)
        textYear = findViewById(R.id.albumYear)

        imageAlbum = findViewById(R.id.albumCover)

        findViewById<Button>(R.id.addToCollection).setOnClickListener { onClickAddToCollection() }
        findViewById<Button>(R.id.moreInfo).setOnClickListener { onClickMoreInfo() }
    }

    /**
     * Clear previous album (if any) and find album
     */
    override fun onStart() {
        super.onStart()

        clearAlbum()
        findAlbum()
    }

    /**
     * Get barcode from intent extras
     */
    private fun getBarcode(): String? {
        val extras = intent.extras

        var barcode : String? = null
        if (extras != null) barcode = extras.getString("BARCODE")

        return barcode
    }

    /**
     * Launch collection activity
     */
    private fun onClickAddToCollection() {
        val intent = Intent(this, CollectionActivity::class.java)
        if (album != null)
            intent.putExtra("ALBUM", album.toString())

        finish()
        startActivity(intent)
    }

    /**
     * Load album uri on browser
     */
    private fun onClickMoreInfo() {
        if(albumURI == null || albumURI!!.isEmpty()) return;

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(albumURI))
            startActivity(browserIntent)
        } catch(e : Exception) {
            // TODO: Handle error
            Log.e("AlbumActivity", "Failed to start browser", e)
        }
    }

    /**
     * Clear album views
     */
    private fun clearAlbum() {
        textAlbum.text = ""
        textYear.text = ""
        textCountry.text = ""

        try {
            imageAlbum.setImageResource(android.R.color.transparent)
        } catch(e : Exception) {
            Log.e("AlbumActivity", "Failed to clear album image view", e)
        }

        albumURI = ""
        album = null
    }

    /**
     * Fill album views
     */
    private fun fillAlbum(album: JSONObject) {
        this.album = album

        try {
            if(album.has("title"))
                textAlbum.text = album.getString("title")

            if(album.has("year"))
                textYear.text = album.getString("year")

            if(album.has("country"))
                textCountry.text = album.getString("country")

            if(album.has("cover_image"))
                fillAlbumImage(album.getString("cover_image"))

            if(album.has("uri"))
                albumURI = "https://discogs.com" + album.getString("uri")
        } catch (e: Exception) {
            Log.e("AlbumActivity", e.toString())
            // TODO: Handle error
        }
    }

    /**
     * Fill album image view
     */
    private fun fillAlbumImage(url : String) {
        Picasso.get().load(url).into(imageAlbum)
    }

    /**
     * Find album with barcode
     */
    private fun findAlbum() {
        var barcode = getBarcode();

        if(barcode != null) {
            albumService.getByBarcode(barcode, object : BaseService.Companion.OnGetListener() {
                override fun onGet(data: Any?, e: java.lang.Exception?) {
                    if(e != null) return  // TODO: Handle error

                    this@AlbumActivity.runOnUiThread(Runnable {
                        fillAlbum(data as JSONObject)
                    })
                }
            })
        }
    }
}
