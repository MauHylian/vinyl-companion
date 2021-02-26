package com.example.practice.services

import com.squareup.picasso.*

open class ImageService() : BaseService("image") {
    fun get(url : String): RequestCreator? {
        return Picasso.get().load("$url?user_key=$userKey")
    }
}