package com.example.practice.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import com.example.practice.services.UserService
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.*


/**
 * CollectionRecyclerViewAdapter class
 * @param context
 * @param collection
 */
class ChatListAdapter(
    private var context: Context,
    var collection: LinkedList<JSONObject> = LinkedList()
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>(), ItemTouchHelperAdapter {
    var onItemClickListener: OnItemClickListener? = null
    var onSwipedListener: OnSwipedListener? = null
    private var lastPosition = -1

    /**
     * ViewHolder class
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        // TODO: Create singleton
        private val userService = UserService()

        var textFrom: TextView = view.findViewById(R.id.from)
        var textTo: TextView = view.findViewById(R.id.to)
        var textMessage: TextView = view.findViewById(R.id.message)

        /**
         * Set on click listener
         */
        fun setOnClickListener(event: (position: Int, type: Int) -> Unit): ViewHolder {
            view.setOnClickListener { event.invoke(adapterPosition, itemViewType) }
            return this
        }

        /**
         * Bind album data to views
         */
        @SuppressLint("SetTextI18n")
        fun bind(chat: JSONObject) {
            if(chat.has("from")) {
                bindUser(chat.getString("from"), textFrom, view.context.getString(R.string.from))
            }

            if(chat.has("to")) {
                bindUser(chat.getString("to"), textTo, view.context.getString(R.string.to))
            }

            if(chat.has("message")) {
                textMessage.text = chat.getString("message")
            }
        }

        @SuppressLint("SetTextI18n")
        private fun bindUser(id : String, textView: TextView, label : String)
        {
            userService.get(id) { user, e ->
                textView.text = id

                if(e != null) {
                    Log.e("ChatListAdapter", "Failed to get user", e)
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

    companion object {
        /**
         * OnItemClickListener interface
         */
        interface OnItemClickListener {
            /**
             * On item click
             * @param album
             */
            fun onItemClick(album: JSONObject)
        }

        /**
         * OnSwipedListener interface
         */
        interface OnSwipedListener {
            /**
             * On swiped item
             * @param position
             */
            fun onSwiped(position: Int)
        }
    }

    /**
     * Create view holder
     * @param parent
     * @param viewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)

        return ViewHolder(view).setOnClickListener { position: Int, _: Int ->
            onItemClickListener?.onItemClick(get(position))
        }

    }

    /**
     * Bind view holder listener
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(get(position))

        setAnimation(holder.itemView, position)
    }

    /**
     * Set animation
     * @param viewToAnimate
     * @param position
     */
    private fun setAnimation(viewToAnimate: View, position: Int){
        // If the item wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_right)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    /**
     * Get item count
     */
    override fun getItemCount(): Int {
        return collection.size
    }

    /**
     * Remove all items
     */
    fun removeAll() {
        collection.clear()
        notifyDataSetChanged()
    }

    /**
     * Remove item
     * @param position
     */
    fun remove(position: Int): JSONObject {
        val removed = collection.removeAt(position)

        notifyItemRemoved(position)

        return removed
    }

    /**
     * Add item
     * @param item
     * @param position
     */
    fun add(item: JSONObject, position: Int = -1) {
        var index = position

        if(position >= 0 && position < collection.size) {
            collection.add(position, item);
        } else {
            collection.add(item)
            index = collection.size - 1
        }

        notifyItemInserted(index);
    }

    /**
     * Get item at position
     * @param position
     */
    fun get(position: Int): JSONObject {
        return collection[position]
    }

    /**
     * On move
     * @param from
     * @param to
     */
    override fun onMove(from: Int, to: Int): Boolean {
        var i = from

        if (from < to) {
            while (i < to) {
                Collections.swap(collection, i, i + 1)

                ++i
            }
        } else {
            while (i > to) {
                Collections.swap(collection, i, i - 1)

                --i
            }
        }

        notifyItemMoved(from, to)

        return true
    }

    /**
     * On swiped item
     * @param position
     */
    override fun onSwiped(position: Int) {
        onSwipedListener?.onSwiped(position)
    }
}


