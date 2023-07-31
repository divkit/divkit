package com.yandex.div.internal.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.AspectView.Companion.DEFAULT_ASPECT_RATIO
import com.yandex.div.core.widget.dimensionAffecting
import com.yandex.div.core.widget.forEach
import com.yandex.div.core.widget.isExact
import com.yandex.div.core.widget.isUnspecified
import com.yandex.div.core.widget.makeExactSpec
import kotlin.math.max
import kotlin.math.roundToInt

open class FrameContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DivViewGroup(context, attrs, defStyleAttr), AspectView {

    private val foregroundPadding = Rect()

    private val paddingLeftWithForeground: Int
        get() = max(paddingLeft, foregroundPadding.left)

    private val paddingRightWithForeground: Int
        get() = max(paddingRight, foregroundPadding.right)

    private val paddingTopWithForeground: Int
        get() = max(paddingTop, foregroundPadding.top)

    private val paddingBottomWithForeground: Int
        get() = max(paddingBottom, foregroundPadding.bottom)

    @Suppress("MemberVisibilityCanBePrivate")
    var measureAllChildren = false

    private val measuredMatchParentChildren = mutableSetOf<View>()
    private val skippedMatchParentChildren = mutableSetOf<View>()
    private val matchParentChildren = mutableSetOf<View>()

    private var maxWidth = 0
    private var maxHeight = 0
    private var childState = 0

    override var aspectRatio by dimensionAffecting(DEFAULT_ASPECT_RATIO) { it.coerceAtLeast(DEFAULT_ASPECT_RATIO) }

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
        maxWidth = 0
        maxHeight = 0
        childState = 0

        val exactWidth = isExact(widthMeasureSpec)
        var heightSpec = when {
            !useAspect -> heightMeasureSpec
            !exactWidth -> MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            else -> makeExactSpec((MeasureSpec.getSize(widthMeasureSpec) / aspectRatio).roundToInt())
        }

        forEach(!measureAllChildren) { measureChildWithDefinedSize(it, widthMeasureSpec, heightSpec) }

        matchParentChildren += measuredMatchParentChildren
        matchParentChildren += skippedMatchParentChildren

        considerMatchParentChildrenInMaxSize(widthMeasureSpec, heightSpec)

        val widthSizeAndState = resolveSizeAndState(getDynamicWidth(widthMeasureSpec), widthMeasureSpec, childState)
        val heightSize = getDynamicHeight(widthMeasureSpec, heightSpec, widthSizeAndState and MEASURED_SIZE_MASK)
        if (isUnspecified(heightSpec)) {
            heightSpec = makeExactSpec(heightSize)
            remeasureWrapContentConstrainedChildren(widthMeasureSpec, heightSpec)
        }

        setMeasuredDimension(
            widthSizeAndState,
            resolveSizeAndState(heightSize, heightSpec, childState shl MEASURED_HEIGHT_STATE_SHIFT)
        )

        matchParentChildren.forEach { remeasureMatchParentChild(it, widthMeasureSpec, heightSpec) }

