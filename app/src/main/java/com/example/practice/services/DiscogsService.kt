package com.example.practice.services

import okhttp3.HttpUrl

class DiscogsService : BaseService("https", "api.discogs.com") {
    // TODO: Write constants to config file

    val consumerKey = "FrRkJYVAJJOjgXtulkDY"
    val consumerSecret = "inbcWrkhMdtOcSMADzZzWjZNALOCXKQF"

    override fun getURLBuilder(): HttpUrl.Builder {
        var builder = super.getURLBuilder()

        builder.addPathSegment("database")
        builder.addPathSegment("search")
        builder.addQueryParameter("key", consumerKey)
        builder.addQueryParameter("secret", consumerSecret)

        return builder
    }
}