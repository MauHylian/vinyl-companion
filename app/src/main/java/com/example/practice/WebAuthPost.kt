package com.example.practice

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.util.*
import java.util.Base64.getEncoder

// This class goes after WebAuth class
class WebAuthPost: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_auth)

        var oauth_access_token: String? = null
        var oauth_verifier: String? = null
        // Recieve the verifier url from WebAuth
        val iin = intent
        val b = iin.extras

        var contenido: String? = null
        if (b != null) {
            contenido = b["verifierUrl"] as String?
            oauth_access_token = b["token"] as String?
            if (contenido != null) {
                Log.d("WebAuthPost", "Aquí está el verifier url: ${contenido}")
            }
        }

        if (contenido != null) {
            oauth_verifier = contenido.substring(contenido.lastIndexOf("=") + 1)
        }
        if (oauth_verifier != null) {
            Log.d("WebAuthPost", oauth_verifier)
        }

        // Making the authenticated post request
        val dateTimeString = Date().time.toString()
        Log.d("Main", dateTimeString)
        val nonceByte = dateTimeString
        val encodedNonce: String = getEncoder().encodeToString(nonceByte.toByteArray())
        Log.d("main", encodedNonce)
    }
}