package com.example.practice.services

import okhttp3.*

open class BaseService {
    var client : OkHttpClient = OkHttpClient()

    var userKey = "8f5a18f191ce9e09607ad98b7aab77fe"

    var scheme = "http"
    var host = "api.onemusicapi.com"
    var version = "20171116"

    lateinit var endpoint : String

    constructor(endpoint : String) {
        this.endpoint = endpoint
    }

    fun getUrlBuilder() : HttpUrl.Builder {
        return HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .addPathSegment(version)
            .addPathSegment(endpoint)
            .addQueryParameter("user_key", userKey)
    }

    fun createRequest(urlBuilder :  HttpUrl.Builder) : Request {
        var url = urlBuilder.build().toString()

        return Request.Builder()
            .url(url)
            .build()
    }

    fun enqueueRequest(req : Request, callback : Callback) {
        return client.newCall(req).enqueue(callback)
    }

    fun enqueueRequest(urlBuilder: HttpUrl.Builder, callback: Callback) {
        var req = createRequest(urlBuilder)
        return enqueueRequest(req, callback)
    }
}