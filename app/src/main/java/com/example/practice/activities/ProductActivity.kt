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

    lateinit var aTitle : String
    lateinit var aDescription : String
    lateinit var aArtist : String
    lateinit var aYear : String
    lateinit var aCountry : String
    lateinit var aPrice : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.postAlbum).setOnClickListener {
            Toast.makeText(applicationContext, "Publicado segment", Toast.LENGTH_SHORT).show()

            aTitle = findViewById<EditText>(R.id.albumTitle).text.toString()
            aDescription = findViewById<EditText>(R.id.albumDescription).text.toString()
            aArtist = findViewById<EditText>(R.id.albumArtist).text.toString()
            aYear = findViewById<EditText>(R.id.albumReleaseYear).text.toString()
            aCountry = findViewById<EditText>(R.id.albumReleaseCountry).text.toString()
            aPrice = findViewById<EditText>(R.id.albumPrice).text.toString()

            val intent = Intent(baseContext, MarketActivity::class.java)

            intent.putExtra("artist", aArtist)
            intent.putExtra("description", aDescription)
            intent.putExtra("year", aYear)
            intent.putExtra("country", aCountry)
            intent.putExtra("price", aPrice)
            intent.putExtra("title", aTitle)

            startActivity(intent)

            finish()
        }
    }
}