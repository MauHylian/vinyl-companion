package com.example.practice.services

import okhttp3.Callback
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator

class AlbumService : ReleaseService() {
    fun getByBarcode(barcode : String, callback : Callback) {
        var urlBuilder = getUrlBuilder()
        urlBuilder.addQueryParameter("barcode", barcode)
        urlBuilder.addQueryParameter("inc", "images")

        return enqueueRequest(urlBuilder, callback)
    }

    fun getImage(url : String): RequestCreator? {
        return Picasso.get().load("$url?user_key=$userKey")
    }
}