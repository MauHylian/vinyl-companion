package com.example.practice.services

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

open class BaseService {
    var client : OkHttpClient = OkHttpClient()

    protected var scheme : String
    protected var host : String

    companion object {
        abstract class OnGetListener {
            abstract fun onGet(data : Any?, e : Exception? = null)
        }
    }

    constructor(scheme : String, host : String) {
        this.scheme = scheme
        this.host = host
    }

    protected open fun getURLBuilder(): HttpUrl.Builder {
        return HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
    }

    protected fun buildURL(path : String?, queryParameters: Map<String, String>? = null) : String {
        var builder = getURLBuilder()

        if(path != null) builder.addPathSegments(path)

        if(queryParameters != null)
            for((name, value) in queryParameters)
                builder.addQueryParameter(name, value)

        return builder.build().toString();
    }

    protected fun enqueueRequest(req : Request, callback: Callback) {
        return client.newCall(req).enqueue(callback)
    }

    protected fun buildRequest(path : String?, queryParameters : Map<String, String>, method : String = "GET", body: RequestBody? = null) : Request {
        var url = buildURL(path, queryParameters)

        return Request.Builder()
                .method(method, body)
                .url(url)
                .build()
    }

    fun get(queryParameters: Map<String, String>, callback: Callback) {
        return get(null, queryParameters, callback)
    }

    fun get(queryParameters: Map<String, String>, onGetListener: OnGetListener) {
        return get(null, queryParameters, onGetListener)
    }

    fun get(path: String?, queryParameters: Map<String, String>, onGetListener: OnGetListener) {
        return get(path, queryParameters,  object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onGetListener.onGet(null, e)
            }

            override fun onResponse(call: Call, res: Response) {
                if(!res.isSuccessful) {
                    // TODO: Handle error
                } else if (res.body != null) {
                    var body = res.body!!.string()
                    var data : Any? = null

                    try {
                        data = JSONArray(body)
                    } catch (e : Exception) { }

                    try {
                        if(data == null) data = JSONObject(body)
                    } catch (e : Exception) {
                        // TODO: Handle unknown body type error
                    }

                    onGetListener.onGet(data, null)
                }
            }

        })
    }


    fun get(path: String?, queryParameters: Map<String, String>, callback: Callback) {
        var req = buildRequest(path, queryParameters)
        return enqueueRequest(req, callback)
    }

    fun post(path : String, queryParameters: Map<String, String>, body: RequestBody?, callback: Callback) {
        var req = buildRequest(path, queryParameters, "POST", body)
        return enqueueRequest(req, callback)
    }
}