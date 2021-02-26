package com.example.practice

import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_scan.*

open class BaseActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    fun loadDrawer() {
        // Menú desplegable
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Send to different views
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> {
                    val intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                }
                R.id.item2 -> Toast.makeText(applicationContext, "Clicked item 2", Toast.LENGTH_SHORT).show()
                R.id.item3 -> Toast.makeText(applicationContext, "Clicked item 3", Toast.LENGTH_SHORT).show()
                R.id.item4 -> Toast.makeText(applicationContext, "Clicked item 4", Toast.LENGTH_SHORT).show()
                R.id.item5 -> {
                    Firebase.auth.signOut()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    finish()
                    startActivity(intent)
                }
            }
            true
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