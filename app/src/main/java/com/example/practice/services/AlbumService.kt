package com.example.practice.services

import okhttp3.Callback

class AlbumService : ReleaseService() {
    fun getByBarcode(barcode : String, callback : Callback) {
        var urlBuilder = getUrlBuilder()
        urlBuilder.addQueryParameter("barcode", barcode)
        urlBuilder.addQueryParameter("inc", "images")

        return enqueueRequest(urlBuilder, callback)
    }
}