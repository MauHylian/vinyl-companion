package com.example.practice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception
import java.security.acl.AclNotFoundException
import java.util.*
import kotlin.collections.HashMap

typealias CollectionType = LinkedList<JSONObject>
typealias OnGetCollectionListener = (collection : CollectionType?, e : Exception?) -> Unit
typealias OnSaveAlbumListener = (e : Exception?) -> Unit

class CollectionService {
    private val db = Firebase.firestore

    inner class UserNotFound : Exception("User was not found") {}

    private fun parseCollectionDocument(documents : MutableList<DocumentSnapshot>) : CollectionType {
        val collection = CollectionType()

        for(doc in documents)
            collection.add(JSONObject(doc.data))

        return collection
    }

    fun get(user: FirebaseUser?,
            onGetCollectionListener: OnGetCollectionListener)
    {
        if(user == null) {
            onGetCollectionListener(null, UserNotFound())
            return
        }

        db.collection("collections")
                .whereEqualTo("user", user.uid)
                .get()
                .addOnSuccessListener {
                    onGetCollectionListener(parseCollectionDocument((it.documents)), null)
                }
                .addOnFailureListener { e ->
                    onGetCollectionListener(null, e)
                }
    }

    fun saveAlbum(user : FirebaseUser?,
                  album : JSONObject,
                  onSaveAlbumListener: OnSaveAlbumListener? = null)
    {
        if(user == null) {
            onSaveAlbumListener?.invoke(UserNotFound())
            return
        }

        val map = ObjectMapper().readValue(album.toString(), HashMap::class.java) as HashMap<String, Any>
        map["user"] = user.uid

        db.collection("collections")
            .add(map)
            .addOnSuccessListener {
                onSaveAlbumListener?.invoke(null)
            }
            .addOnFailureListener { e ->
                onSaveAlbumListener?.invoke(e)
            }
    }

    fun getForCurrentUser(onGetCollectionListener: OnGetCollectionListener) {
        get(FirebaseAuth.getInstance().currentUser, onGetCollectionListener)
    }

    fun saveAlbumForCurrentUser(album : JSONObject, onSaveAlbumListener: OnSaveAlbumListener? = null) {
        saveAlbum(FirebaseAuth.getInstance().currentUser, album, onSaveAlbumListener)
    }
}