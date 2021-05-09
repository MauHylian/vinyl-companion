package com.example.practice

import android.webkit.JavascriptInterface
import com.example.practice.activities.CallActivity

class JavascriptInterface(val callActivity: CallActivity) {

    @JavascriptInterface
    public fun onPeerConnected() {
        callActivity.onPeerConnected()
    }
}