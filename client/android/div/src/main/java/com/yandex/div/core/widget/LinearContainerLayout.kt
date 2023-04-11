package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.HORIZONTAL
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import com.yandex.div.core.widget.AspectView.Companion.DEFAULT_ASPECT_RATIO
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.WRAP_CONTENT_CONSTRAINED
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.max
import kotlin.math.roundToInt

/** Class name may be obfuscated by Proguard. Hardcode the string for accessibility usage.  */
private const val ACCESSIBILITY_CLASS_NAME = "android.widget.LinearLayout"

@Suppress("unused", "MemberVisibilityCanBePrivate")
internal open class LinearContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DivViewGroup(context, attrs, defStyleAttr), AspectView {

    /**
     * Maximally ascented child that is baseline-aligned.
     * This is used because we don't know child's top pre-layout.
     */
    private var maxBaselineAscent = -1
    private var maxBaselineDescent = -1

    @LinearLayoutCompat.OrientationMode
    var orientation = HORIZONTAL
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private var _gravity = GravityCompat.START or Gravity.TOP

    /**
     * Describes how the child views are positioned. Defaults to GRAVITY_TOP. If
     * this layout has a VERTICAL orientation, this controls where all the child
     * views are placed if there is extra vertical space. If this layout has a
     * HORIZONTAL orientation, this controls the alignment of the children.
     */
    var gravity: Int
        get() = _gravity
        set(value) {
            if (_gravity == value) return

            var newGravity = value
            if ((newGravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                newGravity = newGravity or GravityCompat.START
            }
            if ((newGravity and Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                newGravity = newGravity or Gravity.TOP
            }
            _gravity = newGravity
            requestLayout()
        }

    private var totalLength = 0
    private var totalConstrainedLength = 0
    private var childMeasuredState = 0

    override var aspectRatio by dimensionAffecting(DEFAULT_ASPECT_RATIO) { it.coerceAtLeast(DEFAULT_ASPECT_RATIO) }

    private var dividerWidth = 0
    private var dividerHeight = 0

    var dividerDrawable: Drawable? = null
        set(value) {
            if (field == value) return
            field = value
            dividerWidth = value?.intrinsicWidth ?: 0
            dividerHeight = value?.intrinsicHeight ?: 0
            setWillNotDraw(value == null)
            requestLayout()
        }

    @ShowSeparatorsMode
    var showDividers: Int = 0
        set(value) {
            if (field == value) return
            field = value
            requestLayout()
        }

    var dividerPadding = 0

    private val constrainedChildren = mutableListOf<View>()
    private val skippedMatchParentChildren = mutableSetOf<View>()

    // For horizontal orientation we assume width as main size and height as cross size.
    // For vertical orientation, respectively, height is main size and width is cross size.
    private var maxCrossSize = 0
    // Children which have match_parent size in cross direction.
    private val crossMatchParentChildren = mutableSetOf<View>()
    // Children with match_parent size in main direction can be stretched
    // according to the ratio of their weight to total weight of those children.
    private var totalWeight = 0f

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    override fun onDraw(canvas: Canvas) {
        dividerDrawable ?: return
        if (isVertical) {
            drawDividersVertical(canvas)
        } else {
            drawDividersHorizontal(canvas)
        }
    }

    private fun drawDividersVertical(canvas: Canvas) {
        forEachSignificantIndexed { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                val top = child.top - child.lp.topMargin - dividerHeight
                drawHorizontalDivider(canvas, top)
            }
        }
        if (hasDividerBeforeChildAt(childCount)) {
            val bottom = getChildAt(childCount - 1)?.let {
                it.bottom + it.lp.bottomMargin
            } ?: (height - paddingBottom - dividerHeight)
            drawHorizontalDivider(canvas, bottom)
        }
    }

    private fun drawDividersHorizontal(canvas: Canvas) {
        val isLayoutRtl = isLayoutRtl()
        forEachSignificantIndexed { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                val position = if (isLayoutRtl) {
                    child.right + child.lp.rightMargin
                } else {
                    child.left - child.lp.leftMargin - dividerWidth
                }
                drawVerticalDivider(canvas, position)
            }
        }

        if (hasDividerBeforeChildAt(childCount)) {
            val child = getChildAt(childCount - 1)
            val position = when {
                child == null && isLayoutRtl -> paddingLeft
                child == null -> width - paddingRight - dividerWidth
                isLayoutRtl -> child.left - child.lp.leftMargin - dividerWidth
                else -> child.right + child.lp.rightMargin
            }
            drawVerticalDivider(canvas, position)
        }
    }

