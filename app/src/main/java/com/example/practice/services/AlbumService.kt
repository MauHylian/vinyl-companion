package com.example.practice.services

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

/**
 * AlbumService class
 */
class AlbumService : DiscogsService() {
    /**
     * Get album by barcode
     * @param barcode
     * @param onGetListener
     */
    fun getByBarcode(barcode : String,
                     onGetListener: BaseService.Companion.OnGetListener) {
        get(mapOf("barcode" to barcode), object : BaseService.Companion.OnGetListener() {
            override fun onGet(data: Any?, e: Exception?) {
                if(e != null) return onGetListener.onGet(null, e)

                var album = JSONObject()

                if(data is JSONObject) {
                    var results = data.getJSONArray("results")
                    if(results.length() != 0) album = results.getJSONObject(0)
                }

                onGetListener.onGet(extractRelevantData(album))
            }
        })
    }

    /**
     * Get album
     * @param title
     * @param year
     * @param artist
     * @param onGetListener
     */
    fun get(title: String?,
            year: String?,
            artist: String?,
            onGetListener: BaseService.Companion.OnGetListener)
    {
        val queryParameters = mutableMapOf<String, String>()

        var qParam = String()

        if(title != null)
            qParam += title

        if(year != null)
            qParam += " $year"

        if(artist != null)
            qParam += " $artist"

        queryParameters["q"] = qParam

        get(queryParameters, object : BaseService.Companion.OnGetListener() {
            override fun onGet(data: Any?, e: Exception?) {
                if(e != null) return onGetListener.onGet(null, e)

                var results = JSONArray()

                if(data is JSONObject)
                    results = data.getJSONArray("results")

                onGetListener.onGet(extractRelevantData(results))
            }
        })
    }
}