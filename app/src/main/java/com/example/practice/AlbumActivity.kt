package com.example.practice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.practice.services.AlbumService
import com.example.practice.services.ImageService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AlbumActivity : BaseActivity() {
    var albumService = AlbumService()
    var imageService = ImageService()

    lateinit var textAlbum : TextView
    lateinit var textArtist : TextView
    lateinit var textCountry : TextView
    lateinit var textYear : TextView

    lateinit var imageAlbum : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        loadDrawer();

        findViews();

        findAlbum();

        // Más información
        findViewById<Button>(R.id.moreInfo).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discogs.com/Elton-John-Goodbye-Yellow-Brick-Road/master/30577"))
            startActivity(browserIntent)
        }
    }

    private fun findViews() {
        textAlbum = findViewById(R.id.albumInfoName)
        textArtist = findViewById(R.id.AlbumArtistGroup)
        textCountry = findViewById(R.id.albumCountry)
        textYear = findViewById(R.id.albumYear)

        imageAlbum = findViewById(R.id.albumCover)
    }

    private fun fillAlbum(album: JSONObject) {
        try {
            if(album.has("title"))
                textAlbum.text = album.getString("title")

            if(album.has("artist"))
                textArtist.text = album.getString("artist")

            if(album.has("year"))
                textYear.text = album.getString("year")

            if(album.has("country"))
                textCountry.text = album.getString("country")

            if(album.has("images")) {
                var images = album.getJSONArray("images")
                if(images.length() != 0)
                    fillAlbumImage(images.getJSONObject(0).getString("url"))
            }
        } catch (e: Exception) {
            Log.e("AlbumActivity", e.toString())
            // TODO: Handle error
        }
    }

    private fun fillAlbumImage(url : String) {
        albumService.getImage(url)?.into(imageAlbum, object: com.squareup.picasso.Callback {
            override fun onSuccess() {
            }

            override fun onError(e: java.lang.Exception?) {
                // TODO: Handle error
            }

        })
    }

    private fun onFindAlbum(albumArray: JSONArray) {
        if(albumArray.length() == 0) {
            Log.e("AlbumActivity", "Empty album array");
            // TODO: Handle empty album array
        } else {
            var album = albumArray.getJSONObject(0)
            fillAlbum(album)
        }
    }

    private fun findAlbum() {
        var barcode = getBarcode();

        if(barcode != null) {
            albumService.getByBarcode(barcode, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    print(e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            var body = response.body?.string().toString()
                            var albumArray = JSONArray(body)

                            this@AlbumActivity.runOnUiThread(Runnable {
                                onFindAlbum(albumArray)
                            })
                        } catch (e: Exception) {
                            Log.e("AlbumActivity", e.toString())
                            // TODO :Handle error
                        }
                    }
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
