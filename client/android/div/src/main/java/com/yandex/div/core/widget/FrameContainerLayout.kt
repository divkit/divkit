package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min

internal open class FrameContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val foregroundPadding = Rect()

    private val paddingLeftWithForeground: Int
        get() = max(paddingLeft, foregroundPadding.left)

    private val paddingRightWithForeground: Int
        get() = max(paddingRight, foregroundPadding.right)

    private val paddingTopWithForeground: Int
        get() = max(paddingTop, foregroundPadding.top)

    private val paddingBottomWithForeground: Int
        get() = max(paddingBottom, foregroundPadding.bottom)

    var measureAllChildren = false

    override fun setForegroundGravity(gravity: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || foregroundGravity == gravity) return
        super.setForegroundGravity(gravity)
        if (foregroundGravity == Gravity.FILL && foreground != null) {
            foreground.getPadding(foregroundPadding)
        } else {
            foregroundPadding.setEmpty()
        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measureMatchParentChildren = widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY
        val matchParentChildren = mutableListOf<View>()

        var maxWidth = 0
        var maxHeight = 0
        var childState = 0

        forEach (!measureAllChildren) { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = child.layoutParams as DivLayoutParams
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
                widthMode == MeasureSpec.UNSPECIFIED -> max(maxWidth, childWidth)
                lp.width == LayoutParams.MATCH_PARENT -> maxWidth  // ignore match_parent child width
                else -> max(maxWidth, childWidth)
            }
            maxHeight = when {
                childCount == 1 -> childHeight
                heightMode == MeasureSpec.UNSPECIFIED -> max(maxHeight, childHeight)
                lp.height == LayoutParams.MATCH_PARENT -> maxHeight  // ignore match_parent child height
                else -> max(maxHeight, childHeight)
            }
        }

        val horizontalPadding = paddingLeftWithForeground + paddingRightWithForeground
        val verticalPadding = paddingTopWithForeground + paddingBottomWithForeground
        maxWidth += horizontalPadding
        maxHeight += verticalPadding

        maxWidth = maxWidth.coerceAtLeast(suggestedMinimumWidth)
        maxHeight = maxHeight.coerceAtLeast(suggestedMinimumHeight)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground?.run {
                maxWidth = maxWidth.coerceAtLeast(minimumWidth)
                maxHeight = maxHeight.coerceAtLeast(minimumHeight)
            }
        }

        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl MEASURED_HEIGHT_STATE_SHIFT)
        )

        if (childCount > 1) {
            for (i in 0 until matchParentChildren.size) {
                val child = matchParentChildren[i]
                val lp = child.layoutParams as DivLayoutParams
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChildren(left, top, right, bottom)
    }

    private fun layoutChildren(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val parentLeft = paddingLeftWithForeground
        val parentRight = right - left - paddingRightWithForeground
        val parentTop = paddingTopWithForeground
        val parentBottom = bottom - top - paddingBottomWithForeground
        forEach(significantOnly = true) { child ->
            val lp = child.layoutParams as DivLayoutParams
            val width = child.measuredWidth
            val height = child.measuredHeight
            val gravity: Int = lp.gravity
            val layoutDirection = layoutDirection
            val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
            val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
            val childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.CENTER_HORIZONTAL -> parentLeft + (parentRight - parentLeft - width) / 2 +
                        lp.leftMargin - lp.rightMargin
                Gravity.RIGHT -> parentRight - width - lp.rightMargin
                else -> parentLeft + lp.leftMargin
            }
            val childTop = when (verticalGravity) {
                Gravity.CENTER_VERTICAL -> parentTop + (parentBottom - parentTop - height) / 2 +
                        lp.topMargin - lp.bottomMargin
                Gravity.BOTTOM -> parentBottom - height - lp.bottomMargin
                else -> parentTop + lp.topMargin
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height)
        }
    }

    override fun shouldDelayChildPressedState(): Boolean = false

    override fun checkLayoutParams(p: LayoutParams?): Boolean = p is DivLayoutParams

    override fun generateLayoutParams(attrs: AttributeSet?) = DivLayoutParams(context, attrs)

    override fun generateLayoutParams(lp: LayoutParams?) =
        when (lp) {
            is DivLayoutParams -> DivLayoutParams(lp)
            is MarginLayoutParams -> DivLayoutParams(lp)
            else -> DivLayoutParams(lp)
        }

    override fun generateDefaultLayoutParams() = DivLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
}
