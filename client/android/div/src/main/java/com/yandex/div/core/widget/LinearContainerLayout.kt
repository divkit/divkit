package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.HORIZONTAL
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isGone
import com.yandex.div.core.util.getIndices
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.widget.AspectView.Companion.DEFAULT_ASPECT_RATIO
import com.yandex.div.core.widget.AspectView.Companion.aspectRatioProperty
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.WRAP_CONTENT_CONSTRAINED
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
    var orientation by dimensionAffecting(HORIZONTAL)

    private var totalLength = 0
    private var totalConstrainedLength = 0
    private var totalMatchParentLength = 0
    private var childMeasuredState = 0

    override var aspectRatio by aspectRatioProperty()

    private var dividerWidth = 0
    private var dividerHeight = 0

    private var dividerMarginTop = 0
    private var dividerMarginBottom = 0
    private var dividerMarginLeft = 0
    private var dividerMarginRight = 0

    private val offsetsHolder = OffsetsHolder()

    private var firstVisibleChildIndex = -1
    private var lastVisibleChildIndex = -1

    var dividerDrawable: Drawable? = null
        set(value) {
            if (field == value) return
            field = value
            dividerWidth = value?.intrinsicWidth ?: 0
            dividerHeight = value?.intrinsicHeight ?: 0
            setWillNotDraw(value == null)
            requestLayout()
        }

    fun setDividerMargins(left: Int, top: Int, right: Int, bottom: Int) {
        dividerMarginLeft = left
        dividerMarginRight = right
        dividerMarginTop = top
        dividerMarginBottom = bottom

        requestLayout()
    }

    @ShowSeparatorsMode
    var showDividers by dimensionAffecting(0)

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
    private var hasDefinedCrossSize = false

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
                val top = child.top - child.lp.topMargin - dividerHeight - dividerMarginBottom -
                    getDividerOffsetBeforeChildAt(i)
                drawHorizontalDivider(canvas, top)
            }
        }
        if (hasDividerBeforeChildAt(childCount)) {
            val bottom = getChildAt(childCount - 1)?.let {
                it.bottom + it.lp.bottomMargin + dividerMarginTop + offsetsHolder.edgeDividerOffset
            } ?: (height - paddingBottom - dividerHeight - dividerMarginBottom - offsetsHolder.edgeDividerOffset)
            drawHorizontalDivider(canvas, bottom)
        }
    }

    private fun drawDividersHorizontal(canvas: Canvas) {
        val isLayoutRtl = isLayoutRtl()
        forEachSignificantIndexed { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                val offset = getDividerOffsetBeforeChildAt(i)
                val position = if (isLayoutRtl) {
                    child.right + child.lp.rightMargin + dividerMarginLeft + offset
                } else {
                    child.left - child.lp.leftMargin - dividerWidth - dividerMarginRight - offset
                }
                drawVerticalDivider(canvas, position)
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
            drawVerticalDivider(canvas, position)
        }
    }

    private fun drawHorizontalDivider(canvas: Canvas, top: Int) {
        drawDivider(
            canvas,
            paddingLeft + dividerMarginLeft,
            top,
            width - paddingRight - dividerMarginRight,
            top + dividerHeight
        )
    }

    private fun drawVerticalDivider(canvas: Canvas, left: Int) = drawDivider(
        canvas,
        left,
        paddingTop + dividerMarginTop,
        left + dividerWidth,
        height - paddingBottom - dividerMarginBottom
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
        maxCrossSize = 0
        totalConstrainedLength = 0
        totalMatchParentLength = 0
        totalWeight = 0f
        childMeasuredState = 0
        hasDefinedCrossSize = false

        firstVisibleChildIndex = children.indexOfFirst { !it.isGone }
        lastVisibleChildIndex = children.indexOfLast { !it.isGone }

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
            childIndex == firstVisibleChildIndex -> showDividers and ShowSeparatorsMode.SHOW_AT_START != 0
            childIndex > lastVisibleChildIndex -> showDividers and ShowSeparatorsMode.SHOW_AT_END != 0
            showDividers and ShowSeparatorsMode.SHOW_BETWEEN != 0 -> {
                for (i in childIndex - 1 downTo 0) {
                    if (!getChildAt(childIndex).isGone) return true
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
                totalLength += dividerHeightWithMargins
            }
            totalWeight += child.lp.fixedVerticalWeight
            measureChildWithSignificantSizeVertical(child, widthMeasureSpec, heightSpec)
        }
        considerMatchParentChildrenInMaxWidth(widthMeasureSpec, heightSpec)

        if (totalLength > 0 && hasDividerBeforeChildAt(childCount)) {
            totalLength += dividerHeightWithMargins
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

    private val dividerHeightWithMargins get() = dividerHeight + dividerMarginTop + dividerMarginBottom

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
            // If match_parent child has margins it will be drawn even when there's no space for this child itself.
            // So this size should be considered.
            totalLength = getMaxLength(totalLength, child.lp.verticalMargins)
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

        when (lp.height) {
            WRAP_CONTENT_CONSTRAINED -> {
                measureConstrainedHeightChildFirstTime(child, widthMeasureSpec, heightMeasureSpec, considerHeight)
            }
            MATCH_PARENT -> {
                measureMatchParentHeightChildFirstTime(child, widthMeasureSpec, heightMeasureSpec, considerHeight)
            }
            else -> measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }

        childMeasuredState = combineMeasuredStates(childMeasuredState, child.measuredState)
        if (considerWidth) {
            updateMaxCrossSize(widthMeasureSpec, child.measuredWidth + lp.horizontalMargins)
        }

        if (!considerHeight) return
        totalLength = getMaxLength(totalLength, child.measuredHeight + lp.verticalMargins)
    }

    private fun measureConstrainedHeightChildFirstTime(
        child: View,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        considerHeight: Boolean
    ) {
        val lp = child.lp
        val oldMaxHeight = lp.maxHeight
        lp.height = WRAP_CONTENT
        lp.maxHeight = Int.MAX_VALUE
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        lp.height = WRAP_CONTENT_CONSTRAINED
        lp.maxHeight = oldMaxHeight

        if (!considerHeight) return
        totalConstrainedLength = getMaxLength(totalConstrainedLength, child.measuredHeight + lp.verticalMargins)

        if (constrainedChildren.contains(child)) return
        constrainedChildren.add(child)
    }

    private fun measureMatchParentHeightChildFirstTime(
        child: View,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        considerHeight: Boolean
    ) {
        if (isExact(heightMeasureSpec)) {
            measureChildWithMargins(child, widthMeasureSpec, 0, makeExactSpec(0), 0)
            return
        }

        val lp = child.lp
        lp.height = WRAP_CONTENT
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        lp.height = MATCH_PARENT
        if (!considerHeight) return
        totalMatchParentLength = getMaxLength(totalMatchParentLength, child.measuredHeight)
    }

    private fun considerMatchParentChildrenInMaxWidth(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isExact(widthMeasureSpec)) {
            hasDefinedCrossSize = true
            return
        }

        // If container's width is defined by children with exact sizes (fixed or wrap_content)
        // we should consider margins of children with match_parent size.
        if (maxCrossSize != 0) {
            hasDefinedCrossSize = true
            crossMatchParentChildren.forEach { maxCrossSize = max(maxCrossSize, it.lp.horizontalMargins) }
            crossMatchParentChildren.forEach { measureMatchParentWidthChild(it, heightMeasureSpec) }
            return
        }

        // Otherwise we calculate maximum size of match_parent children content.
        crossMatchParentChildren.forEach { child ->
            measureVerticalFirstTime(child, widthMeasureSpec, heightMeasureSpec,
                considerWidth = true, considerHeight = hasSignificantHeight(child, heightMeasureSpec))
            skippedMatchParentChildren -= child
        }
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
        if (constrainedChildren.any { it.maxHeight != Int.MAX_VALUE } || needRemeasureChildren(delta, heightSpec)) {
            totalLength = 0
            remeasureConstrainedHeightChildren(widthMeasureSpec, heightSpec, delta)
            remeasureMatchParentHeightChildren(widthMeasureSpec, heightSpec, initialMaxWidth, delta)
            totalLength += paddingTop + paddingBottom
        }
    }

    // Either expand match_parent children to take up available space or
    // shrink constrained ones if they extend beyond our current bounds.
    // If we skipped measurement on any children, we need to measure them now.
    private fun needRemeasureChildren(delta: Int, spec: Int) = when {
        skippedMatchParentChildren.isNotEmpty() -> true
        isUnspecified(spec) -> false
        delta < 0 -> totalConstrainedLength > 0 || totalWeight > 0
        !isExact(spec) -> false
        delta > 0 -> totalWeight > 0
        else -> false
    }

    private fun remeasureConstrainedHeightChildren(widthMeasureSpec: Int, heightMeasureSpec: Int, delta: Int) {
        val freeSpace = getFreeSpace(delta, heightMeasureSpec)
        if (freeSpace >= 0) {
            constrainedChildren.forEach { child ->
                if (child.maxHeight == Int.MAX_VALUE) return@forEach
                remeasureChildVertical(child, widthMeasureSpec, maxCrossSize,
                    min(child.measuredHeight, child.maxHeight))
            }
            return
        }

        var spaceToShrink = freeSpace
        constrainedChildren.sortByDescending { it.minimumHeight / it.measuredHeight.toFloat() }
        constrainedChildren.forEach { child ->
            val lp = child.lp
            val oldHeight = child.measuredHeight
            val oldHeightWithMargins = oldHeight + lp.verticalMargins
            val share = (oldHeightWithMargins / totalConstrainedLength.toFloat() * spaceToShrink).roundToInt()
            val childHeight = (oldHeight + share).coerceAtLeast(child.minimumHeight).coerceAtMost(lp.maxHeight)

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
            !hasDefinedCrossSize -> lp.width = WRAP_CONTENT_CONSTRAINED
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
            (MEASURED_STATE_MASK shr MEASURED_HEIGHT_STATE_SHIFT))
    }

    private fun remeasureMatchParentHeightChildren(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        initialMaxWidth: Int,
        delta: Int
    ) {
        val freeSpace = getFreeSpace(delta, heightMeasureSpec)
        var spaceToExpand = freeSpace
        var weightSum = totalWeight
        val oldMaxWidth = maxCrossSize
        maxCrossSize = initialMaxWidth
        forEachSignificant { child ->
            val lp = child.lp
            when {
                lp.height != MATCH_PARENT -> Unit
                freeSpace > 0 -> {
                    val share = (lp.fixedVerticalWeight * spaceToExpand / weightSum).toInt()
                    weightSum -= lp.fixedVerticalWeight
                    spaceToExpand -= share
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

    private fun getFreeSpace(delta: Int, spec: Int) = when {
        delta < 0 && totalMatchParentLength > 0 -> (delta + totalMatchParentLength).coerceAtLeast(0)
        delta >= 0 && isExact(spec) -> delta + totalMatchParentLength
        else -> delta
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
        val exactCrossSize = exactHeight || aspectRatio != DEFAULT_ASPECT_RATIO
        hasDefinedCrossSize = exactCrossSize

        val initialMaxHeight = (if (exactHeight) heightSize else suggestedMinimumHeight).coerceAtLeast(0)

        forEachSignificantIndexed { child, i ->
            if (hasDividerBeforeChildAt(i)) {
                totalLength += dividerWidthWithMargins
            }
            totalWeight += child.lp.fixedHorizontalWeight
            hasDefinedCrossSize = hasDefinedCrossSize || child.lp.height != MATCH_PARENT
            measureChildWithSignificantSizeHorizontal(child, widthMeasureSpec, heightSpec)
        }

        forEachSignificant { considerMatchParentChildMarginsInWidth(it, widthMeasureSpec) }

        if (totalLength > 0 && hasDividerBeforeChildAt(childCount)) {
            totalLength += dividerWidthWithMargins
        }
        totalLength += paddingLeft + paddingRight
        val resizedTotalLength = max(suggestedMinimumWidth, totalLength)

        val widthSizeAndState = resolveSizeAndState(resizedTotalLength, widthMeasureSpec, childMeasuredState)
        val widthSize = widthSizeAndState and MEASURED_SIZE_MASK
        if (!exactWidth && aspectRatio != DEFAULT_ASPECT_RATIO) {
            heightSize = (widthSize / aspectRatio).roundToInt()
            heightSpec = makeExactSpec(heightSize)
        }

        remeasureChildrenHorizontalIfNeeded(widthMeasureSpec, widthSize, heightSpec, initialMaxHeight)

        if (!exactCrossSize) {
            forEachSignificant { considerMatchParentChildInMaxHeight(it, heightSpec, maxCrossSize == 0) }
            if (maxBaselineAscent != -1) {
                updateMaxCrossSize(heightSpec, maxBaselineAscent + maxBaselineDescent)
            }

            heightSize = resolveSize(
                maxCrossSize + if (maxCrossSize == initialMaxHeight) 0 else paddingTop + paddingBottom,
                heightSpec
            )
        }

        if (hasDefinedCrossSize) {
            heightSpec = makeExactSpec(heightSize)
        }
        forEachSignificant {
            remeasureDynamicHeightChild(it, heightSpec)
        }

        setMeasuredDimension(
            widthSizeAndState,
            resolveSizeAndState(heightSize, heightSpec, childMeasuredState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    private val dividerWidthWithMargins get() = dividerWidth + dividerMarginRight + dividerMarginLeft

    // At this point we should measure only those children which affect container's final dimensions
    private fun measureChildWithSignificantSizeHorizontal(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!hasSignificantWidth(child, widthMeasureSpec)) return
        val lp = child.lp

        when (lp.width) {
            WRAP_CONTENT_CONSTRAINED -> {
                measureConstrainedWidthChildFirstTime(child, widthMeasureSpec, heightMeasureSpec)
            }
            MATCH_PARENT -> {
                measureMatchParentWidthChildFirstTime(child, widthMeasureSpec, heightMeasureSpec)
            }
            else -> measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        }

        childMeasuredState = combineMeasuredStates(childMeasuredState, child.measuredState)
        updateMaxCrossSize(heightMeasureSpec, child.measuredHeight + lp.verticalMargins)
        updateBaselineOffset(child)
        totalLength = getMaxLength(totalLength, child.measuredWidth + lp.horizontalMargins)
    }

    private fun hasSignificantWidth(child: View, widthMeasureSpec: Int) =
        hasSignificantDimension(child.lp.width, widthMeasureSpec)

    private fun hasSignificantDimension(dimension: Int, parentMeasureSpec: Int) =
        dimension != MATCH_PARENT || !isExact(parentMeasureSpec)

    private fun measureConstrainedWidthChildFirstTime(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        val oldMaxWidth = lp.maxWidth
        lp.width = WRAP_CONTENT
        lp.maxWidth = Int.MAX_VALUE
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        lp.width = WRAP_CONTENT_CONSTRAINED
        lp.maxWidth = oldMaxWidth

        totalConstrainedLength = getMaxLength(totalConstrainedLength, child.measuredWidth + lp.horizontalMargins)
        constrainedChildren.add(child)
    }

    private fun measureMatchParentWidthChildFirstTime(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        lp.width = WRAP_CONTENT
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
        lp.width = MATCH_PARENT
        totalMatchParentLength = getMaxLength(totalMatchParentLength, child.measuredWidth + lp.horizontalMargins)
    }

    // If match_parent child has margins it will be drawn even when there's no space for this child itself.
    // So this size should be considered.
    private fun considerMatchParentChildMarginsInWidth(child: View, widthMeasureSpec: Int) {
        if (hasSignificantWidth(child, widthMeasureSpec)) return
        totalLength = getMaxLength(totalLength, child.lp.horizontalMargins)
    }

    private fun remeasureChildrenHorizontalIfNeeded(
        widthMeasureSpec: Int,
        widthSize: Int,
        heightMeasureSpec: Int,
        initialMaxHeight: Int
    ) {
        val delta = widthSize - totalLength
        if (constrainedChildren.any { it.maxWidth != Int.MAX_VALUE }
            || needRemeasureChildren(delta, widthMeasureSpec)) {
            totalLength = 0
            remeasureConstrainedWidthChildren(widthMeasureSpec, heightMeasureSpec, delta)
            remeasureMatchParentWidthChildren(widthMeasureSpec, heightMeasureSpec, initialMaxHeight, delta)
            totalLength += paddingLeft + paddingRight
        }
    }

    private fun remeasureConstrainedWidthChildren(widthMeasureSpec: Int, heightMeasureSpec: Int, delta: Int) {
        val freeSpace = getFreeSpace(delta, widthMeasureSpec)
        if (freeSpace >= 0) {
            constrainedChildren.forEach { child ->
                if (child.maxWidth == Int.MAX_VALUE) return@forEach
                remeasureChildHorizontal(child, heightMeasureSpec, min(child.measuredWidth, child.maxWidth))
            }
            return
        }

        var spaceToShrink = freeSpace
        constrainedChildren.sortByDescending { it.minimumWidth / it.measuredWidth.toFloat() }
        constrainedChildren.forEach { child ->
            val lp = child.lp
            val oldWidth = child.measuredWidth
            val oldWidthWithMargins = oldWidth + lp.horizontalMargins
            val share = (oldWidthWithMargins / totalConstrainedLength.toFloat() * spaceToShrink).roundToInt()
            val childWidth = (oldWidth + share).coerceAtLeast(child.minimumWidth).coerceAtMost(lp.maxWidth)

            remeasureChildHorizontal(child, heightMeasureSpec, childWidth)
            childMeasuredState = combineMeasuredStates(childMeasuredState,
                child.measuredState and MEASURED_STATE_TOO_SMALL and MEASURED_STATE_MASK)

            totalConstrainedLength -= oldWidthWithMargins
            spaceToShrink -= child.measuredWidth - oldWidth
        }
    }

    private fun remeasureMatchParentWidthChildren(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        initialMaxHeight: Int,
        delta: Int
    ) {
        val freeSpace = getFreeSpace(delta, widthMeasureSpec)
        var spaceToExpand = freeSpace
        var weightSum = totalWeight
        maxCrossSize = initialMaxHeight
        maxBaselineAscent = -1
        maxBaselineDescent = -1
        forEachSignificant { child ->
            val lp = child.lp
            when {
                lp.width != MATCH_PARENT -> Unit
                freeSpace > 0 -> {
                    val share = (lp.fixedHorizontalWeight * spaceToExpand / weightSum).toInt()
                    weightSum -= lp.fixedHorizontalWeight
                    spaceToExpand -= share
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

    private inline fun forEachSignificant(action: (View) -> Unit) = forEach(true, action)

    private inline fun forEachSignificantIndexed(action: (View, Int) -> Unit) = forEachIndexed(true, action)

    private val View.maxHeight get() = lp.maxHeight

    private val View.maxWidth get() = lp.maxWidth

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isVertical) {
            layoutVertical(l, t, r, b)
        } else {
            layoutHorizontal(l, t, r, b)
        }
    }

    private fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int) {
        val childSpace = right - left - paddingLeft - paddingRight
        val freeSpace = (bottom - top - totalLength).toFloat()
        var childTop = paddingTop.toFloat()
        offsetsHolder.update(freeSpace, verticalGravity, visibleChildCount)
        childTop += offsetsHolder.firstChildOffset
        forEachSignificantIndexed { child, i ->
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val lp = child.lp
            var gravity = lp.gravity.toHorizontalGravity()
            if (gravity < 0) {
                gravity = horizontalGravity
            }
            val layoutDirection = ViewCompat.getLayoutDirection(this)
            val childLeft = paddingLeft + when (GravityCompat.getAbsoluteGravity(gravity, layoutDirection)) {
                Gravity.LEFT -> lp.leftMargin
                Gravity.CENTER_HORIZONTAL -> (childSpace - childWidth + lp.leftMargin - lp.rightMargin) / 2
                Gravity.RIGHT -> childSpace - childWidth - lp.rightMargin
                else -> lp.leftMargin
            }
            if (hasDividerBeforeChildAt(i)) {
                childTop += dividerHeightWithMargins
            }
            childTop += lp.topMargin
            setChildFrame(child, childLeft, childTop.roundToInt(), childWidth, childHeight)
            childTop += childHeight + lp.bottomMargin + offsetsHolder.spaceBetweenChildren
        }
    }

    private val visibleChildCount get() = children.count { !it.isGone }

    private fun layoutHorizontal(left: Int, top: Int, right: Int, bottom: Int) {
        val childSpace = bottom - top - paddingTop - paddingBottom
        val layoutDirection = ViewCompat.getLayoutDirection(this)
        val freeSpace = (right - left - totalLength).toFloat()
        var childLeft = paddingLeft.toFloat()
        val absoluteGravity = GravityCompat.getAbsoluteGravity(horizontalGravity, layoutDirection)
        offsetsHolder.update(freeSpace, absoluteGravity, visibleChildCount)
        childLeft += offsetsHolder.firstChildOffset
        for (childIndex in getIndices(0, childCount)) {
            val child = getChildAt(childIndex)
            if (child == null || child.isGone) continue

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val lp = child.lp
            var gravity = lp.gravity.toVerticalGravity()
            if (gravity < 0) {
                gravity = verticalGravity
            }
            val childTop = paddingTop + when (gravity) {
                Gravity.TOP -> {
                    val considerBaseline = lp.isBaselineAligned && lp.height != MATCH_PARENT
                    if (considerBaseline) maxBaselineAscent - child.baseline else lp.topMargin
                }
                Gravity.CENTER_VERTICAL -> (childSpace - childHeight + lp.topMargin - lp.bottomMargin) / 2
                Gravity.BOTTOM -> childSpace - childHeight - lp.bottomMargin
                else -> 0
            }
            if (hasDividerBeforeChildAt(if (isLayoutRtl()) childIndex + 1 else childIndex)) {
                childLeft += dividerWidthWithMargins
            }
            childLeft += lp.leftMargin
            setChildFrame(child, childLeft.roundToInt(), childTop, childWidth, childHeight)
            childLeft += childWidth + lp.rightMargin + offsetsHolder.spaceBetweenChildren
        }
    }

    private fun setChildFrame(child: View, left: Int, top: Int, width: Int, height: Int) =
        child.layout(left, top, left + width, top + height)

    /**
     * Returns a set of layout parameters with a width of [MATCH_PARENT] and a height of [WRAP_CONTENT]
     * when the layout's orientation is [VERTICAL]. When the orientation is
     * [HORIZONTAL], the width is set to [WRAP_CONTENT] and the height to [WRAP_CONTENT].
     */
    override fun generateDefaultLayoutParams() =
        if (isVertical) DivLayoutParams(MATCH_PARENT, WRAP_CONTENT) else DivLayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    private val isVertical get() = orientation == VERTICAL

    private val DivLayoutParams.fixedHorizontalWeight get() = getFixedWeight(horizontalWeight, width)

    private val DivLayoutParams.fixedVerticalWeight get() = getFixedWeight(verticalWeight, height)

    private fun getFixedWeight(weight: Float, size: Int) = when {
        weight > 0 -> weight
        size == MATCH_PARENT -> 1f
        else -> 0f
    }
}
