package com.example.practice.services

import okhttp3.HttpUrl
import org.json.JSONArray
import org.json.JSONObject

open class DiscogsService : BaseService("https", "api.discogs.com") {
    // TODO: Write constants to config file

    val consumerKey = "FrRkJYVAJJOjgXtulkDY"
    val consumerSecret = "inbcWrkhMdtOcSMADzZzWjZNALOCXKQF"

    /**
     * Get base discogs URL
     */
    override fun getURLBuilder(): HttpUrl.Builder {
        var builder = super.getURLBuilder()

        builder.addPathSegment("database")
        builder.addPathSegment("search")
        builder.addQueryParameter("key", consumerKey)
        builder.addQueryParameter("secret", consumerSecret)

        return builder
    }

    /**
     * Extract relevant data from album
     * @param album - JSONObject received from service
     */
    fun extractRelevantData(data : JSONObject): JSONObject {
        val album = JSONObject()

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

        val format = JSONObject()
        var descriptions = JSONArray()
        var description = ""

        if(data.has("formats")) {
            var fmts = data.getJSONArray("formats")

            if(fmts.length() > 0) {
                var fmt = fmts.getJSONObject(0)

                if(fmt.has("text"))
                    format.put("text", fmt.get("text"))

                if(fmt.has("name"))
                    format.put("name", fmt.get("name"))

                if(fmt.has("descriptions")) {
                    descriptions = fmt.getJSONArray("descriptions")

                    val size = descriptions.length()

                    for(i in 0 until size - 1)
                        description += descriptions.getString(i) + ", "
                    description += descriptions.getString(size - 1)
                }
            }
        }

        album.put("format", format)
        album.put("descriptions", descriptions)
        album.put("description", description)

        return album
    }

    /**
     * Extract relevant data from array of albums
     * @param arr - JSONArray received from service
     */
    fun extractRelevantData(arr : JSONArray): JSONArray {
        val results = JSONArray()

        for(i in 0 until arr.length())
            results.put(extractRelevantData(arr.getJSONObject(i)))

        return results
    }
}