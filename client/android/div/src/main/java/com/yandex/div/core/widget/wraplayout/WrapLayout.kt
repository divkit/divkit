package com.yandex.div.core.widget.wraplayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.Px
import androidx.core.view.children
import kotlin.math.max

internal open class WrapLayout(context: Context) : ViewGroup(context) {

    @WrapDirection
    var wrapDirection: Int = WrapDirection.ROW
        set(value) {
            if (field != value) {
                field = value
                when (field) {
                    WrapDirection.ROW -> {
                        isRowDirection = true
                        separatorLength = separatorDrawable?.intrinsicWidth ?: 0
                        lineSeparatorLength = lineSeparatorDrawable?.intrinsicHeight ?: 0
                    }
                    WrapDirection.COLUMN -> {
                        isRowDirection = false
                        separatorLength = separatorDrawable?.intrinsicHeight ?: 0
                        lineSeparatorLength = lineSeparatorDrawable?.intrinsicWidth ?: 0
                    }
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

    @WrapShowSeparatorsMode
    var showSeparators: Int = WrapShowSeparatorsMode.NONE
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    @WrapShowSeparatorsMode
    var showLineSeparators: Int = WrapShowSeparatorsMode.NONE
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    var separatorDrawable: Drawable? = null
        set(value) {
            if (field != value) {
                field = value
                separatorLength = value?.run {
                    if (isRowDirection) intrinsicWidth else intrinsicHeight
                } ?: 0
                requestLayout()
            }
        }

    var lineSeparatorDrawable: Drawable? = null
        set(value) {
            if (field != value) {
                field = value
                lineSeparatorLength = value?.run {
                    if (isRowDirection) intrinsicHeight else intrinsicWidth
                } ?: 0
                requestLayout()
            }
        }

    private var isRowDirection = true

    private val lines: MutableList<WrapLine> = mutableListOf()

    private var childState = 0

    @Px
    private var separatorLength = 0
    @Px
    private var lineSeparatorLength = 0

    private var tempSumCrossSize = 0

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
        tempSumCrossSize = edgeLineSeparatorsLength

        val mainMeasureSpec = if (isRowDirection) widthMeasureSpec else heightMeasureSpec
        val mainMode = MeasureSpec.getMode(mainMeasureSpec)
        val mainSize = MeasureSpec.getSize(mainMeasureSpec)
        val parentHorizontalPaddings = paddingLeft + paddingRight
        val parentVerticalPaddings = paddingTop + paddingBottom
        val mainPaddings = edgeSeparatorsLength +
            if (isRowDirection) parentHorizontalPaddings else parentVerticalPaddings

        var line = WrapLine(mainSize = mainPaddings)

        children.forEachIndexed { index, child ->
            if (child.isHidden) {
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
                horizontalPaddings += edgeSeparatorsLength
                verticalPaddings += tempSumCrossSize
            } else {
                horizontalPaddings += tempSumCrossSize
                verticalPaddings += edgeSeparatorsLength
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

            if (isWrapRequired(mainMode, mainSize, line.mainSize, childMainSize, line.itemCount)) {
                if (line.itemCountNotGone > 0) {
                    addLine(line)
                }
                line = WrapLine(firstIndex = index, mainSize = mainPaddings, itemCount = 1)
                largestSizeInCross = Int.MIN_VALUE
            } else {
                if (line.itemCount > 0) {
                    line.mainSize += middleSeparatorLength
                }
                line.itemCount++
            }

            if (isRowDirection && item.isBaselineAligned) {
                line.maxBaseline = max(line.maxBaseline, child.baseline + item.topMargin)
                line.maxHeightUnderBaseline = max(line.maxHeightUnderBaseline,
                    child.measuredHeight + item.bottomMargin - child.baseline)
            }

            line.mainSize += childMainSize
            largestSizeInCross = max(largestSizeInCross, childCrossSize)
            line.crossSize = max(line.crossSize, largestSizeInCross)

            addLineIfNeeded(index, line)
        }
    }

    private val edgeSeparatorsLength get() = startSeparatorLength + endSeparatorLength

    private val edgeLineSeparatorsLength get() = startLineSeparatorLength + endLineSeparatorLength

    private val startSeparatorLength get() =
        if (showSeparatorAtStart(showSeparators)) separatorLength else 0

    private val middleSeparatorLength get() =
        if (showSeparatorBetween(showSeparators)) separatorLength else 0

    private val endSeparatorLength get() =
        if (showSeparatorAtEnd(showSeparators)) separatorLength else 0

    private val startLineSeparatorLength get() =
        if (showSeparatorAtStart(showLineSeparators)) lineSeparatorLength else 0

    private val middleLineSeparatorLength get() =
        if (showSeparatorBetween(showLineSeparators)) lineSeparatorLength else 0

    private val endLineSeparatorLength get() =
        if (showSeparatorAtEnd(showLineSeparators)) lineSeparatorLength else 0

    private fun showSeparatorAtStart(@WrapShowSeparatorsMode mode: Int) =
        (mode and WrapShowSeparatorsMode.SHOW_AT_START) != 0

    private fun showSeparatorBetween(@WrapShowSeparatorsMode mode: Int) =
        (mode and WrapShowSeparatorsMode.SHOW_BETWEEN) != 0

    private fun showSeparatorAtEnd(@WrapShowSeparatorsMode mode: Int) =
        (mode and WrapShowSeparatorsMode.SHOW_AT_END) != 0

    private val View.isHidden get() = visibility == View.GONE || hasIncorrectSize

    private val View.hasIncorrectSize get() = if (isRowDirection) {
        layoutParams?.height == MATCH_PARENT
    } else {
        layoutParams?.width == MATCH_PARENT
    }

    private val LayoutParams.isBaselineAligned get() = alignSelf == WrapAlignment.BASELINE

    private fun addLineIfNeeded(childIndex: Int, line: WrapLine) {
        val isLastItem = childIndex == childCount - 1 && line.itemCountNotGone != 0
        if (isLastItem) {
            addLine(line)
        }
    }

    private fun addLine(line: WrapLine) {
        lines.add(line)
        if (line.maxBaseline > 0) {
            line.crossSize = max(line.crossSize, line.maxBaseline + line.maxHeightUnderBaseline)
        }
        tempSumCrossSize += line.crossSize
    }

    private fun isWrapRequired(
        mode: Int,
        maxSize: Int,
        currentLength: Int,
        childLength: Int,
        lineItemsCount: Int
    ): Boolean {
        val length = currentLength + childLength +
            if (lineItemsCount != 0) middleSeparatorLength else 0
        return mode != MeasureSpec.UNSPECIFIED && maxSize < length
    }

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
        if (lines.size == 1) {
            lines[0].crossSize = size - paddingAlongCrossAxis
            return
        }

        val totalCrossSize: Int = sumOfCrossSize + paddingAlongCrossAxis
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

    private val sumOfCrossSize get() = lines.sumOf { it.crossSize } + edgeLineSeparatorsLength +
        middleLineSeparatorLength * (lines.count { it.itemCountNotGone > 0 } - 1)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isRowDirection) {
            layoutHorizontal(left, right)
        } else {
            layoutVertical(top, bottom)
        }
    }

    private fun layoutHorizontal(left: Int, right: Int) {
        val width = right - left
        var childTop = paddingTop + startLineSeparatorLength

        var childLeft: Int
        var needLineSeparator = false
        lines.forEach { line ->
            childLeft = startSeparatorLength + when (alignmentHorizontal) {
                WrapAlignment.START -> paddingLeft
                WrapAlignment.END -> width - line.mainSize - paddingRight
                WrapAlignment.CENTER -> paddingLeft + (width - line.mainSize) / 2
                else -> throw java.lang.IllegalStateException(
                    "Invalid alignmentHorizontal is set: $alignmentHorizontal")
            }

            if (line.itemCountNotGone > 0) {
                if (needLineSeparator) {
                    childTop += middleLineSeparatorLength
                }
                needLineSeparator = true
            }

            var needSeparator = false
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.isHidden) {
                    if (child.hasIncorrectSize) {
                        child.layout(0, 0, 0, 0)
                    }
                    continue
                }
                val lp = child.layoutParams as LayoutParams
                childLeft += lp.leftMargin
                if (needSeparator) {
                    childLeft += middleSeparatorLength
                }
                val currentTop = childTop + getTopOffsetForHorizontalLayout(child, line)
                child.layout(childLeft, currentTop,
                    childLeft + child.measuredWidth, currentTop + child.measuredHeight)
                childLeft += child.measuredWidth + lp.rightMargin
                needSeparator = true
            }
            childTop += line.crossSize
            line.right = childLeft
            line.bottom = childTop
        }
    }

    private fun getTopOffsetForHorizontalLayout(view: View, line: WrapLine): Int {
        val lp = view.layoutParams as LayoutParams
        return when (lp.alignSelf) {
            WrapAlignment.END -> line.crossSize - view.measuredHeight - lp.bottomMargin
            WrapAlignment.CENTER ->
                (line.crossSize - view.measuredHeight + lp.topMargin - lp.bottomMargin) / 2
            WrapAlignment.BASELINE -> max(line.maxBaseline - view.baseline, lp.topMargin)
            else -> lp.topMargin
        }
    }

    private fun layoutVertical(top: Int, bottom: Int) {
        val height = bottom - top
        var childLeft = paddingLeft + startLineSeparatorLength

        var childTop: Int
        var needLineSeparator = false
        lines.forEach { line ->
            childTop = startSeparatorLength + when (alignmentVertical) {
                WrapAlignment.START -> paddingTop
                WrapAlignment.END -> height - line.mainSize + paddingBottom
                WrapAlignment.CENTER -> paddingTop + (height - line.mainSize) / 2
                else -> throw java.lang.IllegalStateException(
                    "Invalid alignmentVertical is set: $alignmentVertical")
            }

            if (line.itemCountNotGone > 0) {
                if (needLineSeparator) {
                    childLeft += middleLineSeparatorLength
                }
                needLineSeparator = true
            }

            var needSeparator = false
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.isHidden) {
                    if (child.hasIncorrectSize) {
                        child.layout(0, 0, 0, 0)
                    }
                    continue
                }
                val lp = child.layoutParams as LayoutParams
                childTop += lp.topMargin
                if (needSeparator) {
                    childTop += middleSeparatorLength
                }
                val currentLeft = childLeft + getLeftOffsetForVerticalLayout(child, line.crossSize)
                child.layout(currentLeft, childTop,
                    currentLeft + child.measuredWidth, childTop + child.measuredHeight)
                childTop += child.measuredHeight + lp.bottomMargin
                needSeparator = true
            }
            childLeft += line.crossSize
            line.right = childLeft
            line.bottom = childTop
        }
    }

    private fun getLeftOffsetForVerticalLayout(view: View, lineWidth: Int): Int {
        val lp = view.layoutParams as LayoutParams
        return when (lp.alignSelf) {
            WrapAlignment.END -> lineWidth - view.measuredWidth - lp.rightMargin
            WrapAlignment.CENTER ->
                (lineWidth - view.measuredWidth + lp.leftMargin - lp.rightMargin) / 2
            else -> lp.leftMargin
        }
    }

    override fun onDraw(canvas: Canvas) {
        separatorDrawable ?: lineSeparatorDrawable ?: return
        if (showSeparators == WrapShowSeparatorsMode.NONE &&
            showLineSeparators == WrapShowSeparatorsMode.NONE) {
            return
        }
        if (isRowDirection) {
            drawSeparatorsHorizontal(canvas)
        } else {
            drawSeparatorsVertical(canvas)
        }
    }

    private fun drawSeparatorsHorizontal(canvas: Canvas) {
        var lineTop: Int
        var lineBottom = 0
        val drawLineSeparator = { top: Int -> drawSeparator(lineSeparatorDrawable, canvas,
            paddingLeft, top - lineSeparatorLength, width - paddingRight, top) }
        if (lines.size > 0 && showSeparatorAtStart(showLineSeparators)) {
            lineTop = firstVisibleLine?.let { it.bottom - it.crossSize } ?: 0
            drawLineSeparator(lineTop)
        }
        var needLineSeparator = false
        lines.forEach { line ->
            if (line.itemCountNotGone == 0) {
                return@forEach
            }

            lineBottom = line.bottom
            lineTop = lineBottom - line.crossSize
            if (needLineSeparator && showSeparatorBetween(showLineSeparators)) {
                drawLineSeparator(lineTop)
            }
            needLineSeparator = true

            var childLeft: Int
            var childRight = 0
            var needStartSeparator = true
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.isHidden) {
                    continue
                }

                val lp = child.layoutParams as LayoutParams
                childLeft = child.left - lp.leftMargin
                childRight = child.right + lp.rightMargin
                if (needStartSeparator) {
                    if (showSeparatorAtStart(showSeparators)) {
                        drawSeparator(separatorDrawable, canvas,
                            childLeft - separatorLength, lineTop, childLeft, lineBottom)
                    }
                    needStartSeparator = false
                } else if (showSeparatorBetween(showSeparators)) {
                    drawSeparator(separatorDrawable, canvas,
                        childLeft - separatorLength, lineTop, childLeft, lineBottom)
                }
            }
            if (childRight > 0 && showSeparatorAtEnd(showSeparators)) {
                drawSeparator(separatorDrawable, canvas,
                    childRight, lineTop, childRight + separatorLength, lineBottom)
            }
        }
        if (lineBottom > 0 && showSeparatorAtEnd(showLineSeparators)) {
            drawLineSeparator(lineBottom + lineSeparatorLength)
        }
    }

    private fun drawSeparatorsVertical(canvas: Canvas) {
        var lineLeft: Int
        var lineRight = 0
        val drawLineSeparator = { left: Int -> drawSeparator(lineSeparatorDrawable, canvas,
            left - lineSeparatorLength, paddingTop, left, height - paddingBottom) }
        if (lines.size > 0 && showSeparatorAtStart(showLineSeparators)) {
            lineLeft = firstVisibleLine?.let { it.right - it.crossSize } ?: 0
            drawLineSeparator(lineLeft)
        }
        var needLineSeparator = false
        lines.forEach { line ->
            if (line.itemCountNotGone == 0) {
                return@forEach
            }

            lineRight = line.right
            lineLeft = lineRight - line.crossSize
            if (needLineSeparator && showSeparatorBetween(showLineSeparators)) {
                drawLineSeparator(lineLeft)
            }
            needLineSeparator = lineSeparatorDrawable != null

            var childTop: Int
            var childBottom = 0
            var needStartSeparator = true
            for (j in 0 until line.itemCount) {
                val index = line.firstIndex + j
                val child = getChildAt(index)
                if (child == null || child.isHidden) {
                    continue
                }

                val lp = child.layoutParams as LayoutParams
                childTop = child.top - lp.topMargin
                childBottom = child.bottom + lp.bottomMargin
                if (needStartSeparator) {
                    if (showSeparatorAtStart(showSeparators)) {
                        drawSeparator(separatorDrawable, canvas,
                            lineLeft, childTop - separatorLength, lineRight, childTop)
                    }
                    needStartSeparator = false
                } else if (showSeparatorBetween(showSeparators)) {
                    drawSeparator(separatorDrawable, canvas,
                        lineLeft, childTop - separatorLength, lineRight, childTop)
                }
            }
            if (childBottom > 0 && showSeparatorAtEnd(showSeparators)) {
                drawSeparator(separatorDrawable, canvas,
                    lineLeft, childBottom, lineRight, childBottom + separatorLength)
            }
        }
        if (lineRight > 0 && showSeparatorAtEnd(showLineSeparators)) {
            drawLineSeparator(lineRight + lineSeparatorLength)
        }
    }

    private fun drawSeparator(
        separator: Drawable?, canvas: Canvas,
        left: Int, top: Int, right: Int, bottom: Int
    ) = separator?.run {
        val centerHorizontal = (left + right) / 2f
        val centerVertical = (top + bottom) / 2f
        val halfWidth = intrinsicWidth / 2f
        val halfHeight = intrinsicHeight / 2f
        setBounds((centerHorizontal - halfWidth).toInt(), (centerVertical - halfHeight).toInt(),
            (centerHorizontal + halfWidth).toInt(), (centerVertical + halfHeight).toInt())
        draw(canvas)
    }

    private val firstVisibleLine get() = lines.find { it.itemCountNotGone > 0 }

    override fun getBaseline() = firstVisibleLine?.maxBaseline ?: super.getBaseline()

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?) = p is LayoutParams

    override fun generateLayoutParams(attrs: AttributeSet?) = LayoutParams(context, attrs)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams =
        when (lp) {
            is LayoutParams -> LayoutParams(lp)
            is MarginLayoutParams -> LayoutParams(lp)
            else -> LayoutParams(lp)
        }

    internal class LayoutParams : MarginLayoutParams {
        var alignSelf = WrapAlignment.START

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
        var maxBaseline: Int = -1,
        var maxHeightUnderBaseline: Int = 0,

        var right: Int = 0,
        var bottom: Int = 0,

        var itemCount: Int = 0,
        var goneItemCount: Int = 0
    ) {
        val itemCountNotGone get() = itemCount - goneItemCount
    }
}
