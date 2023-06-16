package com.example.batikcapstone.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.SearchView

class CustomSearchView : SearchView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Treat touch events within the SearchView as clicks
        if (event?.action == MotionEvent.ACTION_UP) {
            // Perform click action
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        // Handle the click action
        // Do any additional actions here if needed

        return super.performClick()
    }
}