    private fun drawHorizontalDivider(canvas: Canvas, top: Int) = drawDivider(
        canvas,
        paddingLeft + dividerPadding,
        top,
        width - paddingRight - dividerPadding,
        top + dividerHeight
    )

    private fun drawVerticalDivider(canvas: Canvas, left: Int) = drawDivider(
        canvas,
        left,
        paddingTop + dividerPadding,
        left + dividerWidth,
        height - paddingBottom - dividerPadding
    )

    private fun drawDivider(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) =
        dividerDrawable?.run {
            val centerHorizontal = (left + right) / 2f
            val centerVertical = (top + bottom) / 2f
            val halfWidth = dividerWidth / 2f
            val halfHeight = dividerHeight / 2f
            setBounds((centerHorizontal - halfWidth).toInt(), (centerVertical - halfHeight).toInt(),
                (centerHorizontal + halfWidth).toInt(), (centerVertical + halfHeight).toInt())
            draw(canvas)
        }

    override fun getBaseline(): Int {
        if (isVertical) {
            val child = getChildAt(0) ?: return super.getBaseline()
            return child.baseline + child.lp.topMargin + paddingTop
        }
        if (maxBaselineAscent != -1) {
            return maxBaselineAscent + paddingTop
        }
        return super.getBaseline()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        totalLength = 0
        totalWeight = 0f
        childMeasuredState = 0

        if (isVertical) {
            measureVertical(widthMeasureSpec, heightMeasureSpec)
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec)
        }

