package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

/**
 * Fix for a bug in [FrameLayout.onMeasure] that causes wrong dimensions of child with
 * [FrameLayout.LayoutParams.MATCH_PARENT] size.
 */
internal open class FrameLayoutFix @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val foregroundPadding = Rect()

    private val paddingLeftWithForeground: Int
        get() = max(paddingLeft, foregroundPadding.left)

    private val paddingRightWithForeground: Int
        get() = max(paddingRight, foregroundPadding.right)

    private val paddingTopWithForeground: Int
        get() = max(paddingTop, foregroundPadding.top)

    private val paddingBottomWithForeground: Int
        get() = max(paddingBottom, foregroundPadding.bottom)


    override fun setForegroundGravity(gravity: Int) {
        super.setForegroundGravity(gravity)

        if (foregroundGravity == Gravity.FILL && foreground != null) {
            foreground.getPadding(foregroundPadding)
        } else {
            foregroundPadding.setEmpty()
        }
    }

    private fun debugLog(message: String) = android.util.Log.d("FrameLayout", message)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measureMatchParentChildren = widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY
        val matchParentChildren = mutableListOf<View>()

        var maxWidth = 0
        var maxHeight = 0
        var childState = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (measureAllChildren || child.visibility != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val lp = child.layoutParams as LayoutParams
                val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
                val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
                childState = combineMeasuredStates(childState, child.measuredState)
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT || lp.height == LayoutParams.MATCH_PARENT) {
                        matchParentChildren += child
                    }
                }
                maxWidth = when {
                    childCount == 1 -> childWidth
                    lp.width == LayoutParams.MATCH_PARENT -> maxWidth  // ignore match_parent child width
                    else -> max(maxWidth, childWidth)
                }
                maxHeight = when {
                    childCount == 1 -> childHeight
                    lp.height == LayoutParams.MATCH_PARENT -> maxHeight  // ignore match_parent child height
                    else -> max(maxHeight, childHeight)
                }
            }
        }

        val horizontalPadding = paddingLeftWithForeground + paddingRightWithForeground
        val verticalPadding = paddingTopWithForeground + paddingBottomWithForeground
        maxWidth += horizontalPadding
        maxHeight += verticalPadding

        maxWidth = maxWidth.coerceAtLeast(suggestedMinimumWidth)
        maxHeight = maxHeight.coerceAtLeast(suggestedMinimumHeight)

        foreground?.run {
            maxWidth = maxWidth.coerceAtLeast(minimumWidth)
            maxHeight = maxHeight.coerceAtLeast(minimumHeight)
        }

        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl MEASURED_HEIGHT_STATE_SHIFT)
        )

        if (childCount > 1) {
            for (i in 0 until matchParentChildren.size) {
                val child = matchParentChildren[i]
                val lp = child.layoutParams as LayoutParams
                val childHorizontalPadding = horizontalPadding + lp.leftMargin + lp.rightMargin
                val childVerticalPadding = verticalPadding + lp.topMargin + lp.bottomMargin

                val childWidthMeasureSpec = if (lp.width == LayoutParams.MATCH_PARENT) {
                    val width = (measuredWidth - childHorizontalPadding)
                        .coerceAtLeast(min(child.measuredWidth, maxWidth))
                    MeasureSpec.makeMeasureSpec(width.coerceAtLeast(0), MeasureSpec.EXACTLY)
                } else {
                    getChildMeasureSpec(widthMeasureSpec, childHorizontalPadding, lp.width)
                }

                val childHeightMeasureSpec = if (lp.height == LayoutParams.MATCH_PARENT) {
                    val height = (measuredHeight - childVerticalPadding)
                        .coerceAtLeast(min(child.measuredHeight, maxHeight))
                    MeasureSpec.makeMeasureSpec(height.coerceAtLeast(0), MeasureSpec.EXACTLY)
                } else {
                    getChildMeasureSpec(heightMeasureSpec, childVerticalPadding, lp.height)
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }
}
