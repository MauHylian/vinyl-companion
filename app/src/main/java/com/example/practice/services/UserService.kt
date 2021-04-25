package com.example.practice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

typealias OnSaveUserListener = (e: Exception?) -> Unit

class UserService {
    private val db = Firebase.firestore
    private val coll = "users"

    inner class UserNotFound : java.lang.Exception("User was not found") {}

    fun get(id : String, onGetListener: (user: JSONObject?, e : Exception?) -> Unit)
    {
        db.collection(coll)
            .document(id)
            .get()
            .addOnSuccessListener {
                onGetListener(JSONObject(it.data), null)
            }
            .addOnFailureListener { e ->
                onGetListener(null, e)
            }
    }

    fun save(user : JSONObject, onSaveListener: OnSaveUserListener? = null)
    {
        if(!user.has("id")) {
            if(onSaveListener != null) onSaveListener(UserNotFound())
            return
        }

        val id = user.getString("id")
        val map = ObjectMapper().readValue(user.toString(), HashMap::class.java) as HashMap<String, Any>

        db.collection(coll)
            .document(id)
            .set(map)
            .addOnSuccessListener {
                if(onSaveListener != null) onSaveListener(null)
            }
            .addOnFailureListener {
                if (onSaveListener != null) onSaveListener(it)
            }
    }

    fun saveCurrentUser(onSaveListener: OnSaveUserListener? = null)
    {
        val currUser = FirebaseAuth.getInstance().currentUser
        if(currUser == null) {
            if(onSaveListener != null) onSaveListener(UserNotFound())
            return
        }

        val user = JSONObject()
        user.put("id", currUser.uid)

        val email = currUser.email
        if(email != null) {
            user.put("email", email)

            val end = email.indexOf('@')
            if(end != -1) {
                val username = email.substring(0, end)
                user.put("username", username)
            }
        }

        save(user, onSaveListener)
    }
}