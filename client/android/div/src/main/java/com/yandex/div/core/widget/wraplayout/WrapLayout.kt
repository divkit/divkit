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

    @WrapAlignment
    var alignmentHorizontal: Int = WrapAlignment.START
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    @WrapAlignment
    var alignmentVertical: Int = WrapAlignment.START
        set(value) {
            if (field != value) {
                field = value
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
            determineCrossSize(heightMeasureSpec, alignmentVertical, paddingTop + paddingBottom)
        } else {
            determineCrossSize(widthMeasureSpec, alignmentHorizontal, paddingLeft + paddingRight)
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

    private fun determineCrossSize(
            measureSpec: Int,
            @WrapAlignment crossAlignment: Int,
            paddingAlongCrossAxis: Int
    ) {
        if (lines.size == 0) {
            return
        }

        if (MeasureSpec.getMode(measureSpec) != MeasureSpec.EXACTLY) {
            return
        }

        val size = MeasureSpec.getSize(measureSpec)
        val totalCrossSize: Int = sumOfCrossSize + paddingAlongCrossAxis
        if (lines.size == 1) {
            lines[0].crossSize = size - paddingAlongCrossAxis
            return
        }

        when (crossAlignment) {
            WrapAlignment.END -> {
                val spaceTop = size - totalCrossSize
                val spaceLine = WrapLine()
                spaceLine.crossSize = spaceTop
                lines.add(0, spaceLine)
            }
            WrapAlignment.CENTER -> {
                val spaceAboveAndBottom = (size - totalCrossSize) / 2
                val spaceLine = WrapLine()
                spaceLine.crossSize = spaceAboveAndBottom
                lines.add(0, spaceLine)
                lines.add(spaceLine)
            }
            else -> Unit
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
            when (alignmentHorizontal) {
                WrapAlignment.START -> {
                    childLeft = paddingLeft
                    childRight = width - paddingRight
                }
                WrapAlignment.END -> {
                    childLeft = width - line.mainSize + paddingRight
                    childRight = line.mainSize - paddingLeft
                }
                WrapAlignment.CENTER -> {
                    childLeft = paddingLeft + (width - line.mainSize) / 2
                    childRight = width - paddingRight - (width - line.mainSize) / 2
                }
                else -> throw java.lang.IllegalStateException(
                        "Invalid alignmentHorizontal is set: $alignmentHorizontal")
            }
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.visibility == GONE) {
                    continue
                }
                val lp = child.layoutParams as LayoutParams
                childLeft += lp.leftMargin
                childRight -= lp.rightMargin
                val currentTop = childTop + getTopOffsetForHorizontalLayout(child, line.crossSize)
                child.layout(childLeft, currentTop,
                        childLeft + child.measuredWidth, currentTop + child.measuredHeight)
                childLeft += child.measuredWidth + lp.rightMargin
                childRight -= child.measuredWidth + lp.leftMargin
            }
            childTop += line.crossSize
            childBottom -= line.crossSize
        }
    }

    private fun getTopOffsetForHorizontalLayout(view: View, lineHeight: Int): Int {
        val lp = view.layoutParams as LayoutParams
        return when (lp.childAlignment) {
            WrapAlignment.END -> lineHeight - view.measuredHeight - lp.bottomMargin
            WrapAlignment.CENTER ->
                (lineHeight - view.measuredHeight + lp.topMargin - lp.bottomMargin) / 2
            else -> lp.topMargin
        }
    }

    private fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int) {
        val height = bottom - top
        var childLeft = paddingLeft
        var childRight = right - left - paddingRight

        var childTop: Int
        var childBottom: Int
        lines.forEach { line ->
            when (alignmentVertical) {
                WrapAlignment.START -> {
                    childTop = paddingTop
                    childBottom = height - paddingBottom
                }
                WrapAlignment.END -> {
                    childTop = height - line.mainSize + paddingBottom
                    childBottom = line.mainSize - paddingTop
                }
                WrapAlignment.CENTER -> {
                    childTop = paddingTop + (height - line.mainSize) / 2
                    childBottom = height - paddingBottom - (height - line.mainSize) / 2
                }
                else -> throw java.lang.IllegalStateException(
                        "Invalid alignmentVertical is set: $alignmentVertical")
            }
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.visibility == GONE) {
                    continue
                }
                val lp = child.layoutParams as LayoutParams
                childTop += lp.topMargin
                childBottom -= lp.bottomMargin
                val currentLeft = childLeft + getLeftOffsetForVerticalLayout(child, line.crossSize)
                child.layout(currentLeft, childTop,
                        currentLeft + child.measuredWidth, childTop + child.measuredHeight)
                childTop += child.measuredHeight + lp.bottomMargin
                childBottom -= child.measuredHeight + lp.topMargin
            }
            childLeft += line.crossSize
            childRight -= line.crossSize
        }
    }

    private fun getLeftOffsetForVerticalLayout(view: View, lineWidth: Int): Int {
        val lp = view.layoutParams as LayoutParams
        return when (lp.childAlignment) {
            WrapAlignment.END -> lineWidth - view.measuredWidth - lp.rightMargin
            WrapAlignment.CENTER ->
                (lineWidth - view.measuredWidth + lp.leftMargin - lp.rightMargin) / 2
            else -> lp.leftMargin
        }
    }

    private val LayoutParams.childAlignment get() = when {
        alignSelf != WrapAlignment.AUTO -> alignSelf
        isRowDirection -> alignmentVertical
        else -> alignmentHorizontal
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
        var alignSelf = WrapAlignment.AUTO

        constructor(source: LayoutParams): super(source) {
            alignSelf = source.alignSelf
        }

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
