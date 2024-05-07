package com.yandex.div.core.widget.wraplayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.Px
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.yandex.div.core.util.getIndices
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.AspectView.Companion.DEFAULT_ASPECT_RATIO
import com.yandex.div.core.widget.AspectView.Companion.aspectRatioProperty
import com.yandex.div.core.widget.ShowSeparatorsMode
import com.yandex.div.core.widget.dimensionAffecting
import com.yandex.div.internal.widget.DivGravity
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal open class WrapContainerLayout(context: Context) : DivViewGroup(context),
    AspectView {

    @WrapDirection
    var wrapDirection: Int = WrapDirection.ROW
        set(value) {
            if (field != value) {
                field = value
                isRowDirection = when (field) {
                    WrapDirection.ROW -> true
                    WrapDirection.COLUMN -> false
                    else -> throw IllegalStateException("Invalid value for the wrap direction is set: $wrapDirection")
                }
                requestLayout()
            }
        }

    @ShowSeparatorsMode
    var showSeparators by dimensionAffecting(ShowSeparatorsMode.NONE)

    @ShowSeparatorsMode
    var showLineSeparators by dimensionAffecting(ShowSeparatorsMode.NONE)

    var separatorDrawable: Drawable? by dimensionAffecting(null)

    var lineSeparatorDrawable: Drawable? by dimensionAffecting(null)

    fun setSeparatorMargins(left: Int, top: Int, right: Int, bottom: Int) {
        separatorMarginLeft = left
        separatorMarginRight = right
        separatorMarginTop = top
        separatorMarginBottom = bottom

        requestLayout()
    }

    fun setLineSeparatorMargins(left: Int, top: Int, right: Int, bottom: Int) {
        lineSeparatorMarginLeft = left
        lineSeparatorMarginRight = right
        lineSeparatorMarginTop = top
        lineSeparatorMarginBottom = bottom

        requestLayout()
    }

    private var isRowDirection = true

    private val lines: MutableList<WrapLine> = mutableListOf()

    private var childState = 0

    private val separatorLength get() = if (isRowDirection) {
        (separatorDrawable?.intrinsicWidth ?: 0) + separatorMarginLeft + separatorMarginRight
    } else {
        (separatorDrawable?.intrinsicHeight ?: 0) + separatorMarginTop + separatorMarginBottom
    }

    @Px
    private var separatorMarginTop = 0
    @Px
    private var separatorMarginBottom = 0
    @Px
    private var separatorMarginLeft = 0
    @Px
    private var separatorMarginRight = 0

    private val lineSeparatorLength get() = if (isRowDirection) {
        (lineSeparatorDrawable?.intrinsicHeight ?: 0) + lineSeparatorMarginTop + lineSeparatorMarginBottom
    } else {
        (lineSeparatorDrawable?.intrinsicWidth ?: 0) + lineSeparatorMarginLeft + lineSeparatorMarginRight
    }

    @Px
    private var lineSeparatorMarginTop = 0
    @Px
    private var lineSeparatorMarginBottom = 0
    @Px
    private var lineSeparatorMarginLeft = 0
    @Px
    private var lineSeparatorMarginRight = 0

    private var middleLineSeparatorOffset = 0
    private var edgeLineSeparatorOffset = 0
    private val offsetsHolder = OffsetsHolder()

    private var tempSumCrossSize = 0

    override var aspectRatio by aspectRatioProperty()

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
            determineCrossSize(heightSpec, verticalGravity, verticalPaddings)
        } else {
            determineCrossSize(widthMeasureSpec, horizontalGravity, horizontalPaddings)
        }

        val calculatedMaxWidth = if (isRowDirection) largestMainSize else sumOfCrossSize + horizontalPaddings
        val calculatedMaxHeight = if (isRowDirection) sumOfCrossSize + verticalPaddings else largestMainSize

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
        val mainPaddings = edgeSeparatorsLength + if (isRowDirection) horizontalPaddings else verticalPaddings

        var line = WrapLine(mainSize = mainPaddings)

        children.forEachIndexed { index, child ->
            if (child.isHidden) {
                line.goneItemCount++
                line.itemCount++
                addLineIfNeeded(index, line)
                return@forEachIndexed
            }

            val item = child.lp
            var horizontalPaddings = horizontalPaddings + item.horizontalMargins
            var verticalPaddings = verticalPaddings + item.verticalMargins

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
            val childWidth = child.measuredWidth + item.horizontalMargins
            val childHeight = child.measuredHeight + item.verticalMargins
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

    private val Int?.isIncorrectForCrossAxis get() = this == MATCH_PARENT

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
        middleLineSeparatorOffset = 0
        edgeLineSeparatorOffset = 0

        if (lines.size == 0) return

        if (MeasureSpec.getMode(measureSpec) != MeasureSpec.EXACTLY) return

        val size = MeasureSpec.getSize(measureSpec)
        if (lines.size == 1) {
            lines[0].crossSize = size - paddingAlongCrossAxis
            return
        }

        val freeSpace = size - sumOfCrossSize + paddingAlongCrossAxis
        when (crossAlignment) {
            Gravity.RIGHT, Gravity.BOTTOM -> {
                val spaceLine = WrapLine()
                spaceLine.crossSize = freeSpace
                lines.add(0, spaceLine)
            }
            Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL -> {
                val spaceLine = WrapLine()
                spaceLine.crossSize = freeSpace / 2
                addEdgeSpace(spaceLine)
            }
            DivGravity.SPACE_AROUND_HORIZONTAL, DivGravity.SPACE_AROUND_VERTICAL -> {
                val spaceLine = WrapLine()
                getSpaceAroundPart(freeSpace.toFloat(), lines.size).roundToInt().let {
                    spaceLine.crossSize = it
                    middleLineSeparatorOffset = it
                    edgeLineSeparatorOffset = it / 2
                }
                var i = 0
                while (i < lines.size) {
                    lines.add(i, spaceLine)
                    lines.add(i + 2, spaceLine)
                    i += 3
                }
            }
            DivGravity.SPACE_BETWEEN_HORIZONTAL, DivGravity.SPACE_BETWEEN_VERTICAL -> {
                val spaceLine = WrapLine()
                getSpaceBetweenPart(freeSpace.toFloat(), lines.size).roundToInt().let {
                    spaceLine.crossSize = it
                    middleLineSeparatorOffset = it / 2
                }
                addSpaceBetweenLines(spaceLine)
            }
            DivGravity.SPACE_EVENLY_HORIZONTAL, DivGravity.SPACE_EVENLY_VERTICAL -> {
                val spaceLine = WrapLine()
                getSpaceEvenlyPart(freeSpace.toFloat(), lines.size).roundToInt().let {
                    spaceLine.crossSize = it
                    middleLineSeparatorOffset = it / 2
                    edgeLineSeparatorOffset = it / 2
                }
                addSpaceBetweenLines(spaceLine)
                addEdgeSpace(spaceLine)
            }
        }
    }

    private fun addEdgeSpace(spaceLine: WrapLine) {
        lines.add(0, spaceLine)
        lines.add(spaceLine)
    }

    private fun addSpaceBetweenLines(spaceLine: WrapLine) {
        var i = 1
        while (i < lines.size) {
            lines.add(i, spaceLine)
            i += 2
        }
    }

    private fun getSize(mode: Int, size: Int, maxSize: Int, isCrossAxis: Boolean) = when (mode) {
        MeasureSpec.EXACTLY -> size
        MeasureSpec.UNSPECIFIED -> maxSize
        MeasureSpec.AT_MOST -> when {
            isCrossAxis -> min(size, maxSize)
            maxSize > size -> size
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
        var childTop = paddingTop + startLineSeparatorLength
        var needLineSeparator = false
        val layoutDirection = ViewCompat.getLayoutDirection(this)
        val absoluteGravity = GravityCompat.getAbsoluteGravity(horizontalGravity, layoutDirection)
        lines.forEach { line ->
            val leftSeparatorLength = if (isLayoutRtl()) endSeparatorLength else startSeparatorLength
            var childLeft = paddingLeft + leftSeparatorLength.toFloat()
            val freeSpace = (right - left - line.mainSize).toFloat()
            offsetsHolder.let {
                it.update(freeSpace, absoluteGravity, line.itemCountNotGone)
                childLeft += it.firstChildOffset
                line.spaceBetweenChildren = it.spaceBetweenChildren
                line.edgeSeparatorOffset = it.edgeDividerOffset
            }

            if (line.itemCountNotGone > 0) {
                if (needLineSeparator) {
                    childTop += middleLineSeparatorLength
                }
                needLineSeparator = true
            }

            var needSeparator = false
            for (index in getIndices(line.firstIndex, line.itemCount)) {
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
                child.layout(childLeft.roundToInt(), currentTop,
                    childLeft.roundToInt() + child.measuredWidth, currentTop + child.measuredHeight)
                childLeft += child.measuredWidth + lp.rightMargin + line.spaceBetweenChildren
                needSeparator = true
            }
            childTop += line.crossSize
            line.right = childLeft.roundToInt()
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
        val leftSeparatorLength = if (isLayoutRtl()) endLineSeparatorLength else startLineSeparatorLength
        var childLeft = paddingLeft + leftSeparatorLength
        var needLineSeparator = false
        getIndices(0, lines.size).forEach { lineIndex ->
            val line = lines[lineIndex]
            val freeSpace = (bottom - top - line.mainSize).toFloat()
            var childTop = paddingTop + startSeparatorLength.toFloat()
            offsetsHolder.let {
                it.update(freeSpace, verticalGravity, line.itemCountNotGone)
                childTop += it.firstChildOffset
                line.spaceBetweenChildren = it.spaceBetweenChildren
                line.edgeSeparatorOffset = it.edgeDividerOffset
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
                child.layout(currentLeft, childTop.roundToInt(),
                    currentLeft + child.measuredWidth, childTop.roundToInt() + child.measuredHeight)
                childTop += child.measuredHeight + lp.bottomMargin + line.spaceBetweenChildren
                needSeparator = true
            }
            childLeft += line.crossSize
            line.right = childLeft
            line.bottom = childTop.roundToInt()
        }
    }

    private fun getLeftOffsetForVerticalLayout(view: View, lineWidth: Int): Int {
        val lp = view.lp
        val layoutDirection = ViewCompat.getLayoutDirection(this)
        return when (GravityCompat.getAbsoluteGravity(lp.gravity.toHorizontalGravity(), layoutDirection)) {
            Gravity.RIGHT -> lineWidth - view.measuredWidth - lp.rightMargin
            Gravity.CENTER_HORIZONTAL ->
                (lineWidth - view.measuredWidth + lp.leftMargin - lp.rightMargin) / 2
            else -> lp.leftMargin
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
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
        fun drawLineSeparator(separatorBottom: Int) = drawLineSeparator(canvas,
            paddingLeft, separatorBottom - lineSeparatorLength, width - paddingRight, separatorBottom)
        if (lines.size > 0 && showSeparatorAtStart(showLineSeparators)) {
            lineTop = firstVisibleLine?.let { it.bottom - it.crossSize } ?: 0
            drawLineSeparator(lineTop - edgeLineSeparatorOffset)
        }
        var needLineSeparator = false
        lines.forEach { line ->
            if (line.itemCountNotGone == 0) {
                return@forEach
            }

            lineBottom = line.bottom
            lineTop = lineBottom - line.crossSize
            fun drawSeparator(separatorRight: Int) = drawSeparator(canvas,
                separatorRight - separatorLength, lineTop, separatorRight, lineBottom)
            if (needLineSeparator && showSeparatorBetween(showLineSeparators)) {
                drawLineSeparator(lineTop - middleLineSeparatorOffset)
            }
            needLineSeparator = true

            var childLeft: Int
            var childRight = 0
            var needLeftSeparator = true
            for (index in getIndices(line.firstIndex, line.itemCount)) {
                val child = getChildAt(index)
                if (child == null || child.isHidden) {
                    continue
                }

                val lp = child.lp
                childLeft = child.left - lp.leftMargin
                childRight = child.right + lp.rightMargin
                if (needLeftSeparator) {
                    if (showLeftSeparator(showSeparators)) {
                        drawSeparator(childLeft - line.edgeSeparatorOffset)
                    }
                    needLeftSeparator = false
                } else if (showSeparatorBetween(showSeparators)) {
                    drawSeparator(childLeft - (line.spaceBetweenChildren / 2).toInt())
                }
            }
            if (childRight > 0 && showRightSeparator(showSeparators)) {
                drawSeparator(childRight + separatorLength + line.edgeSeparatorOffset)
            }
        }
        if (lineBottom > 0 && showSeparatorAtEnd(showLineSeparators)) {
            drawLineSeparator(lineBottom + lineSeparatorLength + edgeLineSeparatorOffset)
        }
    }

    private fun drawSeparatorsVertical(canvas: Canvas) {
        var lineLeft: Int
        var lineRight = 0
        fun drawLineSeparator(separatorRight: Int) = drawLineSeparator(canvas,
            separatorRight - lineSeparatorLength, paddingTop, separatorRight, height - paddingBottom)
        if (lines.size > 0 && showLeftSeparator(showLineSeparators)) {
            lineLeft = firstVisibleLine?.let { it.right - it.crossSize } ?: 0
            drawLineSeparator(lineLeft - edgeLineSeparatorOffset)
        }
        var needLineSeparator = false
        getIndices(0, lines.size).forEach { lineIndex ->
            val line = lines[lineIndex]
            if (line.itemCountNotGone == 0) {
                return@forEach
            }

            lineRight = line.right
            lineLeft = lineRight - line.crossSize
            fun drawSeparator(separatorBottom: Int) = drawSeparator(canvas,
                lineLeft, separatorBottom - separatorLength, lineRight, separatorBottom)
            if (needLineSeparator && showSeparatorBetween(showLineSeparators)) {
                drawLineSeparator(lineLeft - middleLineSeparatorOffset)
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
                        drawSeparator(childTop - line.edgeSeparatorOffset)
                    }
                    needStartSeparator = false
                } else if (showSeparatorBetween(showSeparators)) {
                    drawSeparator(childTop - (line.spaceBetweenChildren / 2).toInt())
                }
            }
            if (childBottom > 0 && showSeparatorAtEnd(showSeparators)) {
                drawSeparator(childBottom + separatorLength + line.edgeSeparatorOffset)
            }
        }
        if (lineRight > 0 && showRightSeparator(showLineSeparators)) {
            drawLineSeparator(lineRight + lineSeparatorLength + edgeLineSeparatorOffset)
        }
    }

    private fun drawSeparator(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        drawSeparator(separatorDrawable, canvas,
            left + separatorMarginLeft,
            top - separatorMarginTop,
            right - separatorMarginRight,
            bottom + separatorMarginBottom)
    }

    private fun drawLineSeparator(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        drawSeparator(lineSeparatorDrawable, canvas,
            left + lineSeparatorMarginLeft,
            top - lineSeparatorMarginTop,
            right - lineSeparatorMarginRight,
            bottom + lineSeparatorMarginBottom)
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

    private fun showLeftSeparator(@ShowSeparatorsMode mode: Int) =
        if (isLayoutRtl()) showSeparatorAtEnd(mode) else showSeparatorAtStart(mode)

    private fun showRightSeparator(@ShowSeparatorsMode mode: Int) =
        if (isLayoutRtl()) showSeparatorAtStart(mode) else showSeparatorAtEnd(mode)

    private val firstVisibleLine: WrapLine? get() =
        if (isRowDirection || !isLayoutRtl()) lines.find { it.isVisible } else lines.findLast { it.isVisible }

    override fun getBaseline() = firstVisibleLine?.maxBaseline?.plus(paddingTop) ?: super.getBaseline()

    private data class WrapLine(
        val firstIndex: Int = 0,
        var mainSize: Int = 0,
        var itemCount: Int = 0,
    ) {
        var crossSize = 0
        var maxBaseline = -1
        var maxHeightUnderBaseline = 0

        var right = 0
        var bottom = 0

        var goneItemCount = 0

        var edgeSeparatorOffset = 0
        var spaceBetweenChildren = 0f

        val itemCountNotGone get() = itemCount - goneItemCount
        val isVisible get() = itemCountNotGone > 0
    }
}
