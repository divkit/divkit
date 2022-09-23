package com.yandex.div.core.widget.wraplayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.children
import kotlin.math.max

open class WrapLayout(context: Context) : ViewGroup(context) {

    @WrapDirection
    var wrapDirection: Int = WrapDirection.ROW
        set(value) {
            if (field != value) {
                field = value
                isRowDirection = when (field) {
                    WrapDirection.ROW -> true
                    WrapDirection.COLUMN -> false
                    else -> throw IllegalStateException(
                            "Invalid value for the wrap direction is set: $wrapDirection")
                }
                requestLayout()
            }
        }

    private var isRowDirection = true

    private val lines: MutableList<WrapLine> = mutableListOf()

    private var childState = 0

    private var indexToLine: IntArray? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        lines.clear()
        childState = 0

        calculateLines(widthMeasureSpec, heightMeasureSpec)

        if (isRowDirection) {
            determineCrossSize(heightMeasureSpec, paddingTop + paddingBottom)
        } else {
            determineCrossSize(widthMeasureSpec, paddingLeft + paddingRight)
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val calculatedMaxWidth =
                if (isRowDirection) largestMainSize else sumOfCrossSize + paddingLeft + paddingRight
        val calculatedMaxHeight =
                if (isRowDirection) sumOfCrossSize + paddingTop + paddingBottom else largestMainSize

        childState = getState(widthMode, childState, widthSize, calculatedMaxWidth,
                MEASURED_STATE_TOO_SMALL)
        val widthSizeAndState = resolveSizeAndState(
                getSize(widthMode, widthSize, calculatedMaxWidth),
                widthMeasureSpec,
                childState
        )

        childState = getState(heightMode, childState, heightSize, calculatedMaxHeight,
                MEASURED_STATE_TOO_SMALL shr MEASURED_HEIGHT_STATE_SHIFT)
        val heightSizeAndState = resolveSizeAndState(
                getSize(heightMode, heightSize, calculatedMaxHeight),
                heightMeasureSpec,
                childState
        )

        setMeasuredDimension(widthSizeAndState, heightSizeAndState)
    }

    private fun calculateLines(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var largestSizeInCross = Int.MIN_VALUE
        var sumCrossSize = 0

        val mainMeasureSpec = if (isRowDirection) widthMeasureSpec else heightMeasureSpec
        val mainMode = MeasureSpec.getMode(mainMeasureSpec)
        val mainSize = MeasureSpec.getSize(mainMeasureSpec)
        val parentHorizontalPaddings = paddingLeft + paddingRight
        val parentVerticalPaddings = paddingTop + paddingBottom
        val mainPaddings = if (isRowDirection) parentHorizontalPaddings else parentVerticalPaddings

        var indexInLine = 0
        var line = WrapLine(mainSize = mainPaddings)

        children.forEachIndexed { index, child ->
            if (child.visibility == View.GONE) {
                line.goneItemCount++
                line.itemCount++
                addLineIfNeeded(index, line)
                return@forEachIndexed
            }

            val item = child.layoutParams as LayoutParams
            val horizontalMargins = item.leftMargin + item.rightMargin
            val verticalMargins = item.topMargin + item.bottomMargin
            var horizontalPaddings = parentHorizontalPaddings + horizontalMargins
            var verticalPaddings = parentVerticalPaddings + verticalMargins

            if (isRowDirection) {
                verticalPaddings += sumCrossSize
            } else {
                horizontalPaddings += sumCrossSize
            }

            val childWidthMeasureSpec =
                    getChildMeasureSpec(widthMeasureSpec, horizontalPaddings, item.width)
            val childHeightMeasureSpec =
                    getChildMeasureSpec(heightMeasureSpec, verticalPaddings, item.height)

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            childState = View.combineMeasuredStates(childState, child.measuredState)

            val childMainSize: Int
            val childCrossSize: Int
            val childWidth = child.measuredWidth + horizontalMargins
            val childHeight = child.measuredHeight + verticalMargins
            if (isRowDirection) {
                childMainSize = childWidth
                childCrossSize = childHeight
            } else {
                childMainSize = childHeight
                childCrossSize = childWidth
            }

            if (isWrapRequired(mainMode, mainSize, line.mainSize, childMainSize)) {
                if (line.itemCountNotGone > 0) {
                    lines.add(line)
                    sumCrossSize += line.crossSize
                }
                line = WrapLine(firstIndex = index, mainSize = mainPaddings, itemCount = 1)
                indexInLine = 0
                largestSizeInCross = Int.MIN_VALUE
            } else {
                line.itemCount++
                indexInLine++
            }
            indexToLine?.set(index, lines.size)
            line.mainSize += childMainSize
            largestSizeInCross = max(largestSizeInCross, childCrossSize)
            line.crossSize = max(line.crossSize, largestSizeInCross)

            if (addLineIfNeeded(index, line)) {
                sumCrossSize += line.crossSize
            }
        }
    }

