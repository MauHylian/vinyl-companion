package com.example.practice.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception
import java.util.*

typealias ChatsType = LinkedList<JSONObject>
typealias OnGetChatsListener = (chats: ListingsType?, e: Exception?) -> Unit
typealias OnSaveChatListener = (id: String?, e: Exception?) -> Unit
typealias OnRemoveChatListener = (e: Exception?) -> Unit

class ChatService {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val collHead = "messages_head"
    private val collMessages = "messages"

    private val userService = UserService()

    /**
     * UserNotFound inner class
     */
    inner class UserNotFound : Exception("User was not found") {}

    /**
     * Parse documents to chats
     * @param documents
     */
    private fun parseDocumentsToCollection(documents: MutableList<DocumentSnapshot>): CollectionType {
        val collection = CollectionType()

        for (doc in documents) {
            val chat = JSONObject(doc.data)
            chat.put("id", doc.id)

            /*
            if(chat.has("from")) {
                val from = chat.getString("from");

                userService.get(from) { fromUser, e ->
                    if(e != null) {
                        Log.e("ChatService", "Failed to get user", e)
                        return@get
                    }

                    chat.put("from", fromUser)
                }
            }

            if(chat.has("to")) {
                val to = chat.getString("to");

                userService.get(to) { toUser, e ->
                    if(e != null) {
                        Log.e("ChatService", "Failed to get user", e)
                        return@get
                    }

                    chat.put("to", toUser)
                }
            }
             */

            collection.add(chat)
        }

        return collection
    }

    fun getHeadForCurrentUser(onGetChatsListener: OnGetChatsListener) {
        val user = auth.currentUser

        if(user == null) {
            onGetChatsListener(null, UserNotFound())
            return
        }

        db.collection(collHead)
            .whereEqualTo("from", user.uid)
            .get()
            .addOnSuccessListener { from ->
                 var messages = parseDocumentsToCollection(from.documents)
                db.collection(collHead)
                    .whereEqualTo("to", user.uid)
                    .get()
                    .addOnSuccessListener { to ->
                        messages.addAll(parseDocumentsToCollection(to.documents))
                        onGetChatsListener(messages, null)
                    }
                    .addOnFailureListener { e ->
                        onGetChatsListener(null, e)
                    }
            }
            .addOnFailureListener { e ->
                onGetChatsListener(null, e)
            }
    }
}