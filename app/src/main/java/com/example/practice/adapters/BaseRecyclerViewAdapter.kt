package com.example.practice.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.R
import org.json.JSONObject
import java.util.*

abstract class BaseRecyclerViewAdapter<T : BaseViewHolder>(
        private var context: Context,
        var collection: LinkedList<JSONObject> = LinkedList()
) : RecyclerView.Adapter<T>(), ItemTouchHelperAdapter {
    var onItemClickListener: OnItemClickListener? = null
    var onSwipedListener: OnItemSwipedListener? = null
    private var lastPosition = -1

    /**
     * @return Instance of view holder
     */
    abstract fun createViewHolder(view : View) : T

    /**
     * @return View holder layout id
     */
    abstract fun getViewHolderLayout() : Int

    /**
     * Create view holder
     * @param parent
     * @param viewType
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val view = LayoutInflater.from(parent.context)
                .inflate(getViewHolderLayout(), parent, false)

        val viewHolder = createViewHolder(view)
        viewHolder.setOnClickListener{ position: Int, _: Int ->
           onItemClickListener?.onItemClick(get(position))
        }

        return viewHolder
    }

    /**
     * Bind view holder listener
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: T, position: Int) {
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