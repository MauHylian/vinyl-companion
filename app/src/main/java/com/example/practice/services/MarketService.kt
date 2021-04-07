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

typealias ListingsType = LinkedList<JSONObject>
typealias OnGetListingsListener = (collection: ListingsType?, e: Exception?) -> Unit
typealias OnSaveListingListener = (id: String?, e: Exception?) -> Unit
typealias OnRemoveListingListener = (e: Exception?) -> Unit

class MarketService {
    private val db = Firebase.firestore
    private val coll = "marketplace"

    /**
     * UserNotFound inner class
     */
    inner class UserNotFound : Exception("User was not found") {}

    inner class NullListingsID : Exception("Listing ID is null") {}

    inner class Unauthorized : Exception("Unauthorized") {}

    /**
     * Parse documents to listings
     * @param documents
     */
    private fun parseDocumentsToCollection(documents: MutableList<DocumentSnapshot>): CollectionType {
        val collection = CollectionType()

        for (doc in documents) {
            var listing = JSONObject(doc.data)
            listing.put("id", doc.id)

            collection.add(listing)
        }

        return collection
    }

    fun get(onGetListingsListener: OnGetListingsListener) {
        db.collection(coll)
                .get()
                .addOnSuccessListener {
                    onGetListingsListener(parseDocumentsToCollection((it.documents)), null)
                }
                .addOnFailureListener { e ->
                    onGetListingsListener(null, e)
                }
    }

    fun save(user: FirebaseUser?,
             listing: JSONObject,
             onSaveListingListener: OnSaveListingListener? = null) {
        if (user == null) {
            onSaveListingListener?.invoke(null, UserNotFound())
            return
        }

        val map = ObjectMapper().readValue(listing.toString(), HashMap::class.java) as HashMap<String, Any>
        map["user"] = user.uid
        listing.put("user", user.uid)

        db.collection(coll)
                .add(map)
                .addOnSuccessListener {
                    listing.put("id", it.id)
                    onSaveListingListener?.invoke(it.id, null)
                }
                .addOnFailureListener { e ->
                    onSaveListingListener?.invoke(null, e)
                }
    }

    fun remove(id: String, onRemoveListingListener: OnRemoveListingListener? = null) {
        db.collection(coll)
                .document(id)
                .delete()
                .addOnSuccessListener {
                    onRemoveListingListener?.invoke(null)
                }
                .addOnFailureListener { e ->
                    onRemoveListingListener?.invoke(e)
                }
    }

    fun remove(listing: JSONObject, onRemoveListingListener: OnRemoveListingListener? = null) {
        if (listing.has("id")) remove(listing.getString("id"), onRemoveListingListener)
        else onRemoveListingListener?.invoke(NullListingsID())
    }

    fun removeForCurrentUser(listing: JSONObject, onRemoveListingListener: OnRemoveListingListener? = null) {
        val user = FirebaseAuth.getInstance().currentUser

        if(!listing.has("user") || user == null) {
            onRemoveListingListener?.invoke(UserNotFound())
            return
        }

        if(listing.getString("user") != user.uid) {
            onRemoveListingListener?.invoke(Unauthorized())
            return
        }

        remove(listing, onRemoveListingListener)
    }

    fun saveForCurrentUser(listing: JSONObject, onSaveListingListener: OnSaveListingListener? = null) {
        save(FirebaseAuth.getInstance().currentUser, listing, onSaveListingListener)
    }
}