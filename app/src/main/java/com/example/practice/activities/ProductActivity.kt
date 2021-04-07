package com.example.practice.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.practice.R


class ProductActivity : BaseActivity() {
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_product
    }

    lateinit var title : String
    lateinit var description : String
    lateinit var artist : String
    lateinit var year : String
    lateinit var country : String
    lateinit var price : String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.info_del_producto)
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.postAlbum).setOnClickListener {
            title = findViewById<EditText>(R.id.albumTitle).text.toString()
            description = findViewById<EditText>(R.id.albumDescription).text.toString()
            artist = findViewById<EditText>(R.id.albumArtist).text.toString()
            year = findViewById<EditText>(R.id.albumReleaseYear).text.toString()
            country = findViewById<EditText>(R.id.albumReleaseCountry).text.toString()
            price = findViewById<EditText>(R.id.albumPrice).text.toString()

            val extras = Bundle();

            extras.putString("artist", artist)
            extras.putString("description", description)
            extras.putString("year", year)
            extras.putString("country", country)
            extras.putString("price", price)
            extras.putString("title", title)

            launchActivity(MarketActivity::class.java, extras)

            finish()
        }
    }
}