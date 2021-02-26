package com.example.practice.services

import okhttp3.Callback

class AlbumService {
    var oneMusicAPIService = OneMusicAPIService()
    var discogsService = DiscogsService()

    fun getByBarcode(barcode : String, callback : Callback) {
        oneMusicAPIService.get("release", mapOf("barcode" to barcode), callback)
    }

    fun getByBarcode(barcode : String, onGetListener: BaseService.Companion.OnGetListener) {
        oneMusicAPIService.get("release", mapOf("barcode" to barcode), onGetListener)
    }

    fun getImage(barcode: String, callback : Callback) {
        //return Picasso.get().load("$url?user_key=${oneMusicAPIService.userKey}")
        //discogsService.get(mapOf("barcode" to barcode), callback)
    }
}