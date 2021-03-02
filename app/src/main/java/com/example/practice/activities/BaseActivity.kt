package com.example.practice.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.practice.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_scan.*


/**
 * BaseActivity class
 */
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    /**
     * Get layout resource ID
     */
    protected abstract fun getLayoutResourceID() : Int

    /**
     * On Create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceID())

        // TODO: Refactor

        // Define ActionBar object
        // Define ActionBar object
        val actionBar: ActionBar? = supportActionBar

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))

        // Set BackgroundDrawable
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable) // Changes action bar color
            //actionBar.setTitle(Html.fromHtml("<font color='#000000'>${this::class.java.simpleName}</font>"));

            // Changes action bar title color
            val text: Spannable = SpannableString(actionBar.title)
            text.setSpan(ForegroundColorSpan(Color.DKGRAY), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            actionBar.title = text
        }

        // Menú desplegable
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.black); // Changes hamburger icon color

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Send to different views
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> {
                    val intent = Intent(this, ScanActivity::class.java)
                    startActivity(intent)
                }
                R.id.item2 -> {
                    val intent = Intent(this, CollectionActivity::class.java)
                    startActivity(intent)
                }
                R.id.item3 -> {
                    val intent = Intent(this, MarketActivity::class.java)
                    startActivity(intent)
                }
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

    /**
     * // TODO: Add description
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Get extra from intent
     * @param key
     */
    fun getExtra(key : String) : String? {
        val extras = intent.extras

        var value : String? = null
        if(extras != null) {
            value = extras.getString(key)
            if(value != null && value.isEmpty()) value = null
        }

        return value
    }
}