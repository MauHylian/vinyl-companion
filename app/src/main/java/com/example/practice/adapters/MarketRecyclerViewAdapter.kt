package com.example.practice.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import org.json.JSONObject
import java.util.*

class MarketRecyclerViewAdapter(
        private var context: Context,
        var listingCollection: LinkedList<JSONObject> = LinkedList() // Maybe linkedlist hashmap
        //var listingCollection: HashMap<Any, Any> = HashMap()
) : RecyclerView.Adapter<MarketRecyclerViewAdapter.ViewHolder>(), ItemTouchHelperAdapter{
    var onItemClickListener: OnItemClickListener? = null //check line
    var onSwipedListener: OnSwipedListener? = null

    /**
     * ViewHolder class
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        //var cover: ImageView = view.findViewById(R.id.listingCover)
        var lTitle : TextView = view.findViewById(R.id.listingTitle)
        var artist : TextView = view.findViewById(R.id.listingArtist)
        var description : TextView = view.findViewById(R.id.listingDescription)
        var price : TextView = view.findViewById(R.id.listingPrice)
        var yearAndCountry : TextView = view.findViewById(R.id.listingYearAndCountry)

        /**
         * Set on click listener
         */
        fun setOnClickListener(event: (position: Int, type: Int) -> Unit): ViewHolder {
            view.setOnClickListener { event.invoke(adapterPosition, itemViewType) }
            return this
        }

        /**
         * Bind album data to views
         */
        @SuppressLint("SetTextI18n")
        fun bind(listing: JSONObject) {
            //if (listing.containsKey("TITLE"))
            //    lTitle.text = listing["TITLE"] as CharSequence?
//
            //if (listing.containsKey("ARTIST"))
            //    artist.text = listing["ARTIST"] as CharSequence?
//
            //if (listing.containsKey("DESCRIPTION"))
            //    description.text = listing["DESCRIPTION"] as CharSequence?
//
            //if (listing.containsKey("PRICE"))
            //    price.text = listing["PRICE"] as CharSequence?
//
            //if (listing.containsKey("YEAR") && listing.containsKey("COUNTRY"))  {
            //    yearAndCountry.text = listing["YEAR"] as CharSequence?
            //    yearAndCountry.text = yearAndCountry.text.toString() + " " + listing["COUNTRY"]
            //}

            // TODO: Bind image with try catch

            if (listing.has("TITLE"))
                lTitle.text = listing.getString("TITLE")

            if (listing.has("ARTIST"))
                artist.text = listing.getString("ARTIST")

            if (listing.has("DESCRIPTION"))
                description.text = listing.getString("DESCRIPTION")

            if(listing.has("PRICE"))
                price.text = listing.getString("PRICE")

            if (listing.has("YEAR") && listing.has("COUNTRY")) {
                yearAndCountry.text = listing.getString("YEAR")
                yearAndCountry.text = yearAndCountry.text.toString() + " " + listing.getString("COUNTRY")
            }

        }
    }

    companion object {
        /**
         * OnItemClickListener interface
         */
        interface OnItemClickListener {
            /**
             * On item click
             * @param listing
             */
            fun onItemClick(listing: JSONObject)
        }

        /**
         * OnSwipedListener interface
         */
        interface OnSwipedListener {
            /**
             * On swiped item
             * @param position
             */
            fun onSwiped(position: Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.market_item, parent, false)

        return ViewHolder(view).setOnClickListener { position: Int, _: Int ->
            onItemClickListener?.onItemClick(get(position))
        }
    }

    /**
     * Bind view holder listener
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(get(position))
    }

    override fun getItemCount(): Int {
        return listingCollection.size
    }

    /**
     * Remove all items (not needed)
     */
    fun removeAll() {
        listingCollection.clear()
        notifyDataSetChanged()
    }

    /**
     * Remove item
     * @param position
     */
    fun remove(position: Int): JSONObject {
        val removed = listingCollection.removeAt(position)

        notifyItemRemoved(position)

        return removed
    }

    /**
     * Add item
     * @param item
     */
    fun add(item: JSONObject) { // maybe hashmap
        listingCollection.add(item)
        notifyItemInserted(listingCollection.size - 1)
    }

    /**
     * Get item at position
     * @param position
     */
    fun get(position: Int): JSONObject {
        return listingCollection[position]
    }

    /**
     * On move
     * @param from
     * @param to
     */
    override fun onMove(from: Int, to: Int): Boolean {
        var i = from

        if (from < to) {
            while (i < to) {
                Collections.swap(listingCollection, i, i + 1)

                ++i
            }
        } else {
            while (i > to) {
                Collections.swap(listingCollection, i, i - 1)

                --i
            }
        }

        notifyItemMoved(from, to)

        return true
    }

    /**
     * On swiped item
     * @param position
     */
    override fun onSwiped(position: Int) {
        onSwipedListener?.onSwiped(position)
    }

}