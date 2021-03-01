package com.example.practice.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CollectionRecyclerViewAdapter(internal var context : Context, internal var collection: ArrayList<JSONObject>) : RecyclerView.Adapter<CollectionRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var albumCover: ImageView
        var albumName: TextView
        var textFormat: TextView
        var nameFormat: TextView
        var descriptionA: TextView
        var descriptionB: TextView
        var year: TextView
        var country: TextView

        init {
            albumCover = view.findViewById(R.id.albumCover)
            albumName = view.findViewById(R.id.artistAndAlbum)
            textFormat = view.findViewById(R.id.textFormat)
            nameFormat = view.findViewById(R.id.nameFormat)
            descriptionA = view.findViewById(R.id.descriptionA)
            descriptionB = view.findViewById(R.id.descriptionB)
            year = view.findViewById(R.id.year)
            country = view.findViewById(R.id.country)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var album : JSONObject = collection.get(position)
        var format : JSONArray = album.getJSONArray("format")
        var formats : JSONObject? = null

        try {
            formats = album.getJSONArray("formats").getJSONObject(0)
        } catch (e : Exception) {
            // TODO: Handle null formats
        }

        // TODO: Add album.has and format.has check

        // holder.albumCover
        holder.albumName.text = album.getString("title")

        //if (formats != null) {
        //    holder.textFormat.text = formats.getString("text")
        //    holder.nameFormat.text = formats.getString("name")
        //}

        // TODO: Get format information

        holder.year.text = album.getString("year")
        holder.country.text = album.getString("country")
    }

    override fun getItemCount(): Int {
        return collection.size
    }
}
