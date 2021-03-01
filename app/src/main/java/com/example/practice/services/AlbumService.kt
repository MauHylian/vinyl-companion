package com.example.practice.services

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

/**
 * AlbumService class
 */
class AlbumService {
    // TODO: Get services using singletons
    var oneMusicAPIService = OneMusicAPIService()
    var discogsService = DiscogsService()

    /**
     * Extract relevant data from album
     * @param album - JSONObject received from service
     */
    private fun extractRelevantData(data : JSONObject): JSONObject {
        var album = JSONObject()

        if(data.has("title"))
            album.put("title", data.get("title"))

        if(data.has("year"))
            album.put("year", data.get("year"))

        if(data.has("country"))
            album.put("country", data.get("country"))

        if(data.has("cover_image"))
            album.put("cover_image", data.get("cover_image"))

        if(data.has("uri"))
            album.put("uri", data.get("uri"))

        var format = JSONObject()
        var description = JSONArray()

        if(data.has("formats")) {
            var fmts = data.getJSONArray("formats")

            if(fmts.length() > 0) {
                var fmt = fmts.getJSONObject(0)

                if(fmt.has("text"))
                    format.put("text", fmt.get("text"))

                if(fmt.has("name"))
                    format.put("name", fmt.get("name"))

                if(fmt.has("descriptions"))
                    description = fmt.getJSONArray("descriptions")
            }
        }

        album.put("format", format)
        album.put("description", description)

        return album
    }

    /**
     * Get album by barcode
     * @param barcode
     * @param onGetListener
     */
    fun getByBarcode(barcode : String, onGetListener: BaseService.Companion.OnGetListener) {
        discogsService.get(mapOf("barcode" to barcode), object : BaseService.Companion.OnGetListener() {
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
}