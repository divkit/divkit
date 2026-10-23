package com.yandex.div.core.widget

import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.core.view.isGone
import com.yandex.div.core.util.getIndices
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.roundToInt

import com.yandex.div.internal.widget.DivViewGroup.Companion.lp
import com.yandex.div.internal.widget.DivViewGroup.Companion.toHorizontalGravity
import com.yandex.div.internal.widget.DivViewGroup.Companion.toVerticalGravity

internal class LinearContainerChildLayout(
    private val container: LinearContainerLayout,
    private val offsetsHolder: DivViewGroup.OffsetsHolder,
    private val dividers: LinearContainerDividers,
) {
    fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int, totalLength: Int): Unit = with(container) {
        val childSpace = right - left - horizontalPaddings
        val freeSpace = (bottom - top - totalLength).toFloat()
        var childTop = paddingTop.toFloat()
        offsetsHolder.update(freeSpace, verticalGravity, visibleChildCount)
        childTop += offsetsHolder.firstChildOffset
        forEachIndexed(significantOnly = true) { child, i ->
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val lp = child.lp
            var gravity = lp.gravity.toHorizontalGravity()
            if (gravity < 0) {
                gravity = horizontalGravity
            }
            val layoutDirection = getLayoutDirection()
            val childLeft = paddingLeft + when (GravityCompat.getAbsoluteGravity(gravity, layoutDirection)) {
                Gravity.LEFT -> lp.leftMargin
                Gravity.CENTER_HORIZONTAL -> (childSpace - childWidth + lp.leftMargin - lp.rightMargin) / 2
                Gravity.RIGHT -> childSpace - childWidth - lp.rightMargin
                else -> lp.leftMargin
            }
            childTop += dividers.gapBeforeChild(i)
            childTop += lp.topMargin
            setChildFrame(child, childLeft, childTop.roundToInt(), childWidth, childHeight)
            childTop += childHeight + lp.bottomMargin + offsetsHolder.spaceBetweenChildren
        }
    }

    private val visibleChildCount get() = container.children.count { !it.isGone }

    fun layoutHorizontal(
        left: Int, top: Int, right: Int, bottom: Int, totalLength: Int, maxBaselineAscent: Int,
    ): Unit = with(container) {
        val childSpace = bottom - top - verticalPaddings
        val layoutDirection = getLayoutDirection()
        val freeSpace = (right - left - totalLength).toFloat()
        var childLeft = paddingLeft.toFloat()
        val absoluteGravity = GravityCompat.getAbsoluteGravity(horizontalGravity, layoutDirection)
        offsetsHolder.update(freeSpace, absoluteGravity, visibleChildCount)
        childLeft += offsetsHolder.firstChildOffset
        for (childIndex in getIndices(0, childCount)) {
            val child = getChildAt(childIndex)
            if (child == null || child.isGone) {
                continue
            }

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val lp = child.lp
            val childTop = resolveChildTop(child, childSpace, maxBaselineAscent)
            val index = if (isLayoutRtl()) {
                childIndex + 1
            } else {
                childIndex
            }
            childLeft += dividers.gapBeforeChild(index)
            childLeft += lp.leftMargin
            setChildFrame(child, childLeft.roundToInt(), childTop, childWidth, childHeight)
            childLeft += childWidth + lp.rightMargin + offsetsHolder.spaceBetweenChildren
        }
    }

    private fun resolveChildTop(child: View, childSpace: Int, maxBaselineAscent: Int): Int = with(container) {
        val lp = child.lp
        val childHeight = child.measuredHeight
        var gravity = lp.gravity.toVerticalGravity()
        if (gravity < 0) {
            gravity = verticalGravity
        }
        return paddingTop + when (gravity) {
            Gravity.TOP -> {
                val considerBaseline = lp.isBaselineAligned && lp.height != MATCH_PARENT && child.baseline != -1
                if (considerBaseline) {
                    maxBaselineAscent - child.baseline
                } else {
                    lp.topMargin
                }
            }
            Gravity.CENTER_VERTICAL -> (childSpace - childHeight + lp.topMargin - lp.bottomMargin) / 2
            Gravity.BOTTOM -> childSpace - childHeight - lp.bottomMargin
            else -> 0
        }
    }

    private fun setChildFrame(child: View, left: Int, top: Int, width: Int, height: Int) =
        child.layout(left, top, left + width, top + height)

}
