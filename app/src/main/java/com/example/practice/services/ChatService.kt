package com.example.practice.services

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

typealias MessagesType = LinkedList<JSONObject>
typealias OnGetMessagesListener = (messages: ListingsType?, e: Exception?) -> Unit
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

    private fun parseDocumentToMessage(doc : DocumentSnapshot) : JSONObject
    {
        val message = JSONObject(doc.data)
        message.put("id", doc.id)
        message.put("sendAt", doc.data?.get("sendAt"))
        return message
    }

    /**
     * Parse documents to messages
     * @param documents
     */
    private fun parseDocumentsToMessages(documents: MutableList<DocumentSnapshot>): MessagesType {
        val collection = CollectionType()

        for (doc in documents) {
            collection.add(parseDocumentToMessage(doc))
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

        val content = message.getString("message").trim()
        message.put("message", content)

        if(content.isEmpty()) {
            if(onSaveMessageListener != null)
                onSaveMessageListener(Exception("Message content is empty"))
            return
        }

        message.put("from", user.uid)

        val map = ObjectMapper().readValue(message.toString(), HashMap::class.java) as HashMap<String, Any>
        map["sendAt"] = Timestamp(Calendar.getInstance().time)

        // Save messages

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

        val from = message.getString("from")
        val to = message.getString("to")

        // Save head

        db.collection(collHead)
                .whereEqualTo("from", from)
                .whereEqualTo("to", to)
                .limit(1)
                .get()
                .addOnSuccessListener { query ->
                    if(query.isEmpty) {
                        db.collection(collHead)
                                .whereEqualTo("from", to)
                                .whereEqualTo("to", from)
                                .limit(1)
                                .get()
                                .addOnSuccessListener { query ->
                                    if(query.isEmpty) {
                                        createMessageHead(map, onSaveMessageListener)
                                    } else {
                                        val id = query.documents[0].id
                                        updateMessageHead(id, map, onSaveMessageListener)
                                    }
                                }
                                .addOnFailureListener {
                                    if(onSaveMessageListener != null)
                                        onSaveMessageListener(it)
                                }
                    } else {
                        val id = query.documents[0].id
                        updateMessageHead(id, map, onSaveMessageListener)
                    }
                }
                .addOnFailureListener {
                    if(onSaveMessageListener != null)
                        onSaveMessageListener(it)
                }
    }

    /**
     * @param other Other user id
     * @param onGetMessagesListener Callback
     */
    fun addNewMessagesListenerForCurrentUser(other: String, onGetMessagesListener: OnGetMessagesListener) {
        val user = auth.currentUser

        if(user == null)  {
            onGetMessagesListener(null, UserNotFound())
            return
        }

        db.collection(collMessages)
                .whereEqualTo("from", user.uid)
                .whereEqualTo("to", other)
                .addSnapshotListener{ snapshots, e ->
                    if(e != null) {
                        onGetMessagesListener(null, e)
                        return@addSnapshotListener
                    }

                    val messages = MessagesType()

                    for(dc in snapshots!!.documentChanges) {
                        val type = dc.type
                        if(type == DocumentChange.Type.ADDED) {
                            val message = parseDocumentToMessage(dc.document)
                            messages.add(message)
                        }
                    }

                    if(!messages.isEmpty())
                        onGetMessagesListener(messages, null)
                }

        db.collection(collMessages)
                .whereEqualTo("from", other)
                .whereEqualTo("to", user.uid)
                .addSnapshotListener{ snapshots, e ->
                    if(e != null) {
                        onGetMessagesListener(null, e)
                        return@addSnapshotListener
                    }

                    val messages = MessagesType()

                    for(dc in snapshots!!.documentChanges) {
                        val type = dc.type
                        if(type == DocumentChange.Type.ADDED) {
                            val message = parseDocumentToMessage(dc.document)
                            messages.add(message)
                        }
                    }

                    if(!messages.isEmpty())
                        onGetMessagesListener(messages, null)
                }
    }

    private fun createMessageHead(data: HashMap<String, Any>, onSaveMessageListener: OnSaveMessageListener? = null)
    {
        db.collection(collHead)
                .add(data)
                .addOnSuccessListener {
                    if(onSaveMessageListener != null)
                        onSaveMessageListener(null)
                }
                .addOnFailureListener {
                    if(onSaveMessageListener != null)
                        onSaveMessageListener(it)
                }
    }

    private fun updateMessageHead(id : String, data : HashMap<String, Any>, onSaveMessageListener: OnSaveMessageListener? = null)
    {
        db.collection(collHead)
                .document(id)
                .set(data)
                .addOnSuccessListener {
                    if(onSaveMessageListener != null)
                        onSaveMessageListener(null)
                }
                .addOnFailureListener {
                    if (onSaveMessageListener != null)
                        onSaveMessageListener(it)
                }
    }

    private fun sortMessages(messages: MessagesType) {
        try {
            messages.sortBy { it.get("sendAt") as Timestamp }
        } catch (e : Exception) {
            Log.e("ChatService", "Failed to sort messages by sendAt", e)
        }
    }

    /**
     * @param other Other user id
     * @param onGetMessagesListener Callback
     */
    fun getForCurrentUser(other : String, onGetMessagesListener: OnGetMessagesListener) {
        val user = auth.currentUser

        if(user == null) {
            onGetMessagesListener(null, UserNotFound())
            return
        }

        db.collection(collMessages)
                .whereEqualTo("from", user.uid)
                .whereEqualTo("to", other)
                .get()
                .addOnSuccessListener { from ->
                    val messages = parseDocumentsToMessages(from.documents)
                    db.collection(collMessages)
                            .whereEqualTo("from", other)
                            .whereEqualTo("to", user.uid)
                            .get()
                            .addOnSuccessListener { to ->
                                messages.addAll(parseDocumentsToMessages(to.documents))
                                sortMessages(messages)

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
                val messages = parseDocumentsToMessages(from.documents)
                db.collection(collHead)
                    .whereEqualTo("to", user.uid)
                    .get()
                    .addOnSuccessListener { to ->
                        messages.addAll(parseDocumentsToMessages(to.documents))
                        sortMessages(messages)

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