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
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_scan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.title = "Escaner"

        editTextBarcode = findViewById(R.id.editTextBarcode)

        findViewById<Button>(R.id.scanBtn).setOnClickListener {
            if(editTextBarcode.text.toString().isNotEmpty()) {
                val intent = Intent(this, AlbumActivity::class.java)
                intent.putExtra("BARCODE", editTextBarcode.text.toString())
                startActivity(intent)
            } else {
                val scanner = IntentIntegrator(this)
                scanner.initiateScan()
            }
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