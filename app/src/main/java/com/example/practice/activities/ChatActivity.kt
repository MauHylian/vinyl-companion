package com.example.practice.activities

import android.os.Bundle
import com.example.practice.R

class ChatActivity : BaseActivity() {
    override fun getLayoutResourceID(): Int {
        return R.layout.activity_chat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}