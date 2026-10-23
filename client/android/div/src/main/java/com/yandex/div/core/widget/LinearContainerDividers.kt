package com.yandex.div.core.widget

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.core.view.children
import androidx.core.view.isGone
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.max
import kotlin.math.min

import com.yandex.div.internal.widget.DivViewGroup.Companion.lp

internal class LinearContainerDividers(
    private val container: LinearContainerLayout,
    private val offsetsHolder: DivViewGroup.OffsetsHolder,
) {
    private var dividerWidth = 0
    private var dividerHeight = 0

    private var dividerMarginTop = 0
    private var dividerMarginBottom = 0
    private var dividerMarginLeft = 0
    private var dividerMarginRight = 0
    private var itemSpacingPx: Int = 0

    private var firstVisibleChildIndex = -1
    private var lastVisibleChildIndex = -1

    var dividerDrawable: Drawable? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            dividerWidth = value?.intrinsicWidth ?: 0
            dividerHeight = value?.intrinsicHeight ?: 0
            container.setWillNotDraw(value == null)
            container.requestLayout()
        }

    fun setDividerMargins(left: Int, top: Int, right: Int, bottom: Int) {
        dividerMarginLeft = left
        dividerMarginRight = right
        dividerMarginTop = top
        dividerMarginBottom = bottom

        container.requestLayout()
    }

    fun setItemSpacing(px: Int) {
        if (itemSpacingPx != px) {
            itemSpacingPx = px
            container.requestLayout()
        }
    }

    @ShowSeparatorsMode
    var showDividers: Int = 0
        set(value) {
            if (field != value) {
                field = value
                container.requestLayout()
            }
        }

    fun gapBeforeChild(i: Int): Int {
        val dividerWithMargins = if (container.orientation == VERTICAL) {
            dividerHeightWithMargins
        } else {
            dividerWidthWithMargins
        }
        return when {
            hasDividerBeforeChildAt(i) -> dividerWithMargins
            i == firstVisibleChildIndex -> 0
            i > lastVisibleChildIndex -> 0
            else -> itemSpacingPx
        }
    }

    fun draw(canvas: Canvas) {
        dividerDrawable ?: return
        if (container.orientation == VERTICAL) {
            drawDividersVertical(canvas)
        } else {
            drawDividersHorizontal(canvas)
        }
    }

    private fun drawDividersVertical(canvas: Canvas): Unit = with(container) {
        forEachIndexed(significantOnly = true) { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                val top = child.top - child.lp.topMargin - dividerHeight - dividerMarginBottom -
                    getDividerOffsetBeforeChildAt(i)
                drawDividerAt(canvas, top)
            }
        }
        if (hasDividerBeforeChildAt(childCount)) {
            val bottom = getChildAt(childCount - 1)?.let {
                it.bottom + it.lp.bottomMargin + dividerMarginTop + offsetsHolder.edgeDividerOffset
            } ?: (height - paddingBottom - dividerHeight - dividerMarginBottom - offsetsHolder.edgeDividerOffset)
            drawDividerAt(canvas, bottom)
        }
    }

    private fun drawDividersHorizontal(canvas: Canvas): Unit = with(container) {
        val isLayoutRtl = isLayoutRtl()
        forEachIndexed(significantOnly = true) { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                val offset = getDividerOffsetBeforeChildAt(i)
                val position = if (isLayoutRtl) {
                    child.right + child.lp.rightMargin + dividerMarginLeft + offset
                } else {
                    child.left - child.lp.leftMargin - dividerWidth - dividerMarginRight - offset
                }
                drawDividerAt(canvas, position)
            }
        }

        if (hasDividerBeforeChildAt(childCount)) {
            val child = getChildAt(childCount - 1)
            val position = when {
                child == null && isLayoutRtl -> paddingLeft + dividerMarginLeft + offsetsHolder.edgeDividerOffset
                child == null ->
                    width - paddingRight - dividerWidth - dividerMarginRight - offsetsHolder.edgeDividerOffset
                isLayoutRtl -> {
                    child.left - child.lp.leftMargin - dividerWidth - dividerMarginRight -
                        offsetsHolder.edgeDividerOffset
                }
                else -> child.right + child.lp.rightMargin + dividerMarginLeft + offsetsHolder.edgeDividerOffset
            }
            drawDividerAt(canvas, position)
        }
    }

    private fun drawDividerAt(canvas: Canvas, position: Int) = with(container) {
        if (orientation == VERTICAL) {
            drawDivider(canvas, paddingLeft + dividerMarginLeft, position,
                width - paddingRight - dividerMarginRight, position + dividerHeight)
        } else {
            drawDivider(canvas, position, paddingTop + dividerMarginTop,
                position + dividerWidth, height - paddingBottom - dividerMarginBottom)
        }
    }

    private fun drawDivider(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) =
        dividerDrawable?.run {
            val centerHorizontal = (left + right) / 2f
            val centerVertical = (top + bottom) / 2f
            val halfWidth = dividerWidth / 2f
            val halfHeight = dividerHeight / 2f
            setBounds(
                /* left = */ max((centerHorizontal - halfWidth).toInt(), left),
                /* top = */ max((centerVertical - halfHeight).toInt(), top),
                /* right = */ min((centerHorizontal + halfWidth).toInt(), right),
                /* bottom = */ min((centerVertical + halfHeight).toInt(), bottom)
            )
            draw(canvas)
        }

    private fun hasDividerBeforeChildAt(childIndex: Int): Boolean {
        return when {
            childIndex == firstVisibleChildIndex -> showDividers and ShowSeparatorsMode.SHOW_AT_START != 0
            childIndex > lastVisibleChildIndex -> showDividers and ShowSeparatorsMode.SHOW_AT_END != 0
            showDividers and ShowSeparatorsMode.SHOW_BETWEEN != 0 -> {
                for (i in childIndex - 1 downTo 0) {
                    if (!container.getChildAt(i).isGone) {
                        return true
                    }
                }
                false
            }
            else -> false
        }
    }

    private fun getDividerOffsetBeforeChildAt(index: Int) = if (index == firstVisibleChildIndex) {
        offsetsHolder.edgeDividerOffset
    } else {
        (offsetsHolder.spaceBetweenChildren / 2).toInt()
    }

    fun updateVisibleChildren() {
        firstVisibleChildIndex = container.children.indexOfFirst { !it.isGone }
        lastVisibleChildIndex = container.children.indexOfLast { !it.isGone }
    }

    private val dividerHeightWithMargins get() = dividerHeight + dividerMarginTop + dividerMarginBottom
    private val dividerWidthWithMargins get() = dividerWidth + dividerMarginRight + dividerMarginLeft
}
