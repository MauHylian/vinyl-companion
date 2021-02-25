package com.example.practice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_scan.*


class AlbumActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        // Menú desplegable
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Send to different views
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item1 -> {
                    val intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                }
                R.id.item2 -> Toast.makeText(applicationContext, "Clicked item 2", Toast.LENGTH_SHORT).show()
                R.id.item3 -> Toast.makeText(applicationContext, "Clicked item 3", Toast.LENGTH_SHORT).show()
                R.id.item4 -> Toast.makeText(applicationContext, "Clicked item 4", Toast.LENGTH_SHORT).show()
                R.id.item5 -> {
                    Firebase.auth.signOut()
                    finish()
                }
            }
            true
        }

        // Más información
        findViewById<Button>(R.id.moreInfo).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discogs.com/Elton-John-Goodbye-Yellow-Brick-Road/master/30577"))
            startActivity(browserIntent)
        }
    }

    // Menú desplegable
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