        measuredMatchParentChildren.clear()
        skippedMatchParentChildren.clear()
        matchParentChildren.clear()
    }

    private val useAspect get() = aspectRatio != DEFAULT_ASPECT_RATIO

    private fun measureChildWithDefinedSize(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        val exactWidth = isExact(widthMeasureSpec)
        val exactHeight = isExact(heightMeasureSpec)
        val matchParentWidth = lp.width == MATCH_PARENT
        val matchParentHeight = lp.height == MATCH_PARENT
        val hasDefinedSize = when {
            exactWidth && exactHeight -> true
            exactHeight -> !matchParentWidth
            exactWidth -> !matchParentHeight
            !matchParentWidth -> true
            matchParentHeight -> false
            lp.height == DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> !useAspect
            else -> true
        }
        if (!hasDefinedSize) {
            if (lp.matchDynamicSize(exactWidth, exactHeight)) {
                skippedMatchParentChildren += child
            }
            return
        }

        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        childState = combineMeasuredStates(childState, child.measuredState)
        if (lp.matchDynamicSize(exactWidth, exactHeight)) {
            measuredMatchParentChildren += child
        }

        if (!exactWidth && !matchParentWidth) {
            updateMaxWidth(child.measuredWidth + lp.horizontalMargins)
        }
        if (!exactHeight && !matchParentHeight && !useAspect) {
            updateMaxHeight(child.measuredHeight + lp.verticalMargins)
        }
    }

    private fun DivLayoutParams.matchDynamicSize(exactWidth: Boolean, exactHeight: Boolean) =
        matchDynamicWidth(exactWidth) || matchDynamicHeight(exactHeight)

    private fun DivLayoutParams.matchDynamicWidth(exactWidth: Boolean) = !exactWidth && width == MATCH_PARENT

    private fun DivLayoutParams.matchDynamicHeight(exactHeight: Boolean) = !exactHeight && height == MATCH_PARENT

    private fun updateMaxWidth(childWidth: Int) {
        maxWidth = max(maxWidth, childWidth)
    }

    private fun updateMaxHeight(childHeight: Int) {
        maxHeight = max(maxHeight, childHeight)
    }

    private fun considerMatchParentChildrenInMaxSize(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (matchParentChildren.isEmpty()) return

        val exactWidth = isExact(widthMeasureSpec)
        val exactHeight = isExact(heightMeasureSpec)
        if (exactWidth && exactHeight) return

        val needMeasureWidth = !exactWidth && maxWidth == 0
        val needMeasureHeight = !exactHeight && !useAspect && maxHeight == 0
        if (!needMeasureWidth && !needMeasureHeight) {
            matchParentChildren.forEach { considerMatchParentMargins(it, exactWidth, exactHeight) }
            return
        }

        matchParentChildren.forEach { child ->
            val lp = child.lp
            if (skippedMatchParentChildren.contains(child) &&
                ((lp.width == MATCH_PARENT && needMeasureWidth) ||
                    (lp.height == MATCH_PARENT && needMeasureHeight))) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                childState = combineMeasuredStates(childState, child.measuredState)
                skippedMatchParentChildren -= child
            }
            if (needMeasureWidth) {
                updateMaxWidth(child.measuredWidth + lp.horizontalMargins)
            }
            if (needMeasureHeight) {
                updateMaxHeight(child.measuredHeight + lp.verticalMargins)
            }
        }
    }

    private fun considerMatchParentMargins(child: View, exactWidth: Boolean, exactHeight: Boolean) {
        val lp = child.lp
        if (lp.matchDynamicWidth(exactWidth)) {
            updateMaxWidth(lp.horizontalMargins)
        }
        if (lp.matchDynamicHeight(exactHeight)) {
            updateMaxHeight(lp.verticalMargins)
        }
    }

    private fun getDynamicWidth(widthMeasureSpec: Int): Int {
        if (isExact(widthMeasureSpec)) return 0

        var widthSize = maxWidth + horizontalPadding
        widthSize = widthSize.coerceAtLeast(suggestedMinimumWidth)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return widthSize
        return foreground?.let { widthSize.coerceAtLeast(it.minimumWidth) } ?: widthSize
    }

    private val horizontalPadding get() = paddingLeftWithForeground + paddingRightWithForeground

    private fun getDynamicHeight(widthMeasureSpec: Int, heightMeasureSpec: Int, widthSize: Int): Int {
        if (isExact(heightMeasureSpec)) return 0

        if (isDynamicAspect(widthMeasureSpec)) {
            return (widthSize / aspectRatio).roundToInt()
        }

        var heightSize = maxHeight + verticalPadding
        heightSize = heightSize.coerceAtLeast(suggestedMinimumHeight)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return heightSize
        return foreground?.let { heightSize.coerceAtLeast(it.minimumHeight) } ?: heightSize
    }

    private fun isDynamicAspect(widthMeasureSpec: Int) = useAspect && !isExact(widthMeasureSpec)

    private val verticalPadding get() = paddingTopWithForeground + paddingBottomWithForeground

    private fun remeasureWrapContentConstrainedChildren(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isDynamicAspect(widthMeasureSpec)) return
        forEach(!measureAllChildren) { remeasureWrapContentConstrainedChild(it, widthMeasureSpec, heightMeasureSpec) }
    }

    private fun remeasureWrapContentConstrainedChild(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (child.lp.height == DivLayoutParams.WRAP_CONTENT_CONSTRAINED) {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            matchParentChildren -= child
        }
    }

    private fun remeasureMatchParentChild(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        val childHorizontalPadding = horizontalPadding + lp.horizontalMargins
        val childVerticalPadding = verticalPadding + lp.verticalMargins

        val childWidthMeasureSpec = if (lp.width == MATCH_PARENT) {
            makeExactSpec((measuredWidth - childHorizontalPadding).coerceAtLeast(0))
        } else {
            getChildMeasureSpec(widthMeasureSpec, childHorizontalPadding,
                lp.width, child.minimumWidth, lp.maxWidth)
        }

        val childHeightMeasureSpec = if (lp.height == MATCH_PARENT) {
            makeExactSpec((measuredHeight - childVerticalPadding).coerceAtLeast(0))
        } else {
            getChildMeasureSpec(heightMeasureSpec, childVerticalPadding,
                lp.height, child.minimumHeight, lp.maxHeight)
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        if (skippedMatchParentChildren.contains(child)) {
            childState = combineMeasuredStates(childState, child.measuredState)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChildren(left, top, right, bottom)
    }

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val parentLeft = paddingLeftWithForeground
        val parentRight = right - left - paddingRightWithForeground
        val parentTop = paddingTopWithForeground
        val parentBottom = bottom - top - paddingBottomWithForeground
        forEach(significantOnly = true) { child ->
            val lp = child.lp
            val width = child.measuredWidth
            val height = child.measuredHeight
            val absoluteGravity = Gravity.getAbsoluteGravity(lp.gravity, layoutDirection)
            val verticalGravity = lp.gravity and Gravity.VERTICAL_GRAVITY_MASK
            val childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.CENTER_HORIZONTAL -> {
                    parentLeft + (parentRight - parentLeft - width + lp.leftMargin - lp.rightMargin) / 2
                }
                Gravity.RIGHT -> parentRight - width - lp.rightMargin
                else -> parentLeft + lp.leftMargin
            }
            val childTop = when (verticalGravity) {
                Gravity.CENTER_VERTICAL -> {
                    parentTop + (parentBottom - parentTop - height + lp.topMargin - lp.bottomMargin) / 2
                }
                Gravity.BOTTOM -> parentBottom - height - lp.bottomMargin
                else -> parentTop + lp.topMargin
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height)
        }
    }

    override fun shouldDelayChildPressedState(): Boolean = false

    override fun generateDefaultLayoutParams(): LayoutParams = DivLayoutParams(MATCH_PARENT, MATCH_PARENT)
}
