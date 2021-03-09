package com.example.practice.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.MarketRecyclerViewAdapter
import com.example.practice.utils.ItemTouchHelperCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.*


/**
 * MarketActivity class
 */
class MarketActivity : BaseActivity(), MarketRecyclerViewAdapter.Companion.OnSwipedListener {
    private val db = Firebase.firestore

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MarketRecyclerViewAdapter

    var listingJSON: JSONObject? = null

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_market
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = MarketRecyclerViewAdapter(applicationContext)

        recyclerView = findViewById(R.id.listingCollection)
        recyclerView.adapter = adapter

        ItemTouchHelper(ItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView)

        adapter.onSwipedListener = this

        findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

        Log.d("MarketActivity", listingJSON.toString())

        // adapter.add(listingJSON)

        getListing()
        saveListing()
        getListings()

    }

    override fun onStart() {
        super.onStart()

        getListings()
    }

    private fun saveListing() {
        val listing = getListing() ?: return

        listing?.let { adapter.add(it) }
        Log.d("MarketActivity", listing.toString())

        val retMap: Map<String, Any> = Gson().fromJson(
                listing.toString(), object : TypeToken<HashMap<String?, Any?>?>() {}.getType()
        )

        db.collection("marketplace")
                .add(retMap)

    }

    private fun fillMarket(listingCollection: LinkedList<JSONObject>) {
        adapter.listingCollection = listingCollection
        adapter.notifyDataSetChanged()

        //saveListing()
    }

    /**
     * Gets all marketplace listings from firebase
     */
    private fun getListings() {
        db.collection("marketplace")
                .get()

        // TODO: Show listings from firebase

    }

    /**
     * Get user posted listing info from intent extras
     */
    private fun getListing(): JSONObject? {
        if (intent.extras != null) {
            var title = intent.getStringExtra("title")
            var description = intent.getStringExtra("description")
            var artist = intent.getStringExtra("artist")
            var year = intent.getStringExtra("year")
            var country = intent.getStringExtra("country")
            var price = intent.getStringExtra("price")

            var listing = hashMapOf("TITLE" to title,
                    "DESCRIPTION" to description,
                    "ARTIST" to artist,
                    "YEAR" to year,
                    "COUNTRY" to country,
                    "PRICE" to price)

            listingJSON = JSONObject(listing as Map<*, *>)
        }

        return listingJSON
    }

    override fun onSwiped(position: Int) {
        println("IMPLEMENT SWIPE")
    }
}