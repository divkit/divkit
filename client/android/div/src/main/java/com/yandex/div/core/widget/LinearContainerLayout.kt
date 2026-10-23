package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.HORIZONTAL
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import androidx.core.view.children
import com.yandex.div.core.widget.AspectView.Companion.DEFAULT_ASPECT_RATIO
import com.yandex.div.core.widget.AspectView.Companion.aspectRatioProperty
import com.yandex.div.internal.Assert
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.DEFAULT_MAX_SIZE
import com.yandex.div.internal.widget.DivLayoutParams.Companion.WRAP_CONTENT_CONSTRAINED
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

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
    private var skippedMatchParentMinSizeLength = 0
    private var childMeasuredState = 0

    override var aspectRatio by aspectRatioProperty()

    private val offsetsHolder = OffsetsHolder()
    private val dividers = LinearContainerDividers(this, offsetsHolder)
    private val childLayout = LinearContainerChildLayout(this, offsetsHolder, dividers)

    var dividerDrawable: Drawable?
        get() = dividers.dividerDrawable
        set(value) { dividers.dividerDrawable = value }

    @ShowSeparatorsMode
    var showDividers: Int
        get() = dividers.showDividers
        set(value) { dividers.showDividers = value }

    fun setDividerMargins(left: Int, top: Int, right: Int, bottom: Int) =
        dividers.setDividerMargins(left, top, right, bottom)

    fun setItemSpacing(px: Int) = dividers.setItemSpacing(px)

    private fun gapBeforeChild(index: Int) = dividers.gapBeforeChild(index)

    private val constrainedChildren = mutableListOf<View>()
    private val skippedMatchParentChildren = mutableSetOf<View>()

    // For horizontal orientation we assume width as main size and height as cross size.
    // For vertical orientation, respectively, height is main size and width is cross size.
    private var maxCrossSize = 0
    // Children which have match_parent size in main direction.
    private val mainMatchParentChildren = mutableListOf<View>()
    // Children which have match_parent size in cross direction.
    private val crossMatchParentChildren = mutableSetOf<View>()
    // Children with match_parent size in main direction can be stretched
    // according to the ratio of their weight to total weight of those children.
    private var totalWeight = 0f
    private val matchParentSizes = mutableListOf<Pair<Int, Int>>()

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    override fun onDraw(canvas: Canvas) = dividers.draw(canvas)

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
        skippedMatchParentMinSizeLength = 0
        totalWeight = 0f
        childMeasuredState = 0

        dividers.updateVisibleChildren()

        if (isVertical) {
            measureVertical(widthMeasureSpec, heightMeasureSpec)
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec)
        }

        constrainedChildren.clear()
        crossMatchParentChildren.clear()
        skippedMatchParentChildren.clear()
        mainMatchParentChildren.clear()
        matchParentSizes.clear()
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

        val initialMaxWidth = (if (exactWidth) widthSize else max(suggestedMinimumWidth, horizontalPaddings))
            .coerceAtLeast(0)

        forEachSignificantIndexed { child, i ->
            totalLength += gapBeforeChild(i)
            totalWeight += child.lp.fixedVerticalWeight
            measureChildWithSignificantSizeVertical(child, widthMeasureSpec, heightSpec)
        }
        considerMatchParentChildrenInMaxWidth(widthMeasureSpec, heightSpec)
        maxCrossSize = max(initialMaxWidth, maxCrossSize + horizontalPaddings)
        crossMatchParentChildren.forEach { measureMatchParentWidthChild(it, heightSpec) }

        if (totalLength > 0) {
            totalLength += gapBeforeChild(childCount)
        }
        totalLength += verticalPaddings

        var heightSize = MeasureSpec.getSize(heightSpec)
        when {
            aspectRatio != DEFAULT_ASPECT_RATIO && !exactWidth -> {
                val widthSizeAndState = resolveSizeAndState(maxCrossSize, widthMeasureSpec, childMeasuredState)
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
            resolveSizeAndState(maxCrossSize, widthMeasureSpec, childMeasuredState),
            resolveSizeAndState(heightSize, heightSpec, childMeasuredState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    // At this point we should measure only those children which affect container's final dimensions
    private fun measureChildWithSignificantSizeVertical(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val lp = child.lp
        val exactWidth = isExact(widthMeasureSpec)
        val hasSignificantHeight = hasSignificantHeight(child, heightMeasureSpec)
        val hasSignificantSize = if (exactWidth) hasSignificantHeight else lp.width != MATCH_PARENT

        if (lp.height == MATCH_PARENT) {
            mainMatchParentChildren.add(child)
            matchParentSizes.add(emptySize)
        }

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
            skippedMatchParentMinSizeLength += child.minimumHeight
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
        lp.maxHeight = DEFAULT_MAX_SIZE
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
            if (!considerHeight) return
            totalMatchParentLength = getMaxLength(totalMatchParentLength, child.measuredHeight)
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
        if (isExact(widthMeasureSpec)) return

        // If container's width is defined by children with exact sizes (fixed or wrap_content)
        // we should consider margins of children with match_parent size.
        if (maxCrossSize != 0) {
            crossMatchParentChildren.forEach { maxCrossSize = max(maxCrossSize, it.minimumWidth + it.lp.horizontalMargins) }
            return
        }

        // Otherwise we calculate maximum size of match_parent children content.
        crossMatchParentChildren.forEach { child ->
            measureVerticalFirstTime(child, widthMeasureSpec, heightMeasureSpec,
                considerWidth = true, considerHeight = false)
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
        val delta = heightSize - totalLength - skippedMatchParentMinSizeLength
        if (constrainedChildren.any { it.maxHeight != DEFAULT_MAX_SIZE } || needRemeasureChildren(delta, heightSpec)) {
            totalLength = 0
            remeasureConstrainedHeightChildren(widthMeasureSpec, heightSpec, delta)
            remeasureMatchParentHeightChildren(widthMeasureSpec, heightSpec, initialMaxWidth, delta)
            totalLength += gapBeforeChild(childCount)
            totalLength += verticalPaddings
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
                if (child.maxHeight == DEFAULT_MAX_SIZE) return@forEach
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
            maxWidth == 0 -> lp.width = WRAP_CONTENT_CONSTRAINED
            else -> widthSpec = makeExactSpec(maxWidth)
        }
        val childWidthMeasureSpec = getChildMeasureSpec(
            widthSpec,
            horizontalPaddings + lp.horizontalMargins,
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
        val oldMaxWidth = maxCrossSize
        maxCrossSize = 0
        val freeSpace = getFreeSpace(delta, heightMeasureSpec)
        if (freeSpace > 0) {
            calculateMatchParentSizes(freeSpace, horizontal = false)
        }

        forEachSignificantIndexed { child, index ->
            val lp = child.lp
            when {
                lp.height != MATCH_PARENT -> Unit
                freeSpace > 0 -> {
                    val size = matchParentSizes[mainMatchParentChildren.indexOf(child)].second
                    remeasureChildVertical(child, widthMeasureSpec, oldMaxWidth, size)
                }
                skippedMatchParentChildren.contains(child) ->
                    remeasureChildVertical(child, widthMeasureSpec, oldMaxWidth, max(child.minimumHeight, 0))
            }

            updateMaxCrossSize(widthMeasureSpec, child.measuredWidth + lp.horizontalMargins)
            totalLength = getMaxLength(totalLength, child.measuredHeight + lp.verticalMargins)
            totalLength += gapBeforeChild(index)
        }
        maxCrossSize = max(initialMaxWidth, maxCrossSize + horizontalPaddings)

        Assert.assertEquals("Width of vertical container changed after remeasuring", oldMaxWidth, maxCrossSize)
    }

    private fun getFreeSpace(delta: Int, spec: Int) = when {
        delta < 0 && totalMatchParentLength > 0 -> (delta + totalMatchParentLength).coerceAtLeast(0)
        delta >= 0 && isExact(spec) -> delta + totalMatchParentLength
        else -> delta
    }

    private fun calculateMatchParentSizes(freeSpace: Int, horizontal: Boolean) {
        var spaceToExpand = freeSpace + skippedMatchParentMinSizeLength
        var weightSum = totalWeight
        var lastSize = emptySize
        while (spaceToExpand != 0 && weightSum > 0) {
            val currentSpace = spaceToExpand
            val currentWeightSum = weightSum
            var shiftSum = 0f
            mainMatchParentChildren.forEachIndexed { index, child ->
                val (oldDesiredSize, oldSize) = matchParentSizes[index]
                if (oldDesiredSize != oldSize) return@forEachIndexed
                val lp = child.lp
                val weight = if (horizontal) lp.fixedHorizontalWeight else lp.fixedVerticalWeight
                val share = (weight * currentSpace / currentWeightSum)
                val roundedShare = share.roundToInt()
                val shift = roundedShare - share
                shiftSum += shift
                var desiredSize = oldSize + roundedShare
                if (abs(shiftSum) >= 1 && shift != 0f) {
                    val compensation = sign(shift)
                    desiredSize -= compensation.toInt()
                    shiftSum -= compensation
                }
                val minSize = if (horizontal) child.minimumWidth else child.minimumHeight
                val maxSize = if (horizontal) lp.maxWidth else lp.maxHeight
                val size = min(max(desiredSize, minSize), maxSize)
                spaceToExpand -= size - oldSize
                if (desiredSize != size) {
                    weightSum -= weight
                }
                lastSize = desiredSize to size
                matchParentSizes[index] = lastSize
            }
            if (spaceToExpand == 1 || spaceToExpand == -1) {
                matchParentSizes[matchParentSizes.lastIndex] =
                    lastSize.first + spaceToExpand to lastSize.second + spaceToExpand
                spaceToExpand = 0
            }
        }
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

        val initialMaxHeight = (if (exactHeight) heightSize else max(suggestedMinimumHeight, verticalPaddings))
            .coerceAtLeast(0)

        forEachSignificantIndexed { child, i ->
            totalLength += gapBeforeChild(i)
            totalWeight += child.lp.fixedHorizontalWeight

            if (child.lp.width == MATCH_PARENT) {
                mainMatchParentChildren.add(child)
                matchParentSizes.add(emptySize)
            }

            measureChildWithSignificantSizeHorizontal(child, widthMeasureSpec, heightSpec)
        }

        forEachSignificant { considerMatchParentChildMarginsInWidth(it, widthMeasureSpec) }

        if (totalLength > 0) {
            totalLength += gapBeforeChild(childCount)
        }
        totalLength += horizontalPaddings

        val widthSize = getWidthSizeAndState(widthMeasureSpec) and MEASURED_SIZE_MASK
        if (!exactWidth && aspectRatio != DEFAULT_ASPECT_RATIO) {
            heightSize = (widthSize / aspectRatio).roundToInt()
            heightSpec = makeExactSpec(heightSize)
        }

        remeasureChildrenHorizontalIfNeeded(widthMeasureSpec, widthSize, heightSpec)

        if (!exactHeight && aspectRatio == DEFAULT_ASPECT_RATIO) {
            forEachSignificant { considerMatchParentChildInMaxHeight(it, heightSpec, maxCrossSize == 0) }
            maxCrossSize = max(initialMaxHeight, maxCrossSize + verticalPaddings)
            if (maxBaselineAscent != -1) {
                updateMaxCrossSize(heightSpec, maxBaselineAscent + maxBaselineDescent)
            }

            heightSize = resolveSize(maxCrossSize, heightSpec)
        }

        forEachSignificant { remeasureDynamicHeightChild(it, makeExactSpec(heightSize)) }

        setMeasuredDimension(
            getWidthSizeAndState(widthMeasureSpec),
            resolveSizeAndState(heightSize, heightSpec, childMeasuredState shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    // At this point we should measure only those children which affect container's final dimensions
    private fun measureChildWithSignificantSizeHorizontal(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!hasSignificantWidth(child, widthMeasureSpec)) {
            if (child.lp.width == MATCH_PARENT) {
                skippedMatchParentMinSizeLength += child.minimumWidth
            }
            return
        }
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
        lp.maxWidth = DEFAULT_MAX_SIZE
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

    private fun getWidthSizeAndState(measureSpec: Int) =
        resolveSizeAndState(max(suggestedMinimumWidth, totalLength), measureSpec, childMeasuredState)

    private fun remeasureChildrenHorizontalIfNeeded(
        widthMeasureSpec: Int,
        widthSize: Int,
        heightMeasureSpec: Int
    ) {
        val delta = widthSize - totalLength - skippedMatchParentMinSizeLength
        if (constrainedChildren.any { it.maxWidth != DEFAULT_MAX_SIZE }
            || needRemeasureChildren(delta, widthMeasureSpec)) {
            totalLength = 0
            remeasureConstrainedWidthChildren(widthMeasureSpec, heightMeasureSpec, delta)
            remeasureMatchParentWidthChildren(widthMeasureSpec, heightMeasureSpec, delta)
            totalLength += gapBeforeChild(childCount)
            totalLength += horizontalPaddings
        }
    }

    private fun remeasureConstrainedWidthChildren(widthMeasureSpec: Int, heightMeasureSpec: Int, delta: Int) {
        val freeSpace = getFreeSpace(delta, widthMeasureSpec)
        if (freeSpace >= 0) {
            constrainedChildren.forEach { child ->
                if (child.maxWidth == DEFAULT_MAX_SIZE) return@forEach
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
        delta: Int
    ) {
        val freeSpace = getFreeSpace(delta, widthMeasureSpec)
        maxCrossSize = 0
        maxBaselineAscent = -1
        maxBaselineDescent = -1
        if (freeSpace > 0) {
            calculateMatchParentSizes(freeSpace, horizontal = true)
        }
        forEachSignificantIndexed { child, index ->
            val lp = child.lp
            when {
                lp.width != MATCH_PARENT -> Unit
                freeSpace > 0 -> {
                    val size = matchParentSizes[mainMatchParentChildren.indexOf(child)].second
                    remeasureChildHorizontal(child, heightMeasureSpec, size)
                }
                else -> remeasureChildHorizontal(child, heightMeasureSpec, max(child.minimumWidth, 0))
            }

            updateMaxCrossSize(heightMeasureSpec, child.measuredHeight + lp.verticalMargins)
            totalLength = getMaxLength(totalLength, child.measuredWidth + lp.horizontalMargins)
            totalLength += gapBeforeChild(index)
            updateBaselineOffset(child)
        }
    }

    private fun considerMatchParentChildInMaxHeight(child: View, heightMeasureSpec: Int, measureChild: Boolean) {
        val lp = child.lp
        if (lp.height != MATCH_PARENT) return

        // If container's height is defined by children with exact sizes (fixed or wrap_content)
        // we should consider margins of children with match_parent size.
        if (measureChild) {
            maxCrossSize = max(maxCrossSize, child.minimumHeight + lp.verticalMargins)
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
            verticalPaddings + lp.verticalMargins,
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
            childLayout.layoutVertical(l, t, r, b, totalLength)
        } else {
            childLayout.layoutHorizontal(l, t, r, b, totalLength, maxBaselineAscent)
        }
    }

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

    private companion object {
        val emptySize = 0 to 0
    }
}
