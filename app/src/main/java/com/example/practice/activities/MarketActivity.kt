package com.example.practice.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.MarketRecyclerViewAdapter
import com.example.practice.services.MarketService
import com.example.practice.utils.ItemTouchHelperCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_market.*
import org.json.JSONObject
import java.util.*


/**
 * MarketActivity class
 */
class MarketActivity : BaseActivity(), MarketRecyclerViewAdapter.Companion.OnSwipedListener, MarketRecyclerViewAdapter.Companion.OnItemClickListener {
    var marketService = MarketService()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MarketRecyclerViewAdapter

    var dbList = mutableListOf<JSONObject>()
    var filteredList = mutableListOf<JSONObject>()

    //var listingJSON: JSONObject? = null

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_market
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.marketplace_bar)
        super.onCreate(savedInstanceState)

        adapter = MarketRecyclerViewAdapter(applicationContext)

        recyclerView = findViewById(R.id.listingCollection)
        recyclerView.adapter = adapter

        ItemTouchHelper(ItemTouchHelperCallback(adapter))
                .attachToRecyclerView(recyclerView)

        adapter.onSwipedListener = this
        adapter.onItemClickListener = this

        // Add button
        findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

        search()

    }

    private fun search(){

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                closeKeyboard(listingCollection)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    Log.e("List", dbList.toString())

                    filteredList.clear()
                    val search = newText.toLowerCase(Locale.getDefault())
                    Log.e("DB List", filteredList.toString())
                    dbList.forEach{
                        if (it.has("title")){
                            if (it.getString("title").toLowerCase(Locale.getDefault()).contains(search)) {
                                filteredList.add(it)
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                else{
                    filteredList.clear()
                    filteredList.addAll(dbList)
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    override fun onStart() {
        super.onStart()

        getListings()
    }

    private fun fillListings(listings: MutableList<JSONObject>) {
        adapter.listings = listings
        adapter.notifyDataSetChanged()

        saveNewListing()
    }

    /**
     * Gets all marketplace listings from firebase
     */
    private fun getListings() {
        marketService.get { listings, e ->
            if (e != null) {
                // TODO: Handle error
                Log.e("MarketActivity", "Failed to get listings", e)
                return@get
            }

            if (listings != null){
                dbList = listings.toMutableList()
                filteredList = listings.toMutableList()
                fillListings(filteredList)
            }
        }
    }

    private fun getListing() : JSONObject? {

        if(intent.extras == null) return null

        val listing = JSONObject()

        listing.put("title", getExtra("title"))
        listing.put("description", getExtra("description"))
        listing.put("artist", getExtra("artist"))
        listing.put("year", getExtra("year"))
        listing.put("country", getExtra("country"))
        listing.put("price", getExtra("price"))

        return listing
    }

    private fun saveListing(listing: JSONObject, position: Int = -1) {
        marketService.saveForCurrentUser(listing) { id, e ->
            if (e != null) {
                Log.e("MarketActivity", "Failed to save listing", e)
                return@saveForCurrentUser
            }

            adapter.add(listing, position);

            if(position != -1) recyclerView.scrollToPosition(position);
            else recyclerView.scrollToPosition(adapter.itemCount - 1);
        }
    }

    /**
     * Save user posted listing info from intent extras
     */
    private fun saveNewListing() {
        val listing = getListing() ?: return

        saveListing(listing)
    }

    override fun onSwiped(position: Int) {
        val listing = adapter.get(position)

        marketService.removeForCurrentUser(listing) { e ->
            if (e != null) {
                // TODO: Handle error
                Log.e("MarketActivity", "Failed to remove listing", e)
                adapter.notifyDataSetChanged()
                return@removeForCurrentUser
            }

            adapter.remove(position)
        }
    }

    override fun onItemClick(listing: JSONObject) {
        val extras = Bundle();
        val user =  listing.getString("user")

        extras.putString("TO", user);

        launchActivity(ChatActivity::class.java, extras, 0, true)
    }
}