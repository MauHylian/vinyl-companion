package com.example.practice.adapters

/**
 * ItemTouchHelperAdapter interface
 */
interface ItemTouchHelperAdapter {
    /**
     * On move
     * @param from
     * @param to
     */
    fun onMove(from: Int, to: Int): Boolean

    /**
     * On swiped
     * @param position
     */
    fun onSwiped(position: Int)
}