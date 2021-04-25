package com.example.practice.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.practice.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_scan.*
import java.util.*
import kotlin.properties.Delegates


/**
 * BaseActivity class
 */
@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private var currentAct = this.javaClass

    companion object {
        private var isEnglish = false
    }

    /**
     * Get layout resource ID
     */
    protected abstract fun getLayoutResourceID(): Int

    /**
     * On Create
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceID())

        // TODO: Refactor

        // Define ActionBar object
        val actionBar: ActionBar? = supportActionBar

        // Set BackgroundDrawable
        if (actionBar != null) {
            // Define ColorDrawable object and parse color
            // using parseColor method
            // with color hash code as its parameter
            val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))

            actionBar.setBackgroundDrawable(colorDrawable) // Changes action bar color
            //actionBar.setTitle(Html.fromHtml("<font color='#000000'>${this::class.java.simpleName}</font>"));

            // Changes action bar title color
            val text: Spannable = SpannableString(actionBar.title)
            text.setSpan(ForegroundColorSpan(Color.DKGRAY), 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            actionBar.title = text

            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.black); // Changes hamburger icon color

        // Send to different views
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> {
                    launchActivity(ScanActivity::class.java)
                }
                R.id.item2 -> {
                    launchActivity(CollectionActivity::class.java)
                }
                R.id.item3 -> {
                    launchActivity(MarketActivity::class.java)
                }
                R.id.item4 -> {

                }
                R.id.item5 -> {
                    Firebase.auth.signOut()
                    launchActivity(LoginActivity::class.java, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, true)
                }
                R.id.item6 -> {
                    Log.d("BaseActivity", currentAct.toString())

                    if (BaseActivity.isEnglish) setLocale("es")
                    else setLocale("en")

                    BaseActivity.isEnglish = !BaseActivity.isEnglish
                }

                R.id.item7 -> {
                    launchActivity(ChatListActivity::class.java)
                }
            }
            true
        }
    }

    open fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(this, currentAct)
        finish()
        startActivity(refresh)
    }

    /**
     * // TODO: Add description
     * @param item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }

    /**
     * Launch new activity
     * @param cls
     * @param extras - Intent extras
     * @param flags - Intent flags
     * @param shouldFinish
     */
    fun <T> launchActivity(cls: Class<T>,
                           extras: Bundle? = null,
                           flags: Int = 0,
                           shouldFinish: Boolean = false
    ) {
        val intent = Intent(this, cls)
                .addFlags(flags)

        if (extras != null)
            intent.putExtras(extras)

        if (shouldFinish)
            finish()

        startActivity(intent)
    }

    /**
     * Get extra from intent
     * @param key
     */
    fun getExtra(key: String): String? {
        val extras = intent.extras

        var value: String? = null
        if (extras != null) {
            value = extras.getString(key)
            if (value != null && value.isEmpty()) value = null
        }

        return value
    }
}