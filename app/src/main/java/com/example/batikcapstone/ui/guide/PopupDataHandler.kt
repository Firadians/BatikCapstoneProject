package com.example.batikcapstone.ui.guide

import android.content.Context
import android.view.LayoutInflater
import com.example.batikcapstone.R

class PopupDataHandler(private val context: Context) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val contents = arrayOf(
        context.getString(R.string.guide_first),
        context.getString(R.string.guide_second),
        context.getString(R.string.guide_third),
        context.getString(R.string.guide_fourth),
        context.getString(R.string.guide_fifth),
        context.getString(R.string.guide_sixth),
        context.getString(R.string.guide_seventh),
        context.getString(R.string.guide_last)
    )
    val images = arrayOf(
        R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3, R.drawable.guide_4,
        R.drawable.guide_5, R.drawable.guide_6, R.drawable.guide_7, R.drawable.app_name
    )
    var currentIndex = 0

    fun getCurrentContent(): String {
        return contents[currentIndex]
    }

    fun getCurrentImage(): Int {
        return images[currentIndex]!!
    }

    fun getCurrentPageCount(): Int {
        return currentIndex + 1
    }

    fun getTotalPageCount(): Int {
        return contents.size
    }

    fun getNextContent(): String {
        currentIndex++
        if (currentIndex < contents.size) {
            return contents[currentIndex]
        } else {
            // Reset index if reached the end of the content array
            currentIndex = 0
            return contents[currentIndex]
        }
    }

    fun getInflater(): LayoutInflater {
        return inflater
    }
}