    private fun addLineIfNeeded(childIndex: Int, line: WrapLine): Boolean {
        val isLastItem = childIndex == childCount - 1 && line.itemCountNotGone != 0
        if (isLastItem) {
            lines.add(line)
        }
        return isLastItem
    }

    private fun isWrapRequired(mode: Int, maxSize: Int, currentLength: Int, childLength: Int) =
            mode != MeasureSpec.UNSPECIFIED && maxSize < currentLength + childLength

    private fun determineCrossSize(measureSpec: Int, paddingAlongCrossAxis: Int) {
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY && lines.size == 1) {
            lines[0].crossSize = MeasureSpec.getSize(measureSpec) - paddingAlongCrossAxis
        }
    }

    private fun getSize(mode: Int, size: Int, maxSize: Int) = when (mode) {
        MeasureSpec.EXACTLY -> size
        MeasureSpec.AT_MOST -> if (size < maxSize) size else maxSize
        MeasureSpec.UNSPECIFIED -> maxSize
        else -> throw IllegalStateException("Unknown width mode is set: $mode")
    }

    private fun getState(
            mode: Int,
            state: Int,
            size: Int,
            maxSize: Int,
            tooSmallState: Int
    ) = when {
        mode == MeasureSpec.UNSPECIFIED -> state
        size < maxSize -> combineMeasuredStates(state, tooSmallState)
        else -> state
    }

    private val largestMainSize get() = lines.maxOfOrNull { it.mainSize } ?: 0

    private val sumOfCrossSize get() = lines.sumOf { it.crossSize }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isRowDirection) {
            layoutHorizontal(left, top, right, bottom)
        } else {
            layoutVertical(left, top, right, bottom)
        }
    }

    private fun layoutHorizontal(left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        var childBottom = bottom - top - paddingBottom
        var childTop = paddingTop

        var childLeft: Int
        var childRight: Int
        lines.forEach { line ->
            childLeft = paddingLeft
            childRight = width - paddingRight
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.visibility == GONE) {
                    continue
                }
                val lp = child.layoutParams as LayoutParams
                childLeft += lp.leftMargin
                childRight -= lp.rightMargin
                child.layout(
                        childLeft,
                        childTop + lp.topMargin,
                        childLeft + child.measuredWidth,
                        childTop + child.measuredHeight + lp.topMargin
                )
                childLeft += child.measuredWidth + lp.rightMargin
                childRight -= child.measuredWidth + lp.leftMargin
            }
            childTop += line.crossSize
            childBottom -= line.crossSize
        }
    }

    private fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int) {
        val height = bottom - top
        var childLeft = paddingLeft
        var childRight = right - left - paddingRight

        var childTop: Int
        var childBottom: Int
        lines.forEach { line ->
            childTop = paddingTop
            childBottom = height - paddingBottom
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.visibility == GONE) {
                    continue
                }
                val lp = child.layoutParams as LayoutParams
                childTop += lp.topMargin
                childBottom -= lp.bottomMargin
                child.layout(
                        childLeft + lp.leftMargin,
                        childTop,
                        childLeft + child.measuredWidth + lp.leftMargin,
                        childTop + child.measuredHeight
                )
                childTop += child.measuredHeight + lp.bottomMargin
                childBottom -= child.measuredHeight + lp.topMargin
            }
            childLeft += line.crossSize
            childRight -= line.crossSize
        }
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?) = p is LayoutParams

    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(context, attrs)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
            when (lp) {
                is LayoutParams -> LayoutParams(lp)
                is MarginLayoutParams -> LayoutParams(lp)
                else -> LayoutParams(lp)
            }

    internal class LayoutParams : MarginLayoutParams {
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(width: Int, height: Int) : super(ViewGroup.LayoutParams(width, height))
        constructor(source: MarginLayoutParams?) : super(source)
    }

    private data class WrapLine(
            val firstIndex: Int = 0,
            var mainSize: Int = 0,
            var crossSize: Int = 0,
            var itemCount: Int = 0,
            var goneItemCount: Int = 0
    ) {
        val itemCountNotGone get() = itemCount - goneItemCount
    }
}
