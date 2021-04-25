package com.example.practice.activities

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.adapters.ChatListAdapter
import com.example.practice.services.ChatService
import org.json.JSONObject
import java.util.*

class ChatListActivity : BaseActivity() {
    var chatService = ChatService()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ChatListAdapter

    override fun getLayoutResourceID(): Int {
        return R.layout.activity_chat_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(R.string.chats)
        super.onCreate(savedInstanceState)

        adapter = ChatListAdapter(applicationContext)

        recyclerView = findViewById(R.id.chatsCollection)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        getChats()
    }

    private fun fillChats(chats: LinkedList<JSONObject>) {
        adapter.collection = chats
        adapter.notifyDataSetChanged()
    }

    private fun getChats() {
        chatService.getHeadForCurrentUser { chats, e ->
            if(e != null) {
                // TODO: Handle error
                Log.e("ChatListActivity", "Failed to get chats", e)
                return@getHeadForCurrentUser
            }

            if (chats != null) fillChats(chats)
        }
    }
}