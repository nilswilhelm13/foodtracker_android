package net.nilswilhelm.foodtracker.utils

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemCLickListener(
    context: Context,
    recyclerView: RecyclerView,
    private val listener: OnRecyclerClickListener
) : RecyclerView.SimpleOnItemTouchListener() {
    private val TAG = "RecyclerItemCLickListen"

    interface OnRecyclerClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    // add the gestureDetector
    private val gestureDetector =
        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                listener.onItemClick(childView!!, recyclerView.getChildAdapterPosition(childView))
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                listener.onItemLongClick(childView!!, recyclerView.getChildAdapterPosition(childView))
                super.onLongPress(e)
            }
        })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        Log.d(TAG, ".onInterceptTouchEvent: starts $e")
        val result = gestureDetector.onTouchEvent(e)
        Log.d(TAG, ".onInterceptTouchEvent() returning: $result")
//        return super.onInterceptTouchEvent(rv, e)
        return result
    }
}