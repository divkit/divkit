package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

internal class TabTitleDelimitersController(
    private val context: Context,
    private val indicators: BaseIndicatorTabLayout.OvalIndicators,
) {
    private var tabDelimiterBitmap: Bitmap? = null
    private var tabDelimiterHeight = 0
    private var tabDelimiterWidth = 0
    private val hasTabDelimiters get() = tabDelimiterBitmap != null

    fun tabAdded(index: Int) {
        when {
            !hasTabDelimiters -> return
            indicators.childCount == 1 -> return
            index == 0 -> indicators.addView(createDelimiterView(), 1)
            else -> indicators.addView(createDelimiterView(), index)
        }
    }

    fun tabRemoved(index: Int) {
        when {
            !hasTabDelimiters -> return
            indicators.childCount == 0 -> return
            index == 0 -> indicators.removeViewAt(0)
            else -> indicators.removeViewAt(index - 1)
        }
    }

    fun updateTitleDelimiters(bitmap: Bitmap, width: Int, height: Int) {
        tabDelimiterBitmap = bitmap
        tabDelimiterHeight = height
        tabDelimiterWidth = width

        addDelimiters()
    }

    private fun addDelimiters() {
        removeDelimiters()
        if (!hasTabDelimiters) return
        for (i in 1 until indicators.childCount) {
            indicators.addView(createDelimiterView(), i * 2 - 1)
        }
        indicators.setContainsDelimiters(true)
    }

    private fun removeDelimiters() {
        if (indicators.hasDelimiters()) {
            var i = indicators.childCount - 1
            while (i > 0) {
                indicators.removeViewAt(i)
                i -= 2
            }
        }
        indicators.setContainsDelimiters(false)
    }

    private fun createDelimiterView(): View {
        val view = ImageView(context)
        view.layoutParams = LinearLayout.LayoutParams(tabDelimiterWidth, tabDelimiterHeight).apply {
            gravity = Gravity.CENTER
        }
        view.setImageBitmap(tabDelimiterBitmap)
        return view
    }
}
