package com.example.practice.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.practice.R
import com.example.practice.services.UserService
import org.json.JSONObject
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class MessagesRecyclerViewAdapter(
        private var context: Context,
) : BaseRecyclerViewAdapter<MessagesRecyclerViewAdapter.ViewHolder>(context) {
    class ViewHolder(var view : View) : BaseViewHolder(view) {
        // TODO: Create singleton
        private val userService = UserService()

        var textFrom: TextView = view.findViewById(R.id.from)
        var textTo: TextView = view.findViewById(R.id.to)
        var textMessage: TextView = view.findViewById(R.id.message)
        var textSendAt: TextView = view.findViewById(R.id.sendAt)

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun bind(item: JSONObject) {
            if(item.has("from")) {
                bindUser(item.getString("from"), textFrom, view.context.getString(R.string.from))
            }

            if(item.has("to")) {
                bindUser(item.getString("to"), textTo, view.context.getString(R.string.to))
            }

            if(item.has("message")) {
                textMessage.text = item.getString("message")
            }

            if(item.has("sendAt")) {
                val timestamp = item.get("sendAt") as Timestamp
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa")
                textSendAt.text = sdf.format(timestamp.toDate())
            }
        }

        @SuppressLint("SetTextI18n")
        private fun bindUser(id : String, textView: TextView, label : String)
        {
            userService.get(id) { user, e ->
                textView.text = id

                if(e != null) {
                    Log.e("MessagesRecyclerViewAdapter", "Failed to get user", e)
                }

                if(user != null) {
                    if(user.has("username")) {
                        textView.text = user.getString("username")
                    } else if(user.has("email")) {
                        textView.text = user.getString("email")
                    }
                }

                textView.text = label + ": " + textView.text
            }
        }
    }

    override fun getViewHolderLayout(): Int {
        return R.layout.message_item
    }

    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }
}