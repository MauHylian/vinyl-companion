package com.example.practice.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.JavascriptInterface
import com.example.practice.R
import com.example.practice.adapters.*
import com.example.practice.services.ChatService
import com.example.practice.services.UserService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_call.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.*

class CallActivity : BaseActivity() {
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_call
    }

    // set username to current user email
    var username = ""
    var friendUsername = ""

    var isPeerConnected = false

    var firebaseRef = Firebase.database.getReference("users")

    var isAudio = true
    var isVideo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.Chat)
        super.onCreate(savedInstanceState)

        // username = intent.getStringExtra("username")!!

        callBtn.setOnClickListener {
            friendUsername = friendNameEdit.text.toString()
            sendCallRequest()
        }

        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24)
        }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24)
        }

        setupWebView()
    }

    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "No estas conectado, revisa tu internet.", Toast.LENGTH_LONG).show()
            return
        }

        firebaseRef.child(friendUsername).child("incoming").setValue(username)
        firebaseRef.child(friendUsername).child("isAvailable").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value.toString() == "true") {
                    listenForConnId()
                }

            }
        })
    }

    private fun listenForConnId() {
        firebaseRef.child(friendUsername).child("connId").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                switchToControls()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun setupWebView() {
        webView.webChromeClient = object: WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.allowContentAccess = true; // extra
        webView.settings.allowFileAccess = true; // config
        webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()

    }

    private fun loadVideoCall() {
        val filePath = "./src/main/assets/Content/call.html"
        // val filePath = "file:///android_asset/Content/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""

    private fun initializePeer() {

        uniqueId = getUniqueID()

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef.child(username).child("incoming").addValueEventListener(object:
                ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                onCallRequest(snapshot.value as String?) // Added !! to prevent crash
            }
        })
    }

    private fun onCallRequest(caller: String?) {
        if (caller == null) return

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("connId").setValue(uniqueId)
            firebaseRef.child(username).child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()
        }

        rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("incoming").setValue(null)
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        inputLayout.visibility = View.GONE
        callControlLayout.visibility = View.VISIBLE
    }

    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String) {
        webView.post { webView.evaluateJavascript(functionString, null) }
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(username).setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }

}