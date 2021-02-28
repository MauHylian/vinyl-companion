package com.example.practice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.practice.services.AlbumService
import com.example.practice.services.BaseService
import org.json.JSONObject
import com.squareup.picasso.Picasso

class AlbumActivity : BaseActivity() {
    var albumService = AlbumService()

    lateinit var albumURI : String

    lateinit var textAlbum : TextView
    lateinit var textArtist : TextView
    lateinit var textCountry : TextView
    lateinit var textYear : TextView

    lateinit var imageAlbum : ImageView

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_album
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViews();

        findAlbum();

        findViewById<Button>(R.id.moreInfo).setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(albumURI))
                startActivity(browserIntent)
            } catch(e : Exception) {
                // TODO: Handle error
                Log.e("AlbumActivity", "Failed to start browser", e)
            }
        }
    }

    private fun findViews() {
        textAlbum = findViewById(R.id.albumInfoName)
        textCountry = findViewById(R.id.albumCountry)
        textYear = findViewById(R.id.albumYear)

        imageAlbum = findViewById(R.id.albumCover)
    }

    private fun fillAlbum(album: JSONObject) {
        try {
            if(album.has("title"))
                textAlbum.text = album.getString("title")

            if(album.has("year"))
                textYear.text = album.getString("year")

            if(album.has("country"))
                textCountry.text = album.getString("country")

            if(album.has("cover_image"))
                fillAlbumImage(album.getString("cover_image"))

            if(album.has("uri")) albumURI = "https://discogs.com" + album.getString("uri")
        } catch (e: Exception) {
            Log.e("AlbumActivity", e.toString())
            // TODO: Handle error
        }
    }

    private fun fillAlbumImage(url : String) {
        Picasso.get().load(url).into(imageAlbum)
    }

    private fun onFindAlbum(album: JSONObject) {
        fillAlbum(album)
    }

    private fun findAlbum() {
        var barcode = getBarcode();

        if(barcode != null) {
            albumService.getByBarcode(barcode, object : BaseService.Companion.OnGetListener() {
                override fun onGet(data: Any?, e: java.lang.Exception?) {
                    if(e != null) return  // TODO: Handle error

                    this@AlbumActivity.runOnUiThread(Runnable {
                        onFindAlbum(data as JSONObject)
                    })
                }
            })
        }
    }

    private fun getBarcode(): String? {
        val extras = intent.extras

        var barcode : String? = null
        if (extras != null) barcode = extras.getString("BARCODE")

        return barcode
    }
}
