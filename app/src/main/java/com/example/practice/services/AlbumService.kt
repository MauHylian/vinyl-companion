package com.example.practice.services

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

                onGetListener.onGet(album)
            }
        })
    }
}