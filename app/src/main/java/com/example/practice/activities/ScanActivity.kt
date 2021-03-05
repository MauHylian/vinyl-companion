package com.example.practice.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.practice.R
import com.google.zxing.integration.android.IntentIntegrator

class ScanActivity : BaseActivity() {
    lateinit var editTextBarcode: EditText
    lateinit var editTextTitle: EditText
    lateinit var editTextYear: EditText
    lateinit var editTextArtist: EditText

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_scan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editTextBarcode = findViewById(R.id.editTextBarcode)
        editTextTitle = findViewById(R.id.albumName)
        editTextYear = findViewById(R.id.releaseYear)
        editTextArtist = findViewById(R.id.artistGroup)

        findViewById<Button>(R.id.scanBtn).setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }

        findViewById<Button>(R.id.searchAlbumBtn).setOnClickListener {
            val extras = Bundle()

            val barcode = editTextBarcode.text.toString()

            if (barcode.isNotEmpty()) {
                extras.putString("BARCODE", barcode)

                // Start AlbumActivity
                launchActivity(AlbumActivity::class.java, extras)
            } else {
                extras.putString("TITLE", editTextTitle.text.toString())
                extras.putString("YEAR", editTextYear.text.toString())
                extras.putString("ARTIST", editTextArtist.text.toString())

                // Start BrowserActivity
                launchActivity(BrowserActivity::class.java, extras)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                ?: return super.onActivityResult(requestCode, resultCode, data)


        if (result.contents != null) {
            Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()

            val extras = Bundle()
            extras.putString("BARCODE", result.contents)

            launchActivity(AlbumActivity::class.java, extras)
        }
    }
}
