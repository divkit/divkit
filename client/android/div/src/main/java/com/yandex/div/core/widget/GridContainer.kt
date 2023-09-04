@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.yandex.div.R
import com.yandex.div.internal.KLog
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.DEFAULT_GRAVITY
import com.yandex.div.internal.widget.DivLayoutParams.Companion.DEFAULT_WEIGHT
import com.yandex.div.internal.widget.DivViewGroup
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@SuppressLint("RtlHardcoded")
internal open class GridContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DivViewGroup(context, attrs, defStyleAttr) {

    var columnCount: Int
        get() = grid.columnCount
        set(value) {
            grid.columnCount = value
            invalidateStructure()
            requestLayout()
        }

    val rowCount: Int
        get() = grid.rowCount

    private val grid = Grid()
    private var lastLayoutHashCode = UNINITIALIZED_HASH
    private var initialized = false

    private val paddingHorizontal: Int
        get() = paddingLeft + paddingRight

    private val paddingVertical: Int
        get() = paddingTop + paddingBottom

    init {
        if (isInEditMode) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.GridContainer, defStyleAttr, 0)
            try {
                columnCount = array.getInt(R.styleable.GridContainer_android_columnCount, DEFAULT_COLUMN_COUNT)
                gravity = array.getInt(R.styleable.GridContainer_android_gravity, DEFAULT_GRAVITY)
            } finally {
                array.recycle()
            }
        }

        initialized = true
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        invalidateStructure()
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        invalidateStructure()
    }

    override fun requestLayout() {
        super.requestLayout()
        if (initialized) {
            invalidateMeasurement()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val start = SystemClock.elapsedRealtime()

        checkConsistency()
        invalidateMeasurement()

        val paddingHorizontal = paddingHorizontal
        val paddingVertical = paddingVertical

        val widthSpecWithoutPadding = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec - paddingHorizontal),
            MeasureSpec.getMode(widthMeasureSpec)
        )
        val heightSpecWithoutPadding = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(heightMeasureSpec - paddingVertical),
            MeasureSpec.getMode(heightMeasureSpec)
        )

        measureChildrenInitial(widthSpecWithoutPadding, heightSpecWithoutPadding)

        val contentWidth = grid.measureWidth(widthSpecWithoutPadding)
        remeasureChildrenWidth(widthSpecWithoutPadding, heightSpecWithoutPadding)

        val contentHeight = grid.measureHeight(heightSpecWithoutPadding)
        remeasureChildrenHeight(widthSpecWithoutPadding, heightSpecWithoutPadding)

        val measuredWidth = max(contentWidth + paddingHorizontal, suggestedMinimumWidth)
        val measuredHeight = max(contentHeight + paddingVertical, suggestedMinimumHeight)

        setMeasuredDimension(
            resolveSizeAndState(measuredWidth, widthMeasureSpec, 0),
            resolveSizeAndState(measuredHeight, heightMeasureSpec, 0)
        )

        val duration = SystemClock.elapsedRealtime() - start
        KLog.i(TAG) { "onMeasure() performed in $duration ms" }
    }

    private fun measureChildrenInitial(widthSpec: Int, heightSpec: Int) {
        forEach(significantOnly = true) { child ->
            val params = child.lp
            // At initial measurement only intrinsic view sizes are of interest to us.
            val width = if (params.width == LayoutParams.MATCH_PARENT) 0 else params.width
            val height = if (params.height == LayoutParams.MATCH_PARENT) 0 else params.height
            measureChild(child, widthSpec, heightSpec, width, height)
        }
    }

    private fun measureChild(
        child: View,
        parentWidthSpec: Int,
        parentHeightSpec: Int,
        childWidth: Int,
        childHeight: Int
    ) {
        val childWidthSpec = getChildMeasureSpec(parentWidthSpec, 0,
            childWidth, child.minimumWidth, child.lp.maxWidth)
        val childHeightSpec = getChildMeasureSpec(parentHeightSpec, 0,
            childHeight, child.minimumHeight, child.lp.maxHeight)
        child.measure(childWidthSpec, childHeightSpec)
    }

    private fun remeasureChildrenWidth(widthSpec: Int, heightSpec: Int) {
        val cells = grid.cells
        val columns = grid.columns

        forEachIndexed(significantOnly = true) { child, index ->
            val params = child.lp
            if (params.width != LayoutParams.MATCH_PARENT) {
                return@forEachIndexed
            }

            val cell = cells[index]
            val celldWidth = cell.width(columns) - params.horizontalMargins
            measureMatchParentChild(child, widthSpec, heightSpec, params.width, params.height, celldWidth, 0)
        }
    }

    private fun remeasureChildrenHeight(widthSpec: Int, heightSpec: Int) {
        val cells = grid.cells
        val columns = grid.columns
        val rows = grid.rows

        forEachIndexed(significantOnly = true) { child, index ->
            val params = child.lp
            if (params.height != LayoutParams.MATCH_PARENT) {
                return@forEachIndexed
            }

            val cell = cells[index]
            val celldWidth = cell.width(columns) - params.horizontalMargins
            val cellHeight = cell.height(rows) - params.verticalMargins
            measureMatchParentChild(child, widthSpec, heightSpec, params.width, params.height, celldWidth, cellHeight)
        }
    }

    private fun measureMatchParentChild(
        child: View,
        parentWidthSpec: Int,
        parentHeightSpec: Int,
        childWidth: Int,
        childHeight: Int,
        cellWidth: Int,
        cellHeight: Int
    ) {
        val childWidthSpec = if (childWidth == LayoutParams.MATCH_PARENT) {
            MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY)
        } else {
            getChildMeasureSpec(parentWidthSpec, 0, childWidth, child.minimumWidth, child.lp.maxWidth)
        }
        val childHeightSpec = if (childHeight == LayoutParams.MATCH_PARENT) {
            MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY)
        } else {
            getChildMeasureSpec(parentHeightSpec, 0, childHeight, child.minimumHeight, child.lp.maxHeight)
        }
        child.measure(childWidthSpec, childHeightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val start = SystemClock.elapsedRealtime()

        checkConsistency()

        val columns = grid.columns
        val rows = grid.rows
        val cells = grid.cells

        val offsetLeft = calculateGridHorizontalPosition()
        val offsetTop = calculateGridVerticalPosition()

        forEachIndexed(significantOnly = true) { child, index ->
            val params = child.lp
            val cell = cells[index]

            val cellLeft = cell.left(columns) + params.leftMargin
            val cellTop = cell.top(rows) + params.topMargin
            val cellWidth = cell.right(columns) - cellLeft - params.rightMargin
            val cellHeight = cell.bottom(rows) - cellTop - params.bottomMargin

            var childLeft = calculateChildHorizontalPosition(cellLeft, cellWidth, child.measuredWidth, params.gravity)
            var childTop = calculateChildVerticalPosition(cellTop, cellHeight, child.measuredHeight, params.gravity)

            childLeft += offsetLeft
            childTop += offsetTop

            child.layout(childLeft, childTop, childLeft + child.measuredWidth, childTop + child.measuredHeight)
        }

        val duration = SystemClock.elapsedRealtime() - start
        KLog.i(TAG) { "onLayout() performed in $duration ms" }
    }

    private fun calculateGridHorizontalPosition(): Int {
        val horizontalGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
        val gridWidth = grid.measuredWidth
        val widthWithoutPaddings = measuredWidth - paddingLeft - paddingRight
        return when (horizontalGravity) {
            Gravity.RIGHT -> paddingLeft + widthWithoutPaddings - gridWidth
            Gravity.CENTER_HORIZONTAL -> paddingLeft + (widthWithoutPaddings - gridWidth) / 2
            else -> paddingLeft
        }
    }

    private fun calculateGridVerticalPosition(): Int {
        val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val gridHeight = grid.measuredHeight
        val heightWithoutPaddings = measuredHeight - paddingTop - paddingBottom
        return when (verticalGravity) {
            Gravity.BOTTOM -> paddingTop + heightWithoutPaddings - gridHeight
            Gravity.CENTER_VERTICAL -> paddingTop + (heightWithoutPaddings - gridHeight) / 2
            else -> paddingTop
        }
    }

    private fun calculateChildHorizontalPosition(cellLeft: Int, cellWidth: Int, childWidth: Int, gravity: Int): Int {
        return when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.RIGHT -> cellLeft + cellWidth - childWidth
            Gravity.CENTER_HORIZONTAL -> cellLeft + (cellWidth - childWidth) / 2
            else -> cellLeft
        }
    }

    private fun calculateChildVerticalPosition(cellTop: Int, cellHeight: Int, childHeight: Int, gravity: Int): Int {
        return when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.BOTTOM -> cellTop + cellHeight - childHeight
            Gravity.CENTER_VERTICAL -> cellTop + (cellHeight - childHeight) / 2
            else -> cellTop
        }
    }

    private fun invalidateStructure() {
        lastLayoutHashCode = UNINITIALIZED_HASH
        grid.invalidateStructure()
    }

    private fun invalidateMeasurement() = grid.invalidateMeasurement()

    private fun checkConsistency() {
        if (lastLayoutHashCode == UNINITIALIZED_HASH) {
            validateLayoutParams()
            lastLayoutHashCode = computeLayoutHashCode()
        } else if (lastLayoutHashCode != computeLayoutHashCode()) {
            invalidateStructure()
            checkConsistency()
        }
    }

    private fun computeLayoutHashCode(): Int {
        var result = 223
        forEach(significantOnly = true) { child ->
            result = 31 * result + child.lp.hashCode()
        }
        return result
    }

    private fun validateLayoutParams() {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val params = child.lp

            if (params.columnSpan < 0 || params.rowSpan < 0) {
                throw IllegalStateException("Negative spans are not supported.")
            }

            if (params.columnWeight < 0.0f || params.rowWeight < 0.0f) {
                throw IllegalStateException("Negative weights are not supported.")
            }
        }
    }

    companion object {

        private const val TAG = "GridContainer"
        private const val MAX_SIZE = 32768
        private const val DEFAULT_COLUMN_COUNT = 1
        private const val UNINITIALIZED_HASH = 0
    }

    private class Cell(
        val viewIndex: Int,
        val columnIndex: Int,
        val rowIndex: Int,
        var columnSpan: Int,
        var rowSpan: Int
    )

    private inline fun Cell.left(columns: List<Line>): Int {
        val firstColumn = columns[columnIndex]
        return firstColumn.offset
    }

    private inline fun Cell.right(columns: List<Line>): Int {
        val lastColumn = columns[columnIndex + columnSpan - 1]
        return lastColumn.offset + lastColumn.size
    }

    private inline fun Cell.top(rows: List<Line>): Int {
        val firstRow = rows[rowIndex]
        return firstRow.offset
    }

    private inline fun Cell.bottom(rows: List<Line>): Int {
        val lastRow = rows[rowIndex + rowSpan - 1]
        return lastRow.offset + lastRow.size
    }

    private inline fun Cell.width(columns: List<Line>): Int = right(columns) - left(columns)

    private inline fun Cell.height(rows: List<Line>): Int = bottom(rows) - top(rows)

    private class CellProjection(
        val index: Int,
        val contentSize: Int,
        val marginStart: Int,
        val marginEnd: Int,
        val span: Int,
        val weight: Float
    ) {
        val size
            get() = contentSize + marginStart + marginEnd

        val specificSize
            get() = size / span
    }

    private class Line {

        var offset: Int = 0

        var size: Int = 0
            private set

        var weight: Float = 0.0f
            private set

        val isFlexible: Boolean
            get() = weight > DEFAULT_WEIGHT

        fun include(
            size: Int = 0,
            weight: Float = 0.0f
        ) {
            this.size = max(this.size, size)
            this.weight = max(this.weight, weight)
        }
    }

    private class SizeConstraint(
        var min: Int = 0,
        var max: Int = MAX_SIZE
    ) {
        fun set(measureSpec: Int) {
            val mode = MeasureSpec.getMode(measureSpec)
            val size = MeasureSpec.getSize(measureSpec)
            when (mode) {
                MeasureSpec.UNSPECIFIED -> apply { min = 0; max = MAX_SIZE }
                MeasureSpec.EXACTLY -> apply { min = size; max = size }
                MeasureSpec.AT_MOST -> apply { min = 0; max = size }
            }
        }
    }

    private inner class Grid {

        var columnCount: Int = DEFAULT_COLUMN_COUNT
            set(value) {
                if (value > 0 && field != value) {
                    field = value
                    invalidateStructure()
                }
            }

        val rowCount: Int
            get() = cells.rowCount()

        val cells: List<Cell>
            get() = _cells.get()

        val columns: List<Line>
            get() = _columns.get()

        val rows: List<Line>
            get() = _rows.get()

        val measuredWidth: Int
            get() = if (_columns.initialized) calculateSize(_columns.get()) else 0

        val measuredHeight: Int
            get() = if (_rows.initialized) calculateSize(_rows.get()) else 0

        private val _cells = Resettable { distributeCells() }
        private val _columns = Resettable { measureColumns() }
        private val _rows = Resettable { measureRows() }

        private val widthConstraint = SizeConstraint()
        private val heightConstraint = SizeConstraint()

        private val width: Int
            get() = calculateSize(columns)

        private val height: Int
            get() = calculateSize(rows)

        private fun List<Cell>.rowCount(): Int {
            return if (isEmpty()) 0 else last().run { rowIndex + rowSpan }
        }

        fun invalidateStructure() {
            _cells.reset()
            invalidateMeasurement()
        }

        fun invalidateMeasurement() {
            _columns.reset()
            _rows.reset()
        }

        fun measureWidth(widthSpec: Int): Int {
            widthConstraint.set(widthSpec)
            return max(widthConstraint.min, min(width, widthConstraint.max))
        }

        fun measureHeight(heightSpec: Int): Int {
            heightConstraint.set(heightSpec)
            return max(heightConstraint.min, min(height, heightConstraint.max))
        }

        private fun calculateSize(lines: List<Line>) : Int {
            if (lines.isEmpty()) return 0
            val lastLine = lines.last()
            return lastLine.offset + lastLine.size
        }

        private fun distributeCells(): List<Cell> {
            if (childCount == 0) return emptyList()

            val columnCount = columnCount
            val cells = ArrayList<Cell>(childCount)

            val cellIndices = IntArray(columnCount)
            val cellHeights = IntArray(columnCount)
            var column = 0
            var row = 0

            forEachIndexed(significantOnly = true) { child, index ->
                val minHeight = cellHeights.minOrNull() ?: 0
                column = cellHeights.indexOf(minHeight)
                row += minHeight
                cellHeights.update { max(0, it - minHeight) }

                val params = child.lp
                val columnSpan = min(params.columnSpan, columnCount - column)
                val rowSpan = params.rowSpan

                cells.add(Cell(index, column, row, columnSpan, rowSpan))
                for (i in column until column + columnSpan) {
                    if (cellHeights[i] > 0) {
                        val cellToTrim = cells[cellIndices[i]]
                        cellHeights.update(cellToTrim.columnIndex, cellToTrim.columnSpan) { 0 }
                        cellToTrim.rowSpan = row - cellToTrim.rowIndex
                    }
                    cellIndices[i] = index
                    cellHeights[i] = rowSpan
                }
            }

            val lastSpan = cellHeights.minByOrNull { max(1, it) } ?: 1
            val rowCount = cells.last().rowIndex + lastSpan
            cells.iterate { cell ->
                if (cell.rowIndex + cell.rowSpan > rowCount) {
                    cell.rowSpan = rowCount - cell.rowIndex
                }
            }
            return cells
        }

        private fun measureColumns(): List<Line> {
            return measureAxis(columnCount, widthConstraint) { cell, view ->
                val params = view.lp
                CellProjection(
                    index = cell.columnIndex,
                    contentSize = view.measuredWidth,
                    marginStart = params.leftMargin,
                    marginEnd = params.rightMargin,
                    span = cell.columnSpan,
                    weight = params.columnWeight
                )
            }
        }

        private fun measureRows(): List<Line> {
            return measureAxis(rowCount, heightConstraint) { cell, view ->
                val params = view.lp
                CellProjection(
                    index = cell.rowIndex,
                    contentSize = view.measuredHeight,
                    marginStart = params.topMargin,
                    marginEnd = params.bottomMargin,
                    span = cell.rowSpan,
                    weight = params.rowWeight
                )
            }
        }

        private inline fun measureAxis(
            count: Int,
            constraint: SizeConstraint,
            projection: (Cell, View) -> CellProjection
        ): List<Line> {
            val cells = _cells.get()
            val result = List(count) { Line() }

            applyFixedParamsToLines(cells, result, projection)
            applySpansToLines(cells, result, projection)
            adjustWeightedLines(result, constraint)
            align(result)

            return result
        }

        private inline fun applyFixedParamsToLines(
            cells: List<Cell>,
            lines: List<Line>,
            projection: (Cell, View) -> CellProjection
        ) {
            cells.iterate { cell ->
                val child = getChildAt(cell.viewIndex)
                val projected = projection(cell, child)
                if (projected.span == 1) {
                    val measurement = lines[projected.index]
                    measurement.include(size = projected.size, weight = projected.weight)
                } else {
                    val first = 0
                    val last = projected.span - 1
                    val weight = projected.weight / projected.span
                    for (i in first .. last) {
                        val measurement = lines[projected.index + i]
                        measurement.include(weight = weight)
                    }
                }
            }
        }

        private inline fun applySpansToLines(
            cells: List<Cell>,
            lines: List<Line>,
            projection: (Cell, View) -> CellProjection
        ) {
            val spannedCells = ArrayList<CellProjection>()
            cells.iterate { cell ->
                val child = getChildAt(cell.viewIndex)
                val projected = projection(cell, child)
                if (projected.span > 1) {
                    spannedCells.add(projected)
                }
            }
            spannedCells.sortWith(SpannedCellComparator)

            spannedCells.iterate { projected ->
                val first = projected.index
                val last = projected.index + projected.span - 1

                var undistributedSize = projected.size
                var flexibleSize = undistributedSize
                var totalWeight = 0.0f
                var unusedLineCount = 0
                for (i in first .. last) {
                    val line = lines[i]
                    undistributedSize -= line.size
                    if (line.isFlexible) {
                        totalWeight += line.weight
                    } else {
                        if (line.size == 0) unusedLineCount++
                        flexibleSize -= line.size
                    }
                }
                if (totalWeight > 0.0f) {
                    for (i in first..last) {
                        val line = lines[i]
                        if (line.isFlexible) {
                            val size = ceil(line.weight / totalWeight * flexibleSize).toInt()
                            line.include(size = size)
                        }
                    }
                } else if (undistributedSize > 0) {
                    for (i in first..last) {
                        val line = lines[i]
                        if (unusedLineCount > 0) {
                            if (line.size == 0 && !line.isFlexible) {
                                line.include(size = line.size + undistributedSize / unusedLineCount)
                            }
                        } else {
                            line.include(size = line.size + undistributedSize / projected.span)
                        }
                    }
                }
            }
        }

        private fun adjustWeightedLines(
            lines: List<Line>,
            constraint: SizeConstraint
        ) {
            var totalSize = 0
            var totalFixedSize = 0
            var totalWeight = 0.0f
            var maxWeightedSize = 0.0f
            lines.iterate { line ->
                if (line.isFlexible) {
                    totalWeight += line.weight
                    maxWeightedSize = max(maxWeightedSize, line.size / line.weight)
                } else {
                    totalFixedSize += line.size
                }
                totalSize += line.size
            }

            var maxTotalSize = 0
            lines.iterate { line ->
                maxTotalSize += if (line.isFlexible) {
                    ceil((line.weight * maxWeightedSize)).toInt()
                } else {
                    line.size
                }
            }

            val desiredTotalSize = max(constraint.min, maxTotalSize)
            val totalWeightedSize = max(0, desiredTotalSize - totalFixedSize)
            val weightedSize = totalWeightedSize / totalWeight
            lines.iterate { line ->
                if (line.isFlexible) {
                    val size = ceil((line.weight * (weightedSize))).toInt()
                    line.include(size = size)
                }
            }
        }

        private fun align(lines: List<Line>) {
            var totalOffset = 0
            for (i in lines.indices) {
                val measurement = lines[i]
                measurement.offset = totalOffset
                totalOffset += measurement.size
            }
        }
    }

    private object SpannedCellComparator : Comparator<CellProjection> {
        override fun compare(lhs: CellProjection, rhs: CellProjection): Int {
            return when {
                lhs.specificSize < rhs.specificSize -> 1
                lhs.specificSize > rhs.specificSize -> -1
                else -> 0
            }
        }
    }
}

private class Resettable<T>(private val initializer: () -> T) {

    val initialized: Boolean
        get() = value != null

    private var value: T? = null

    fun get(): T {
        if (value == null) {
            value = initializer()
        }
        return value ?: throw ConcurrentModificationException("Set to null by another thread")
    }

    fun reset() {
        value = null
    }
}

private val DivLayoutParams.rowWeight: Float
    get() = verticalWeight

private val DivLayoutParams.columnWeight: Float
    get() = horizontalWeight
