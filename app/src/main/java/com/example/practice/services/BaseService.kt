package com.example.practice.services

import okhttp3.*

open class BaseService {
    var client : OkHttpClient = OkHttpClient()

    protected var scheme : String
    protected var host : String

    constructor(scheme : String, host : String) {
        this.scheme = scheme
        this.host = host
    }

    protected open fun getURLBuilder(): HttpUrl.Builder {
        return HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
    }

    protected fun buildURL(path : String, queryParameters: Map<String, String>? = null) : String {
        var builder = getURLBuilder()
                .addPathSegments(path)

        if(queryParameters != null)
            for((name, value) in queryParameters)
                builder.addQueryParameter(name, value)

        return builder.build().toString();
    }

    protected fun enqueueRequest(req : Request, callback: Callback) {
        return client.newCall(req).enqueue(callback)
    }

    protected fun buildRequest(path : String, queryParameters : Map<String, String>, method : String = "GET", body: RequestBody? = null) : Request {
        var url = buildURL(path, queryParameters)

        return Request.Builder()
                .method(method, body)
                .url(url)
                .build()
    }

    fun get(path : String, queryParameters: Map<String, String>, callback: Callback) {
        var req = buildRequest(path, queryParameters)
        return enqueueRequest(req, callback)
    }

    fun post(path : String, queryParameters: Map<String, String>, body: RequestBody?, callback: Callback) {
        var req = buildRequest(path, queryParameters, "POST", body)
        return enqueueRequest(req, callback)
    }
}