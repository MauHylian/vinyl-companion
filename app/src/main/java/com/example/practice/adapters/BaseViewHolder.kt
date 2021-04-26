package com.example.practice.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

abstract class BaseViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

    /**
     * Set on click listener
     */
    fun setOnClickListener(event: (position: Int, type: Int) -> Unit): BaseViewHolder {
        view.setOnClickListener { event.invoke(adapterPosition, itemViewType) }
        return this
    }

    abstract fun bind(item : JSONObject)
}