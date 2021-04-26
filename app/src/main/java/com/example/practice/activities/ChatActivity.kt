package com.example.practice.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.*
import com.example.practice.services.ChatService
import org.json.JSONObject
import java.util.*

class ChatActivity : BaseActivity(), OnItemClickListener {
    val chatService = ChatService()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MessagesRecyclerViewAdapter

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_chat
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.Chat)
        super.onCreate(savedInstanceState)

        adapter = MessagesRecyclerViewAdapter(applicationContext)

        recyclerView = findViewById(R.id.messagesCollection)
        recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.stackFromEnd = true

        recyclerView.layoutManager = layoutManager

        adapter.onItemClickListener = this

        val sendButton = findViewById<Button>(R.id.sendButton)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)

        sendButton.setOnClickListener {
            saveMessage(editTextMessage.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()

        getMessages()
    }

    override fun onItemClick(item: JSONObject) {
    }

    private fun saveMessage(content: String) {
        var message = JSONObject()

        message.put("message", content)
        message.put("to", getExtra("TO"))

        chatService.saveForCurrentUser(message)
    }

    private fun fillMessages(messages: LinkedList<JSONObject>) {
        adapter.collection = messages
        adapter.notifyDataSetChanged()
    }

    private fun getMessages()
    {
        chatService.getForCurrentUser() { messages, e ->
            if(e != null) {
                Log.e("ChatActivity", "Failed to get messages")
                return@getForCurrentUser
            }

            if(messages != null) fillMessages(messages)
        }
    }
}