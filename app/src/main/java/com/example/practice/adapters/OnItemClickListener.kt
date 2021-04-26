package com.example.practice.adapters

import org.json.JSONObject

interface OnItemClickListener {
    /**
     * On item click
     * @param item
     */
    fun onItemClick(item: JSONObject)
}