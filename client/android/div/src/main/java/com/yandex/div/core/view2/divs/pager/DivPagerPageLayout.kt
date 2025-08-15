package com.yandex.div.core.view2.divs.pager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.core.view.isVisible
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.core.widget.isUnspecified
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.internal.widget.DivLayoutParams

@SuppressLint("ViewConstructor")
internal class DivPagerPageLayout(
    context: Context,
    private val isHorizontal: () -> Boolean
) : DivViewWrapper(context) {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // When the page cross size is equal to MATCH_PARENT or WRAP_CONTENT_CONSTRAINED,
        // the page must be the size of a pager minus padding,
        // otherwise the page size should be enough for its own content regardless of the current pager height
        if (childCount == 0) return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val lp = getChildAt(0).layoutParams
        val isHorizontal = isHorizontal()
        if (isHorizontal) {
            minimumHeight = getMinimumSize(heightMeasureSpec)
        } else {
            minimumWidth = getMinimumSize(widthMeasureSpec)
        }
        val widthSpec = getSpec(lp.width, widthMeasureSpec, isHorizontal)
        val heightSpec = getSpec(lp.height, heightMeasureSpec, !isHorizontal)
        super.onMeasure(widthSpec, heightSpec)
    }

    private fun getMinimumSize(parentSpec: Int) = if (isUnspecified(parentSpec)) 0 else MeasureSpec.getSize(parentSpec)

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    private fun getSpec(size: Int, parentSpec: Int, alongScrollAxis: Boolean): Int {
        return when {
            alongScrollAxis -> parentSpec
            size == LayoutParams.MATCH_PARENT -> parentSpec
            size == DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> parentSpec
            else -> makeUnspecifiedSpec()
        }
    }
}
