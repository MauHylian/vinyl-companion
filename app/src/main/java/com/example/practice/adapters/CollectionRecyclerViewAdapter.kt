package com.example.practice.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import org.json.JSONObject
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * CollectionRecyclerViewAdapter class
 * @param context
 * @param collection
 */
class CollectionRecyclerViewAdapter(
        private var context : Context,
        private var collection: LinkedList<JSONObject>)
    : RecyclerView.Adapter<CollectionRecyclerViewAdapter.ViewHolder>(), ItemTouchHelperAdapter
{
    /**
     * ViewHolder class
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cover: ImageView

        var title: TextView
        var format: TextView
        var description: TextView
        var yearAndCountry: TextView

        /**
         * Constructor???
         */
        init {
            cover = view.findViewById(R.id.cover)
            title = view.findViewById(R.id.title)
            format = view.findViewById(R.id.format)
            description = view.findViewById(R.id.description)
            yearAndCountry = view.findViewById(R.id.yearAndCountry)
        }

        /**
         * Bind album data to views
         */
        @SuppressLint("SetTextI18n")
        fun bind(album : JSONObject) {
            // Title
            if(album.has("title"))
                title.text = album.getString("title")

            // Year Country
            yearAndCountry.text = String()

            if(album.has("year"))
                yearAndCountry.text = album.getString("year") + " "

            if(album.has("country"))
                yearAndCountry.text = yearAndCountry.text.toString() + album.getString("country")

            if(yearAndCountry.text.isEmpty())
                yearAndCountry.visibility = View.GONE

            // Cover
            try {
                if(album.has("cover_image"))
                    Picasso.get().load(album.getString("cover_image")).into(cover)
            } catch (e : Exception) {
                Log.e("CollectionRecyclerViewAdapter", "Failed to get cover image", e)
            }

            // Format text Format name
            if(album.has("format")) {
                var fmt = album.getJSONObject("format")

                if(fmt.has("text") && fmt.has("name"))
                    format.text = "${fmt.getString("text")} ${fmt.getString("name")}"
                else
                    format.visibility = View.GONE
            }

            // Description
            if(album.has("description"))
                description.text = album.getString("description")
            else
                description.visibility = View.GONE
        }
    }

    /**
     * Create view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_item, parent, false)

        return ViewHolder(view)
    }

    /**
     * Bind view holder listener
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    /**
     * Get item count
     */
    override fun getItemCount(): Int {
        return collection.size
    }

    /**
     * Remove item
     * @param position
     */
    fun removeAt(position : Int) {
        collection.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Add item
     * @param item
     */
    fun add(item : JSONObject) {
        collection.add(item)
        notifyItemInserted(collection.size - 1)
    }

    /**
     * On move
     * @param from
     * @param to
     */
    override fun onMove(from: Int, to: Int): Boolean {
        var i = from

        if(from < to) {
            while(i < to) {
                Collections.swap(collection, i, i + 1)

                ++i
            }
        } else {
            while(i > to) {
                Collections.swap(collection, i, i - 1)

                --i
            }
        }

        notifyItemMoved(from, to)

        return true
    }

    /**
     * On swiped
     * @param position
     */
    override fun onSwiped(position: Int) {
        removeAt(position)
    }
}
