package com.example.practice.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.activities.ChatActivity
import org.json.JSONObject
import java.util.*


class MarketRecyclerViewAdapter(
        private var context: Context,
        var listings: LinkedList<JSONObject> = LinkedList()
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
            view.setOnClickListener {
                event.invoke(adapterPosition, itemViewType)
                // Toast.makeText(view.context, "CLICK", Toast.LENGTH_SHORT).show()
                val intent = Intent(view.context, ChatActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                view.context.startActivity(intent)
            }
            return this
        }

        /**
         * Bind album data to views
         */
        @SuppressLint("SetTextI18n")
        fun bind(listing: JSONObject) {
            // TODO: Bind image with try catch

            if (listing.has("title"))
                lTitle.text = listing.getString("title")

            if (listing.has("artist"))
                artist.text = listing.getString("artist")

            if (listing.has("description"))
                description.text = listing.getString("description")

            if(listing.has("price"))
                price.text = listing.getString("price")

            if (listing.has("year") && listing.has("country")) {
                yearAndCountry.text = listing.getString("year")
                yearAndCountry.text = yearAndCountry.text.toString()+ " " + listing.getString("country")
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
        return listings.size
    }

    /**
     * Remove all items (not needed)
     */
    fun removeAll() {
        listings.clear()
        notifyDataSetChanged()
    }

    /**
     * Remove item
     * @param position
     */
    fun remove(position: Int): JSONObject {
        val removed = listings.removeAt(position)

        notifyItemRemoved(position)

        return removed
    }

    /**
     * Add item
     * @param item
     */
    fun add(item: JSONObject, position: Int = -1) {
        var index = position

        if(position >= 0 && position < listings.size) {
            listings.add(position, item);
        } else {
            listings.add(item)
            index = listings.size - 1
        }

        notifyItemInserted(index);
    }

    /**
     * Get item at position
     * @param position
     */
    fun get(position: Int): JSONObject {
        return listings[position]
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
                Collections.swap(listings, i, i + 1)

                ++i
            }
        } else {
            while (i > to) {
                Collections.swap(listings, i, i - 1)

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