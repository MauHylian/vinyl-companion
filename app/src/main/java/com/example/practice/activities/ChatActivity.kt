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

        setupNewMessagesListener()

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

    private fun setupNewMessagesListener()
    {
        val other = getOtherUser()
        if(other == null) {
            Log.e("ChatActivity", "Other user is not defined")
            return
        }

        chatService.addNewMessagesListenerForCurrentUser(other) { messages, e ->
            if(e != null) {
                Log.e("ChatActivity", "Error in new messages listener", e)
                return@addNewMessagesListenerForCurrentUser
            }

            if(messages != null) fillNewMessages(messages)
        }
    }

    private fun getOtherUser() : String? {
        return  getExtra("TO")
    }

    private fun saveMessage(content: String) {
        var message = JSONObject()

        message.put("message", content)
        message.put("to", getOtherUser())

        chatService.saveForCurrentUser(message)
    }

    private fun fillMessages(messages: LinkedList<JSONObject>) {
        adapter.collection = messages
        adapter.notifyDataSetChanged()
    }

    private fun fillNewMessages(messages: LinkedList<JSONObject>)
    {
        adapter.collection.addAll(messages)
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun getMessages()
    {
        val other = getOtherUser()
        if(other == null) {
            Log.e("ChatActivity", "Other user is not defined")
            return
        }

        chatService.getForCurrentUser(other) { messages, e ->
            if(e != null) {
                Log.e("ChatActivity", "Failed to get messages")
                return@getForCurrentUser
            }

            if(messages != null) fillMessages(messages)
        }
    }
}