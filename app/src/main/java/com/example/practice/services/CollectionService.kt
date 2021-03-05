package com.example.practice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

typealias CollectionType = LinkedList<JSONObject>
typealias OnGetCollectionListener = (collection: CollectionType?, e: Exception?) -> Unit
typealias OnSaveAlbumListener = (id: String?, e: Exception?) -> Unit
typealias OnRemoveAlbumListener = (e: Exception?) -> Unit

/**
 * CollectionService class
 */
class CollectionService {
    private val db = Firebase.firestore

    /**
     * UserNotFound inner class
     */
    inner class UserNotFound : Exception("User was not found") {}

    /**
     * Parse documents to collection
     * @param documents
     */
    private fun parseCollectionDocument(documents: MutableList<DocumentSnapshot>): CollectionType {
        val collection = CollectionType()

        for (doc in documents) {
            var album = JSONObject(doc.data)
            album.put("id", doc.id)

            collection.add(album)
        }

        return collection
    }

    /**
     * Get album for user
     * @param user
     * @param onGetCollectionListener
     */
    fun get(user: FirebaseUser?,
            onGetCollectionListener: OnGetCollectionListener) {
        if (user == null) {
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

    /**
     * Save album for user
     * @param user
     * @param album
     * @param onSaveAlbumListener
     */
    fun save(user: FirebaseUser?,
             album: JSONObject,
             onSaveAlbumListener: OnSaveAlbumListener? = null) {
        if (user == null) {
            onSaveAlbumListener?.invoke(null, UserNotFound())
            return
        }

        val map = ObjectMapper().readValue(album.toString(), HashMap::class.java) as HashMap<String, Any>
        map["user"] = user.uid

        db.collection("collections")
                .add(map)
                .addOnSuccessListener {
                    onSaveAlbumListener?.invoke(it.id, null)
                }
                .addOnFailureListener { e ->
                    onSaveAlbumListener?.invoke(null, e)
                }
    }

    /**
     * Remove album
     * @param id
     * @param onRemoveAlbumListener
     */
    fun remove(id: String, onRemoveAlbumListener: OnRemoveAlbumListener? = null) {
        db.collection("collections")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    onRemoveAlbumListener?.invoke(null)
                }
                .addOnFailureListener { e ->
                    onRemoveAlbumListener?.invoke(e)
                }
    }

    /**
     * Remove album
     * @param album
     * @param onRemoveAlbumListener
     */
    fun remove(album: JSONObject, onRemoveAlbumListener: OnRemoveAlbumListener? = null) {
        if (album.has("id")) remove(album.getString("id"), onRemoveAlbumListener)
    }

    /**
     * Get album for current user
     * @param onGetCollectionListener
     */
    fun getForCurrentUser(onGetCollectionListener: OnGetCollectionListener) {
        get(FirebaseAuth.getInstance().currentUser, onGetCollectionListener)
    }

    /**
     * Save album for current user
     * @param album
     * @param onSaveAlbumListener
     */
    fun saveForCurrentUser(album: JSONObject, onSaveAlbumListener: OnSaveAlbumListener? = null) {
        save(FirebaseAuth.getInstance().currentUser, album, onSaveAlbumListener)
    }
}