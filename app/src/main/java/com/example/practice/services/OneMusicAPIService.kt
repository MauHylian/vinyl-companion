package com.example.practice.services

import okhttp3.*

open class OneMusicAPIService() : BaseService("http", "api.onemusicapi.com") {
    // TODO: Write constants to config file

    var userKey = "8f5a18f191ce9e09607ad98b7aab77fe"
    var version = "20171116"

    override fun getURLBuilder(): HttpUrl.Builder {
        var builder = super.getURLBuilder()

        builder.addPathSegment(version)
        builder.addQueryParameter("user_key", userKey)

        return builder
    }
}