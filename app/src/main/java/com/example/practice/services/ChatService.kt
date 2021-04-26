package com.example.practice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception
import com.google.firebase.Timestamp
import java.util.*

typealias MessagesType = LinkedList<JSONObject>
typealias OnGetMessagesListener = (chats: ListingsType?, e: Exception?) -> Unit
typealias OnSaveMessageListener = (e: Exception?) -> Unit
typealias OnRemoveMessageListener = (e: Exception?) -> Unit

class ChatService {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val collHead = "messages_head"
    private val collMessages = "messages"

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
            chat.put("sendAt", doc.data?.get("sendAt"))
            collection.add(chat)
        }

        return collection
    }

    fun saveForCurrentUser(message : JSONObject, onSaveMessageListener: OnSaveMessageListener? = null)
    {
        val user = auth.currentUser

        if(user == null)  {
            if(onSaveMessageListener != null)
                onSaveMessageListener(UserNotFound())
            return
        }

        if(!message.has("to")) {
            if (onSaveMessageListener != null)
                onSaveMessageListener(Exception("Message to is not defined"))
            return
        }

        if(!message.has("message")) {
            if(onSaveMessageListener != null)
                onSaveMessageListener(Exception("Message content is not defined"))
            return
        }

        if(message.getString("message").isEmpty()) {
            if(onSaveMessageListener != null)
                onSaveMessageListener(Exception("Message content is empty"))
            return
        }

        message.put("from", user.uid)

        val map = ObjectMapper().readValue(message.toString(), HashMap::class.java) as HashMap<String, Any>
        map["sendAt"] = Timestamp(Calendar.getInstance().time)

        db.collection(collMessages)
                .add(map)
                .addOnSuccessListener {
                    if (onSaveMessageListener != null)
                        onSaveMessageListener(null)
                }
                .addOnFailureListener {
                    if (onSaveMessageListener != null)
                        onSaveMessageListener(it)
                }

        // TODO: Update messages head collection
    }

    fun getForCurrentUser(onGetMessagesListener: OnGetMessagesListener) {
        val user = auth.currentUser

        if(user == null) {
            onGetMessagesListener(null, UserNotFound())
            return
        }

        db.collection(collMessages)
                .whereEqualTo("from", user.uid)
                .get()
                .addOnSuccessListener { from ->
                    val messages = parseDocumentsToCollection(from.documents)
                    db.collection(collMessages)
                            .whereEqualTo("to", user.uid)
                            .get()
                            .addOnSuccessListener { to ->
                                messages.addAll(parseDocumentsToCollection(to.documents))
                                messages.sortBy { it.get("sendAt") as Timestamp }

                                onGetMessagesListener(messages, null)
                            }
                            .addOnFailureListener { e ->
                                onGetMessagesListener(null, e)
                            }
                }
                .addOnFailureListener { e ->
                    onGetMessagesListener(null, e)
                }
    }

    fun getHeadForCurrentUser(onGetMessagesListener: OnGetMessagesListener) {
        val user = auth.currentUser

        if(user == null) {
            onGetMessagesListener(null, UserNotFound())
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
                        onGetMessagesListener(messages, null)
                    }
                    .addOnFailureListener { e ->
                        onGetMessagesListener(null, e)
                    }
            }
            .addOnFailureListener { e ->
                onGetMessagesListener(null, e)
            }
    }

    fun getCurrentUserFromMessage(message: JSONObject) : String? {
        val user = auth.currentUser ?: return null

        if(message.has("from")) {
            val from = message.getString("from")
            if(from.equals(user.uid)) return from
        }

        if(message.has("to")) {
            val to = message.getString("to")
            if(to.equals(user.uid)) return to
        }

        return null
    }

    fun getOtherUserFromMessage(message: JSONObject) : String? {
        val user = auth.currentUser ?: return null

        if(message.has("from")) {
            val from = message.getString("from")
            if(!from.equals(user.uid)) return from
        }

        if(message.has("to")) {
            val to = message.getString("to")
            if(!to.equals(user.uid)) return to
        }

        return null
    }
}