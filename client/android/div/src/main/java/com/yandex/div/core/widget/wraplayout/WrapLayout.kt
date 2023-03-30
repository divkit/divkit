package com.yandex.div.core.widget.wraplayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.Px
import androidx.core.view.children
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.AspectView.Companion.DEFAULT_ASPECT_RATIO
import com.yandex.div.core.widget.ShowSeparatorsMode
import com.yandex.div.core.widget.dimensionAffecting
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal open class WrapLayout(context: Context) : DivViewGroup(context), AspectView {

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

    var gravity: Int = Gravity.LEFT or Gravity.TOP
        set(value) {
            if (field == value) return

            var newGravity = value
            if (newGravity.toHorizontalGravity() == 0) {
                newGravity = newGravity or Gravity.LEFT
            }
            if (newGravity.toVerticalGravity() == 0) {
                newGravity = newGravity or Gravity.TOP
            }
            field = newGravity
            requestLayout()
        }

    private fun Int.toHorizontalGravity() = this and Gravity.HORIZONTAL_GRAVITY_MASK

    private fun Int.toVerticalGravity() = this and Gravity.VERTICAL_GRAVITY_MASK

    @ShowSeparatorsMode
    var showSeparators: Int = ShowSeparatorsMode.NONE
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    @ShowSeparatorsMode
    var showLineSeparators: Int = ShowSeparatorsMode.NONE
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

    override var aspectRatio by dimensionAffecting(DEFAULT_ASPECT_RATIO) { it.coerceAtLeast(DEFAULT_ASPECT_RATIO) }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        lines.clear()
        childState = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        var heightMode: Int
        var heightSize: Int
        var heightSpec: Int
        if (aspectRatio != DEFAULT_ASPECT_RATIO && widthMode == MeasureSpec.EXACTLY) {
            heightSize = (widthSize / aspectRatio).roundToInt()
            heightMode = MeasureSpec.EXACTLY
            heightSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        } else {
            heightSpec = heightMeasureSpec
            heightMode = MeasureSpec.getMode(heightSpec)
            heightSize = MeasureSpec.getSize(heightSpec)
        }

        calculateLines(widthMeasureSpec, heightSpec)

        if (isRowDirection) {
            determineCrossSize(heightSpec, gravity.toVerticalGravity(), paddingTop + paddingBottom)
        } else {
            determineCrossSize(widthMeasureSpec, gravity.toHorizontalGravity(), paddingLeft + paddingRight)
        }

        val calculatedMaxWidth =
            if (isRowDirection) largestMainSize else sumOfCrossSize + paddingLeft + paddingRight
        val calculatedMaxHeight =
            if (isRowDirection) sumOfCrossSize + paddingTop + paddingBottom else largestMainSize

        childState = getState(widthMode, childState, widthSize, calculatedMaxWidth,
            MEASURED_STATE_TOO_SMALL)
        val widthSizeAndState = resolveSizeAndState(
            getSize(widthMode, widthSize, calculatedMaxWidth, !isRowDirection),
            widthMeasureSpec,
            childState
        )

        if (isRowDirection && aspectRatio != DEFAULT_ASPECT_RATIO && widthMode != MeasureSpec.EXACTLY) {
            heightSize = ((widthSizeAndState and View.MEASURED_SIZE_MASK) / aspectRatio).roundToInt()
            heightMode = MeasureSpec.EXACTLY
            heightSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        }

        childState = getState(heightMode, childState, heightSize, calculatedMaxHeight,
            MEASURED_STATE_TOO_SMALL shr MEASURED_HEIGHT_STATE_SHIFT)
        val heightSizeAndState = resolveSizeAndState(
            getSize(heightMode, heightSize, calculatedMaxHeight, isRowDirection),
            heightSpec,
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

            val item = child.lp
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
                getChildMeasureSpec(widthMeasureSpec, horizontalPaddings, item.width, child.minimumWidth, item.maxWidth)
            val childHeightMeasureSpec =
                getChildMeasureSpec(heightMeasureSpec, verticalPaddings,
                    item.height, child.minimumHeight, item.maxHeight)

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

    private fun showSeparatorAtStart(@ShowSeparatorsMode mode: Int) =
        (mode and ShowSeparatorsMode.SHOW_AT_START) != 0

    private fun showSeparatorBetween(@ShowSeparatorsMode mode: Int) =
        (mode and ShowSeparatorsMode.SHOW_BETWEEN) != 0

    private fun showSeparatorAtEnd(@ShowSeparatorsMode mode: Int) =
        (mode and ShowSeparatorsMode.SHOW_AT_END) != 0

    private val View.isHidden get() = visibility == View.GONE || hasIncorrectSize

    private val View.hasIncorrectSize get() = if (isRowDirection) {
        layoutParams?.height.isIncorrectForCrossAxis
    } else {
        layoutParams?.width.isIncorrectForCrossAxis
    }

    private val Int?.isIncorrectForCrossAxis get() =
        this == MATCH_PARENT || this == DivLayoutParams.WRAP_CONTENT_CONSTRAINED

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
        crossAlignment: Int,
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
            Gravity.RIGHT, Gravity.BOTTOM -> {
                val spaceTop = size - totalCrossSize
                val spaceLine = WrapLine()
                spaceLine.crossSize = spaceTop
                lines.add(0, spaceLine)
            }
            Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL -> {
                val spaceAboveAndBottom = (size - totalCrossSize) / 2
                val spaceLine = WrapLine()
                spaceLine.crossSize = spaceAboveAndBottom
                lines.add(0, spaceLine)
                lines.add(spaceLine)
            }
            else -> Unit
        }
    }

    private fun getSize(mode: Int, size: Int, maxSize: Int, isCrossAxis: Boolean) = when (mode) {
        MeasureSpec.EXACTLY -> size
        MeasureSpec.UNSPECIFIED -> maxSize
        MeasureSpec.AT_MOST -> when {
            isCrossAxis -> min(size, maxSize)
            maxSize < size -> size
            visibleLinesCount > 1 -> size
            else -> maxSize
        }
        else -> throw IllegalStateException("Unknown size mode is set: $mode")
    }

    private val visibleLinesCount get() = lines.count { it.itemCountNotGone > 0 }

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
        middleLineSeparatorLength * (visibleLinesCount - 1)

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
            childLeft = startSeparatorLength + when (val gravityHorizontal = gravity.toHorizontalGravity()) {
                Gravity.LEFT -> paddingLeft
                Gravity.RIGHT -> width - line.mainSize - paddingRight
                Gravity.CENTER_HORIZONTAL -> paddingLeft + (width - line.mainSize) / 2
                else -> throw java.lang.IllegalStateException(
                    "Invalid horizontal gravity is set: $gravityHorizontal")
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
                val lp = child.lp
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
        val lp = view.lp
        return when (lp.gravity.toVerticalGravity()) {
            Gravity.BOTTOM -> line.crossSize - view.measuredHeight - lp.bottomMargin
            Gravity.CENTER_VERTICAL -> (line.crossSize - view.measuredHeight + lp.topMargin - lp.bottomMargin) / 2
            else -> if (lp.isBaselineAligned) max(line.maxBaseline - view.baseline, lp.topMargin) else lp.topMargin
        }
    }

    private fun layoutVertical(top: Int, bottom: Int) {
        val height = bottom - top
        var childLeft = paddingLeft + startLineSeparatorLength

        var childTop: Int
        var needLineSeparator = false
        lines.forEach { line ->
            childTop = startSeparatorLength + when (val gravityVertical = gravity.toVerticalGravity()) {
                Gravity.TOP -> paddingTop
                Gravity.BOTTOM -> height - line.mainSize + paddingBottom
                Gravity.CENTER_VERTICAL -> paddingTop + (height - line.mainSize) / 2
                else -> throw java.lang.IllegalStateException(
                    "Invalid vertical gravity is set: $gravityVertical")
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
                val lp = child.lp
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
        val lp = view.lp
        return when (lp.gravity.toHorizontalGravity()) {
            Gravity.RIGHT -> lineWidth - view.measuredWidth - lp.rightMargin
            Gravity.CENTER_HORIZONTAL ->
                (lineWidth - view.measuredWidth + lp.leftMargin - lp.rightMargin) / 2
            else -> lp.leftMargin
        }
    }

    override fun onDraw(canvas: Canvas) {
        separatorDrawable ?: lineSeparatorDrawable ?: return
        if (showSeparators == ShowSeparatorsMode.NONE && showLineSeparators == ShowSeparatorsMode.NONE) {
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

                val lp = child.lp
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

                val lp = child.lp
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

    override fun getBaseline() = firstVisibleLine?.maxBaseline?.plus(paddingTop) ?: super.getBaseline()

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
