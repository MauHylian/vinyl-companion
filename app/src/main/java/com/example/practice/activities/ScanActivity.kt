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
    lateinit var editTextTitle : EditText
    lateinit var editTextYear : EditText
    lateinit var editTextArtist : EditText

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
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
        }

        findViewById<Button>(R.id.searchAlbumBtn).setOnClickListener {
            val intent = Intent(this, AlbumActivity::class.java)

            var extras = Bundle()

            extras.putString("BARCODE", editTextBarcode.text.toString())
            extras.putString("TITLE", editTextTitle.text.toString())
            extras.putString("YEAR", editTextYear.text.toString())
            extras.putString("ARTIST", editTextArtist.text.toString())

            intent.putExtras(extras)

            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result != null) {
                if(result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AlbumActivity::class.java)
                    intent.putExtra("BARCODE", result.contents)
                    startActivity(intent)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