        constrainedChildren.clear()
        crossMatchParentChildren.clear()
        skippedMatchParentChildren.clear()
    }

    private fun hasDividerBeforeChildAt(childIndex: Int): Boolean {
        return when {
            childIndex == 0 -> showDividers and ShowSeparatorsMode.SHOW_AT_START != 0
            childIndex == childCount -> showDividers and ShowSeparatorsMode.SHOW_AT_END != 0
            showDividers and ShowSeparatorsMode.SHOW_BETWEEN != 0 -> {
                for (i in childIndex - 1 downTo 0) {
                    if (getChildAt(i).visibility != GONE) {
                        return true
                    }
                }
                false
            }
            else -> false
        }
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to [VERTICAL].
     *
     * @param widthMeasureSpec Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     *
     * @see orientation
     * @see onMeasure
     */
    private fun measureVertical(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val exactWidth = widthMode == MeasureSpec.EXACTLY
        var heightSpec = when {
            aspectRatio == DEFAULT_ASPECT_RATIO -> heightMeasureSpec
            exactWidth -> makeExactSpec((widthSize / aspectRatio).roundToInt())
            else -> makeExactSpec(0)
        }

        val initialMaxWidth = (if (exactWidth) widthSize else suggestedMinimumWidth).coerceAtLeast(0)
        maxCrossSize = initialMaxWidth

        forEachSignificantIndexed { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                totalLength += dividerHeight
            }
            totalWeight += child.lp.fixedVerticalWeight
            measureChildWithSignificantSizeVertical(child, widthMeasureSpec, heightSpec)
        }

        setParentCrossSizeIfNeeded(widthMeasureSpec)
        considerMatchParentChildrenInMaxWidth(widthMeasureSpec, heightSpec)
        crossMatchParentChildren.forEach { measureMatchParentWidthChild(it, heightSpec) }
        forEachSignificant { considerMatchParentChildMarginsInHeight(it, heightSpec) }

        if (totalLength > 0 && hasDividerBeforeChildAt(childCount)) {
            totalLength += dividerHeight
        }
        totalLength += paddingTop + paddingBottom

        var heightSize = MeasureSpec.getSize(heightSpec)
        when {
            aspectRatio != DEFAULT_ASPECT_RATIO && !exactWidth -> {
                val widthSizeAndState = getWidthSizeAndState(maxCrossSize, initialMaxWidth, widthMeasureSpec)
                heightSize = ((widthSizeAndState and MEASURED_SIZE_MASK) / aspectRatio).roundToInt()
                heightSpec = makeExactSpec(heightSize)
                remeasureChildrenVerticalIfNeeded(widthMeasureSpec, heightSize, heightSpec, initialMaxWidth)
            }
            aspectRatio == DEFAULT_ASPECT_RATIO && !isExact(heightSpec) -> {
                heightSize = max(totalLength, suggestedMinimumHeight)
                if (isAtMost(heightSpec) && totalWeight > 0) {
                    heightSize = max(MeasureSpec.getSize(heightSpec), heightSize)
                }
                heightSize = resolveSize(heightSize, heightSpec)
                remeasureChildrenVerticalIfNeeded(widthMeasureSpec, heightSize, heightSpec, initialMaxWidth)
                heightSize = max(totalLength, suggestedMinimumHeight)
            }
            else -> remeasureChildrenVerticalIfNeeded(widthMeasureSpec, heightSize, heightSpec, initialMaxWidth)
        }

        setMeasuredDimension(
            getWidthSizeAndState(maxCrossSize, initialMaxWidth, widthMeasureSpec),
            resolveSizeAndState(heightSize, heightSpec, childMeasuredState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    // At this point we should measure only those children which affect container's final dimensions
    private fun measureChildWithSignificantSizeVertical(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        val exactWidth = isExact(widthMeasureSpec)
        val hasSignificantHeight = hasSignificantHeight(child, heightMeasureSpec)
        val hasSignificantSize = if (exactWidth) hasSignificantHeight else lp.width != MATCH_PARENT

        if (hasSignificantSize){
            measureVerticalFirstTime(child, widthMeasureSpec, heightMeasureSpec,
                considerWidth = true, considerHeight = true)
            return
        }

        if (!exactWidth) {
            crossMatchParentChildren += child
        }
        if (!hasSignificantHeight) {
            skippedMatchParentChildren += child
        }
    }

    private fun hasSignificantHeight(child: View, heightMeasureSpec: Int) =
        hasSignificantDimension(child.lp.height, heightMeasureSpec)

    private fun measureVerticalFirstTime(
        child: View,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        considerWidth: Boolean,
        considerHeight: Boolean
    ) {
        val lp = child.lp

        if (lp.height == WRAP_CONTENT_CONSTRAINED) {
            measureConstrainedHeightChildFirstTime(child, widthMeasureSpec, heightMeasureSpec, considerHeight)
        } else {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }

        childMeasuredState = combineMeasuredStates(childMeasuredState, child.measuredState)
        if (considerWidth) {
            updateMaxCrossSize(widthMeasureSpec, child.measuredWidth + lp.horizontalMargins)
        }

        if (!considerHeight || !hasSignificantHeight(child, heightMeasureSpec)) return
        totalLength = getMaxLength(totalLength, child.measuredHeight + lp.verticalMargins)
    }

    private fun measureConstrainedHeightChildFirstTime(
        child: View,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        considerHeight: Boolean
    ) {
        val lp = child.lp
        lp.height = WRAP_CONTENT
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        lp.height = WRAP_CONTENT_CONSTRAINED

        if (!considerHeight) return
        totalConstrainedLength = getMaxLength(totalConstrainedLength, child.measuredHeight + lp.verticalMargins)

        if (constrainedChildren.contains(child)) return
        constrainedChildren.add(child)
    }

    // When container has children with match_parent size along cross direction without other defined dimensions
    // such children can be stretched to maximum size that parent provides.
    private fun setParentCrossSizeIfNeeded(measureSpec: Int) {
        when {
            crossMatchParentChildren.isEmpty() -> Unit
            maxCrossSize > 0 -> Unit
            !isAtMost(measureSpec) -> Unit
            else -> maxCrossSize = MeasureSpec.getSize(measureSpec)
        }
    }

    private fun considerMatchParentChildrenInMaxWidth(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isExact(widthMeasureSpec)) return

        // If container's width is defined by children with exact sizes (fixed or wrap_content)
        // we should consider margins of children with match_parent size.
        if (maxCrossSize != 0) {
            crossMatchParentChildren.forEach { maxCrossSize = max(maxCrossSize, it.lp.horizontalMargins) }
            return
        }

        // Otherwise we calculate maximum size of match_parent children content.
        crossMatchParentChildren.forEach { child ->
            measureVerticalFirstTime(child, widthMeasureSpec, heightMeasureSpec,
                considerWidth = true, considerHeight = false)
            skippedMatchParentChildren -= child
        }
    }

    // If match_parent child has margins it will be drawn even when there's no space for this child itself.
    // So this size should be considered.
    private fun considerMatchParentChildMarginsInHeight(child: View, heightMeasureSpec: Int) {
        if (hasSignificantHeight(child, heightMeasureSpec)) return
        totalLength = getMaxLength(totalLength, child.lp.verticalMargins)
    }

    private fun measureMatchParentWidthChild(child: View, heightMeasureSpec: Int) {
        if (!hasSignificantHeight(child, heightMeasureSpec)) return
        measureVerticalFirstTime(child, makeExactSpec(maxCrossSize), heightMeasureSpec,
            considerWidth = false, considerHeight = true)
        skippedMatchParentChildren -= child
    }

    private fun remeasureChildrenVerticalIfNeeded(
        widthMeasureSpec: Int,
        heightSize: Int,
        heightSpec: Int,
        initialMaxWidth: Int
    ) {
        val delta = heightSize - totalLength
        if (needRemeasureChildren(delta, heightSpec)) {
            totalLength = 0
            remeasureConstrainedHeightChildren(widthMeasureSpec, delta)
            remeasureMatchParentHeightChildren(widthMeasureSpec, initialMaxWidth, delta)
            totalLength += paddingTop + paddingBottom
        }
    }

    // Either expand match_parent children to take up available space or
    // shrink constrained ones if they extend beyond our current bounds.
    // If we skipped measurement on any children, we need to measure them now.
    private fun needRemeasureChildren(delta: Int, spec: Int) = when {
        isUnspecified(spec) -> false
        skippedMatchParentChildren.isNotEmpty() -> true
        delta > 0 -> totalWeight > 0
        delta < 0 -> totalConstrainedLength > 0
        else -> false
    }

    private fun remeasureConstrainedHeightChildren(widthMeasureSpec: Int, delta: Int) {
        if (delta >= 0) return

        var spaceToShrink = delta
        constrainedChildren.sortByDescending { it.minimumHeight / it.measuredHeight.toFloat() }
        constrainedChildren.forEach { child ->
            val lp = child.lp
            val oldHeight = child.measuredHeight
            val oldHeightWithMargins = oldHeight + lp.verticalMargins
            val share = (oldHeightWithMargins / totalConstrainedLength.toFloat() * spaceToShrink).roundToInt()
            val childHeight = (oldHeight + share).coerceAtLeast(child.minimumHeight)

            remeasureChildVertical(child, widthMeasureSpec, maxCrossSize, childHeight)
            childMeasuredState = combineMeasuredStates(childMeasuredState,
                child.measuredState and MEASURED_STATE_TOO_SMALL
                    and (MEASURED_STATE_MASK shr MEASURED_HEIGHT_STATE_SHIFT))

            totalConstrainedLength -= oldHeightWithMargins
            spaceToShrink -= child.measuredHeight - oldHeight
        }
    }

    private fun remeasureChildVertical(child: View, widthMeasureSpec: Int, maxWidth: Int, height: Int) {
        val lp = child.lp
        var widthSpec = widthMeasureSpec
        val oldWidth = lp.width
        when {
            oldWidth != MATCH_PARENT -> Unit
            maxWidth == 0 -> lp.width = WRAP_CONTENT_CONSTRAINED
            else -> widthSpec = makeExactSpec(maxWidth)
        }
        val childWidthMeasureSpec = getChildMeasureSpec(
            widthSpec,
            paddingLeft + paddingRight + lp.horizontalMargins,
            lp.width,
            child.minimumWidth,
            lp.maxWidth
        )
        lp.width = oldWidth

        child.measure(childWidthMeasureSpec, makeExactSpec(height))
        childMeasuredState = combineMeasuredStates(childMeasuredState, child.measuredState and
            ((MEASURED_STATE_MASK shr MEASURED_HEIGHT_STATE_SHIFT)))
    }

    private fun remeasureMatchParentHeightChildren(widthMeasureSpec: Int, initialMaxWidth: Int, delta: Int) {
        var freeSpace = delta
        var weightSum = totalWeight
        val oldMaxWidth = maxCrossSize
        maxCrossSize = initialMaxWidth
        forEachSignificant { child ->
            val lp = child.lp
            when {
                lp.height != MATCH_PARENT -> Unit
                delta > 0 -> {
                    val share = (lp.fixedVerticalWeight * freeSpace / weightSum).toInt()
                    weightSum -= lp.fixedVerticalWeight
                    freeSpace -= share
                    remeasureChildVertical(child, widthMeasureSpec, oldMaxWidth, share)
                }
                skippedMatchParentChildren.contains(child) -> {
                    remeasureChildVertical(child, widthMeasureSpec, oldMaxWidth, 0)
                }
            }

            updateMaxCrossSize(widthMeasureSpec, child.measuredWidth + lp.horizontalMargins)
            totalLength = getMaxLength(totalLength, child.measuredHeight + lp.verticalMargins)
        }

        KAssert.assertEquals(oldMaxWidth, maxCrossSize) { "Width of vertical container changed after remeasuring" }
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to [HORIZONTAL].
     *
     * @param widthMeasureSpec Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     *
     * @see orientation
     * @see onMeasure
     */
    private fun measureHorizontal(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxBaselineAscent = -1
        maxBaselineDescent = -1

        val exactWidth = isExact(widthMeasureSpec)
        var heightSpec = when {
            aspectRatio == DEFAULT_ASPECT_RATIO -> heightMeasureSpec
            exactWidth -> makeExactSpec((MeasureSpec.getSize(widthMeasureSpec) / aspectRatio).roundToInt())
            else -> makeExactSpec(0)
        }
        var heightSize = MeasureSpec.getSize(heightSpec)
        val exactHeight = isExact(heightSpec)

        val initialMaxHeight = (if (exactHeight) heightSize else suggestedMinimumHeight).coerceAtLeast(0)

        forEachSignificantIndexed { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                totalLength += dividerWidth
            }
            totalWeight += child.lp.fixedHorizontalWeight
            measureChildWithSignificantSizeHorizontal(child, widthMeasureSpec, heightSpec)
        }

        forEachSignificant { considerMatchParentChildMarginsInWidth(it, widthMeasureSpec) }

        if (totalLength > 0 && hasDividerBeforeChildAt(childCount)) {
            totalLength += dividerWidth
        }
        totalLength += paddingLeft + paddingRight
        if (isAtMost(widthMeasureSpec) && totalWeight > 0) {
            totalLength = max(MeasureSpec.getSize(widthMeasureSpec), totalLength)
        }

        val widthSizeAndState = resolveSizeAndState(totalLength, widthMeasureSpec, childMeasuredState)
        if (!exactWidth && aspectRatio != DEFAULT_ASPECT_RATIO) {
            heightSize = ((widthSizeAndState and MEASURED_SIZE_MASK) / aspectRatio).roundToInt()
            heightSpec = makeExactSpec(heightSize)
        }

        remeasureChildrenHorizontalIfNeeded(widthMeasureSpec, heightSpec, initialMaxHeight)

        if (!exactHeight && aspectRatio == DEFAULT_ASPECT_RATIO) {
            setParentCrossSizeIfNeeded(heightSpec)
            forEachSignificant { considerMatchParentChildInMaxHeight(it, heightSpec, maxCrossSize == 0) }
            if (maxBaselineAscent != -1) {
                updateMaxCrossSize(heightSpec, maxBaselineAscent + maxBaselineDescent)
            }

            heightSize = resolveSize(
                maxCrossSize + if (maxCrossSize == initialMaxHeight) 0 else paddingTop + paddingBottom,
                heightSpec
            )
        }

        forEachSignificant { remeasureDynamicHeightChild(it, makeExactSpec(heightSize)) }

        setMeasuredDimension(
            widthSizeAndState,
            resolveSizeAndState(heightSize, heightSpec, childMeasuredState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    // At this point we should measure only those children which affect container's final dimensions
    private fun measureChildWithSignificantSizeHorizontal(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!hasSignificantWidth(child, widthMeasureSpec)) return
        val lp = child.lp

        if (lp.width == WRAP_CONTENT_CONSTRAINED) {
            measureChildWithConstrainedWidthFirstTime(child, widthMeasureSpec, heightMeasureSpec)
        } else {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }

        childMeasuredState = combineMeasuredStates(childMeasuredState, child.measuredState)
        updateMaxCrossSize(heightMeasureSpec, child.measuredHeight + lp.verticalMargins)
        updateBaselineOffset(child)

        if (!hasSignificantWidth(child, widthMeasureSpec)) return
        totalLength = getMaxLength(totalLength, child.measuredWidth + lp.horizontalMargins)
    }

    private fun hasSignificantWidth(child: View, widthMeasureSpec: Int) =
        hasSignificantDimension(child.lp.width, widthMeasureSpec)

    private fun hasSignificantDimension(dimension: Int, parentMeasureSpec: Int) =
        dimension != MATCH_PARENT || isUnspecified(parentMeasureSpec)

    private fun measureChildWithConstrainedWidthFirstTime(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        lp.width = WRAP_CONTENT
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        lp.width = WRAP_CONTENT_CONSTRAINED

        totalConstrainedLength = getMaxLength(totalConstrainedLength, child.measuredWidth + lp.horizontalMargins)
        constrainedChildren.add(child)
    }

    // If match_parent child has margins it will be drawn even when there's no space for this child itself.
    // So this size should be considered.
    private fun considerMatchParentChildMarginsInWidth(child: View, widthMeasureSpec: Int) {
        if (hasSignificantWidth(child, widthMeasureSpec)) return
        totalLength = getMaxLength(totalLength, child.lp.horizontalMargins)
    }

    private fun remeasureChildrenHorizontalIfNeeded(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        initialMaxHeight: Int
    ) {
        val delta = MeasureSpec.getSize(widthMeasureSpec) - totalLength
        if (needRemeasureChildren(delta, widthMeasureSpec)) {
            totalLength = 0
            remeasureConstrainedWidthChildren(heightMeasureSpec, delta)
            remeasureMatchParentWidthChildren(heightMeasureSpec, initialMaxHeight, delta)
            totalLength += paddingTop + paddingBottom
        }
    }

    private fun remeasureConstrainedWidthChildren(heightMeasureSpec: Int, delta: Int) {
        if (delta >= 0) return

        var spaceToShrink = delta
        constrainedChildren.sortByDescending { it.minimumWidth / it.measuredWidth.toFloat() }
        constrainedChildren.forEach { child ->
            val lp = child.lp
            val oldWidth = child.measuredWidth
            val oldWidthWithMargins = oldWidth + lp.horizontalMargins
            val share = (oldWidthWithMargins / totalConstrainedLength.toFloat() * spaceToShrink).roundToInt()
            val childWidth = (oldWidth + share).coerceAtLeast(child.minimumWidth)

            remeasureChildHorizontal(child, heightMeasureSpec, childWidth)
            childMeasuredState = combineMeasuredStates(childMeasuredState,
                child.measuredState and MEASURED_STATE_TOO_SMALL and MEASURED_STATE_MASK)

            totalConstrainedLength -= oldWidthWithMargins
            spaceToShrink -= child.measuredWidth - oldWidth
        }
    }

    private fun remeasureMatchParentWidthChildren(heightMeasureSpec: Int, initialMaxHeight: Int, delta: Int) {
        var freeSpace = delta
        var weightSum = totalWeight
        maxCrossSize = initialMaxHeight
        maxBaselineAscent = -1
        maxBaselineDescent = -1
        forEachSignificant { child ->
            val lp = child.lp
            when {
                lp.width != MATCH_PARENT -> Unit
                delta > 0 -> {
                    val share = (lp.fixedHorizontalWeight * freeSpace / weightSum).toInt()
                    weightSum -= lp.fixedHorizontalWeight
                    freeSpace -= share
                    remeasureChildHorizontal(child, heightMeasureSpec, share)
                }
                else -> remeasureChildHorizontal(child, heightMeasureSpec, 0)
            }

            updateMaxCrossSize(heightMeasureSpec, child.measuredHeight + lp.verticalMargins)
            totalLength = getMaxLength(totalLength, child.measuredWidth + lp.horizontalMargins)
            updateBaselineOffset(child)
        }
    }

    private fun considerMatchParentChildInMaxHeight(child: View, heightMeasureSpec: Int, measureChild: Boolean) {
        val lp = child.lp
        if (lp.height != MATCH_PARENT) return

        // If container's height is defined by children with exact sizes (fixed or wrap_content)
        // we should consider margins of children with match_parent size.
        if (measureChild) {
            maxCrossSize = max(maxCrossSize, lp.verticalMargins)
            return
        }

        // Otherwise we calculate maximum size of match_parent children content.
        remeasureChildHorizontal(child, heightMeasureSpec, child.measuredWidth)
        updateMaxCrossSize(heightMeasureSpec, child.measuredHeight + lp.verticalMargins)
    }

    private fun remeasureDynamicHeightChild(child: View, heightMeasureSpec: Int) {
        val height = child.lp.height
        if (height != MATCH_PARENT && height != WRAP_CONTENT_CONSTRAINED) return
        remeasureChildHorizontal(child, heightMeasureSpec, child.measuredWidth)
    }

    private fun remeasureChildHorizontal(child: View, heightMeasureSpec: Int, width: Int): Int {
        val lp = child.lp
        val childHeightMeasureSpec = getChildMeasureSpec(
            heightMeasureSpec,
            paddingTop + paddingBottom + lp.verticalMargins,
            lp.height,
            child.minimumHeight,
            lp.maxHeight
        )

        child.measure(makeExactSpec(width), childHeightMeasureSpec)
        return combineMeasuredStates(childMeasuredState, child.measuredState and MEASURED_STATE_MASK)
    }

    private fun updateMaxCrossSize(measureSpec: Int, childSize: Int) {
        if (!isExact(measureSpec)) {
            maxCrossSize = max(maxCrossSize, childSize)
        }
    }

    private fun getMaxLength(current: Int, additional: Int) = max(current, current + additional)

    private fun getWidthSizeAndState(width: Int, initialWidth: Int, widthMeasureSpec: Int) = resolveSizeAndState(
        width + if (width == initialWidth) 0 else paddingLeft + paddingRight,
        widthMeasureSpec,
        childMeasuredState
    )

    private fun updateBaselineOffset(child: View) {
        val lp = child.lp
        if (!lp.isBaselineAligned) return
        val childBaseline = child.baseline
        if (childBaseline == -1) return

        maxBaselineAscent = max(maxBaselineAscent, childBaseline + lp.topMargin)
        maxBaselineDescent = max(maxBaselineDescent, child.measuredHeight - childBaseline - lp.topMargin)
    }

    private fun forEachSignificant(action: (View) -> Unit) = forEach(true, action)

    private fun forEachSignificantIndexed(action: (View, Int) -> Unit) = forEachIndexed(true, action)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isVertical) {
            layoutVertical(l, t, r, b)
        } else {
            layoutHorizontal(l, t, r, b)
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to [VERTICAL].
     *
     * @see orientation
     * @see onLayout
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    open fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int) {

        // Where right end of child should go
        val width = right - left
        val childRight = width - paddingRight

        // Space available for child
        val childSpace = width - paddingLeft - paddingRight
        val majorGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val minorGravity = gravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK
        var childTop = when (majorGravity) {
            Gravity.BOTTOM -> paddingTop + bottom - top - totalLength
            Gravity.CENTER_VERTICAL -> paddingTop + (bottom - top - totalLength) / 2
            Gravity.TOP -> paddingTop
            else -> paddingTop
        }
        forEachSignificantIndexed { child, i ->
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val lp = child.lp
            var gravity = lp.gravity
            if (gravity < 0) {
                gravity = minorGravity
            }
            val layoutDirection = ViewCompat.getLayoutDirection(this)
            val absoluteGravity = GravityCompat.getAbsoluteGravity(gravity, layoutDirection)
            val childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.CENTER_HORIZONTAL ->
                    paddingLeft + (childSpace - childWidth) / 2 + lp.leftMargin - lp.rightMargin
                Gravity.RIGHT -> childRight - childWidth - lp.rightMargin
                Gravity.LEFT -> paddingLeft + lp.leftMargin
                else -> paddingLeft + lp.leftMargin
            }
            if (hasDividerBeforeChildAt(i)) {
                childTop += dividerHeight
            }
            childTop += lp.topMargin
            setChildFrame(child, childLeft, childTop, childWidth, childHeight)
            childTop += childHeight + lp.bottomMargin
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to [HORIZONTAL].
     *
     * @see orientation
     * @see onLayout
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    open fun layoutHorizontal(left: Int, top: Int, right: Int, bottom: Int) {
        val isLayoutRtl = isLayoutRtl()
        var childTop: Int

        // Where bottom of child should go
        val height = bottom - top
        val childBottom = height - paddingBottom

        // Space available for child
        val childSpace = height - paddingTop - paddingBottom
        val majorGravity = gravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK
        val minorGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val layoutDirection = ViewCompat.getLayoutDirection(this)
        var childLeft = when (GravityCompat.getAbsoluteGravity(majorGravity, layoutDirection)) {
            Gravity.RIGHT -> paddingLeft + right - left - totalLength
            Gravity.CENTER_HORIZONTAL -> paddingLeft + (right - left - totalLength) / 2
            Gravity.LEFT -> paddingLeft
            else -> paddingLeft
        }
        var start = 0
        var dir = 1
        //In case of RTL, start drawing from the last child.
        if (isLayoutRtl) {
            start = childCount - 1
            dir = -1
        }
        for (i in 0 until childCount) {
            val childIndex = start + dir * i
            val child = getChildAt(childIndex)
            if (child == null || child.visibility == GONE) continue

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            var childBaseline = -1
            val lp = child.lp
            if (lp.isBaselineAligned && lp.height != MATCH_PARENT) {
                childBaseline = child.baseline
            }
            var gravity = lp.gravity
            if (gravity < 0) {
                gravity = minorGravity
            }
            when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.TOP -> {
                    childTop = paddingTop + lp.topMargin
                    if (childBaseline != -1) {
                        childTop += maxBaselineAscent - childBaseline - lp.topMargin
                    }
                }
                Gravity.CENTER_VERTICAL ->
                    // Removed support for baseline alignment when layout_gravity or
                    // gravity == center_vertical. See bug #1038483.
                    // Keep the code around if we need to re-enable this feature
                    // if (childBaseline != -1) {
                    //     // Align baselines vertically only if the child is smaller than us
                    //     if (childSpace - childHeight > 0) {
                    //         childTop = paddingTop + (childSpace / 2) - childBaseline;
                    //     } else {
                    //         childTop = paddingTop + (childSpace - childHeight) / 2;
                    //     }
                    // } else {
                    childTop = paddingTop + (childSpace - childHeight) / 2 +
                        lp.topMargin - lp.bottomMargin
                Gravity.BOTTOM -> {
                    childTop = childBottom - childHeight - lp.bottomMargin
                }
                else -> childTop = paddingTop
            }
            if (hasDividerBeforeChildAt(childIndex)) {
                childLeft += dividerWidth
            }
            childLeft += lp.leftMargin
            setChildFrame(child, childLeft, childTop, childWidth, childHeight)
            childLeft += childWidth + lp.rightMargin
        }
    }

    private fun setChildFrame(child: View, left: Int, top: Int, width: Int, height: Int) =
        child.layout(left, top, left + width, top + height)

    fun setHorizontalGravity(horizontalGravity: Int) {
        val newGravity = horizontalGravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK
        if ((gravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) == newGravity) return
        _gravity = (gravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK.inv()) or newGravity
        requestLayout()
    }

    fun setVerticalGravity(verticalGravity: Int) {
        val newGravity = verticalGravity and Gravity.VERTICAL_GRAVITY_MASK
        if ((gravity and Gravity.VERTICAL_GRAVITY_MASK) == newGravity) return
        _gravity = (gravity and Gravity.VERTICAL_GRAVITY_MASK.inv()) or newGravity
        requestLayout()
    }

    /**
     * Returns a set of layout parameters with a width of [MATCH_PARENT] and a height of [WRAP_CONTENT]
     * when the layout's orientation is [VERTICAL]. When the orientation is
     * [HORIZONTAL], the width is set to [WRAP_CONTENT] and the height to [WRAP_CONTENT].
     */
    override fun generateDefaultLayoutParams() =
        if (isVertical) DivLayoutParams(MATCH_PARENT, WRAP_CONTENT) else DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = ACCESSIBILITY_CLASS_NAME
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = ACCESSIBILITY_CLASS_NAME
    }

    private val isVertical get() = orientation == VERTICAL

    private fun isLayoutRtl() =
        ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

    private val DivLayoutParams.fixedHorizontalWeight get() = getFixedWeight(horizontalWeight, width)

    private val DivLayoutParams.fixedVerticalWeight get() = getFixedWeight(verticalWeight, height)

    private fun getFixedWeight(weight: Float, size: Int) = when {
        weight > 0 -> weight
        size == MATCH_PARENT -> 1f
        else -> 0f
    }
}
