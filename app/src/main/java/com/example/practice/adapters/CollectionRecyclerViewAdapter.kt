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

/**
 * CollectionRecyclerViewAdapter class
 */
class CollectionRecyclerViewAdapter(
        private var context : Context,
        private var collection: MutableList<JSONObject>)
    : RecyclerView.Adapter<CollectionRecyclerViewAdapter.ViewHolder>()
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
            if(album.has("year") && album.has("country"))
                yearAndCountry.text = "${album.getString("year")} ${album.getString("country")}"

            // Cover
            try {
                if(album.has("cover_image"))
                    Picasso.get().load(album.getString("cover_image")).into(cover)
            } catch (e : Exception) {
                Log.e("CollectionRecyclerViewAdapter", "Failed to get cover image", e)
            }

            // Format text Format name
            // Descriptions
            if(album.has("formats")) {
                var formats = album.getJSONArray("formats")
                if(formats.length() > 0) {
                    var value = formats.getJSONObject(0)

                    // Format text Format name
                    if(value.has("text") && value.has("name"))
                        format.text = "${value.getString("text")} ${value.getString("name")}"

                    // Descriptions
                    if(value.has("descriptions")) {
                        var text = ""

                        var descs = value.getJSONArray("descriptions")

                        var size = descs.length()
                        for(i in 0 until size - 1)
                            text += descs.getString(i) + ", "
                        text += descs.getString(size - 1)

                        description.text = text
                    }
                }
            }
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
}
