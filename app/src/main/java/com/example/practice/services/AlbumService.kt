package com.example.practice.services

import okhttp3.Callback
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator

class AlbumService : OneMusicAPIService() {
    fun getByBarcode(barcode : String, callback : Callback) {
        get("release", mapOf("barcode" to barcode), callback)
    }

    fun getImage(url : String): RequestCreator? {
        return Picasso.get().load("$url?user_key=$userKey")
    }
}