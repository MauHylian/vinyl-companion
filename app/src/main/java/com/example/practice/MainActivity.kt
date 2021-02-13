package com.example.practice

// import com.android.volley.Request

import android.content.Intent
import java.util.Base64
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import java.util.Base64.getEncoder

open class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var verfifierRecieved = false

        // url we want: https://www.discogs.com/my?oauth_token=sgVSytuDcGvTAovkCiTLZRPjQHKVppslcHIypYbb&oauth_verifier=xMiKPPFOnn
        oauth()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun oauth() {

        // OBTAIN CONSUMER KEY AND CONSUMER SECRET FROM DEVELOPER SETTINGS
        val consumer_key = "FrRkJYVAJJOjgXtulkDY"
        val consumer_secret = "inbcWrkhMdtOcSMADzZzWjZNALOCXKQF"

        println("Attempting to print json")

        val url = "https://api.discogs.com/oauth/request_token"

        val dateTimeString = Date().time.toString()
        Log.d("Main", dateTimeString)
        val nonceByte = dateTimeString
        val encodedNonce: String = getEncoder().encodeToString(nonceByte.toByteArray())
        Log.d("main", encodedNonce)

        var headers = "OAuth oauth_consumer_key=\"FrRkJYVAJJOjgXtulkDY\",oauth_signature_method=\"PLAINTEXT\",oauth_timestamp=\"${dateTimeString}\",oauth_nonce=\"${encodedNonce}\",oauth_version=\"1.0\",oauth_callback=\"https%3A%2F%2Fwww.discogs.com%2Fmy\",oauth_signature=\"inbcWrkhMdtOcSMADzZzWjZNALOCXKQF%26\""

        // SEND A GET REQUEST TO THE DISCOGS REQUEST TOKEN URL
        val request = Request.Builder()
            .url(url)
                .addHeader("Authorization", headers)
            .build()

        var body: String
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                body = response.body?.string().toString()
                Log.d("Main", "El body es: $body")

                // REDIRECT YOUR USER TO THE DISCOGS AUTHORIZE PAGE (https://discogs.com/oauth/authorize?oauth_token=<your_oauth_request_token> OAuth Access Token)

                var oauth_access_token: String? = null
                var oauth_token_secret: String? = null

                val regex = """([\w_]+)=([\w\d]+)&?""".toRegex()

                // val input = body
                val matches = regex.findAll(body!!)
                for (match in matches) {
                    if (match.groups.get(1)?.value == "oauth_token") oauth_access_token = match.groups.get(2)?.value
                    if (match.groups.get(1)?.value == "oauth_token_secret") oauth_token_secret = match.groups.get(2)?.value
                    Log.d("Main", "LA LLAVE ES: ${match.groups.get(1)?.value}")
                    Log.d("Main", "EL VALOR ES: ${match.groups.get(2)?.value}")
                }
                println("ACCESS TOKEN: $oauth_access_token")
                println("TOKEN SECRET: $oauth_token_secret")

                if (oauth_access_token != null && oauth_token_secret != null) {
                    // START WEB AUTH. PASARLE oauth_access_token A LA CLASE WebAuth
                    val intent = Intent(this@MainActivity, WebAuth::class.java)
                    intent.putExtra("Keyname", oauth_access_token)
                    startActivity(intent)
                    //val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://discogs.com/oauth/authorize?oauth_token=${oauth_access_token}"))
                    //startActivity(browserIntent)
                } else {
                    Log.d("Main", "OAuth access token and token secret are null")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute")
            }
        })

        //consumer.setTokenWithSecret(token, secret)

    }

}
