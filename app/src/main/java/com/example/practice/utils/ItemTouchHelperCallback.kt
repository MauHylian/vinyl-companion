package com.example.practice.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.adapters.ItemTouchHelperAdapter

/**
 * ItemTouchHelperCallback class
 * @param adapter
 */
class ItemTouchHelperCallback(private var adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    /**
     * Get movement flags
     * @param recyclerView
     * @param viewHolder
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags: Int = ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * On move
     * @param recyclerView
     * @param viewHolder
     * @param target
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return adapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    /**
     * On swiped
     * @param viewHolder
     * @param direction
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onSwiped(viewHolder.adapterPosition)
    }

    /**
     * Enable long press drag
     */
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    /**
     * Enable swiping from touch events that start anywhere within the view
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
}