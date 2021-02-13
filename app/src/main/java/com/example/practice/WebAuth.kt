package com.example.practice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


class WebAuth: AppCompatActivity(){

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_auth)

        var returnBooleanTest: Boolean? = null
        var check = false
        var webUrlRedirect: String? = null

        val iin = intent
        val b = iin.extras

        var contenido: String? = null
        if (b != null) {
            contenido = b["Keyname"] as String?
            if (contenido != null) {
                Log.d("WebAuth", "Aquí está el token perro: ${contenido}")
            }
        }

        Log.d("WebAuth", "Inicia la actividad")
        Log.d("WebAuth", "EJEMPLO")

        webView = findViewById(R.id.webview)
        webView.settings.setJavaScriptEnabled(true)

        var webUrl: String? = null

        webView.webViewClient = object : WebViewClient() {
            @Override
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    returnBooleanTest = (url.indexOf("https://www.discogs.com/my?oauth_token=") > -1)
                    //view?.loadUrl(url)
                    Log.d("WebAuth", "La URL que contiene el verifier: $url")
                    if(url.contains("https://www.discogs.com/my?oauth_token=")) {
                        Log.d("WebAuth", "Lo contiene perro")
                    }
                    webUrlRedirect = url.toString()
                    println("Esta es la buena ${webUrlRedirect}")
                    if(webUrlRedirect!!.contains("https://www.discogs.com/my?oauth_token=")) {
                        // INICIAR UNA TERCER ACTIVIDAD QUE RECIBA LA URL DE VERIFIER TOKEN
                        // EN DICHA ACTIVIDAD REALIZAR EL POST REQUEST QUE MENCIONA LA DOCUMENTACION DE DISCOGS
                        val intent = Intent(this@WebAuth, WebAuthPost::class.java)
                        intent.putExtra("verifierUrl", webUrlRedirect)
                        intent.putExtra("token", contenido)
                        startActivity(intent)
                    }
                    return (url.indexOf("https://www.discogs.com/my?oauth_token=") > -1)
                }
                return returnBooleanTest!!
            }
        }
        // webView.loadUrl("https://accounts.discogs.com/login")
        webView.loadUrl("https://discogs.com/oauth/authorize?oauth_token=${contenido}")
    }
}