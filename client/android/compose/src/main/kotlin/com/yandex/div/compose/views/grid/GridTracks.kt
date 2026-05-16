package com.yandex.div.compose.views.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Fr
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.Div
import com.yandex.div2.DivSize

@OptIn(ExperimentalGridApi::class)
@Composable
internal fun List<Div>.computeTracks(
    columnCount: Int,
    widthWrapContent: Boolean,
    heightWrapContent: Boolean,
): Pair<List<GridTrackSize>, List<GridTrackSize>> {
    val freeAtRow = IntArray(columnCount)
    val cells = map { it.value().toCell(freeAtRow, columnCount) }
    val rowCount = freeAtRow.maxOrNull() ?: 0

    val columnTracks = cells.axisTracks(columnCount, isColumn = true, isWrapContent = widthWrapContent)
    val rowTracks = cells.axisTracks(rowCount, isColumn = false, isWrapContent = heightWrapContent)
    return columnTracks to rowTracks
}

@OptIn(ExperimentalGridApi::class)
@Composable
private fun List<Cell>.axisTracks(
    lineCount: Int,
    isColumn: Boolean,
    isWrapContent: Boolean,
): List<GridTrackSize> {
    val maxWeightRatio = (0 until lineCount).maxOfOrNull { line ->
        val weight = lineWeight(line, isColumn)
        if (weight > 0f) lineFixedSize(line, isColumn) / weight else 0f
    } ?: 0f
    return List(lineCount) { line ->
        val weight = lineWeight(line, isColumn)
        val fixedSize = lineFixedSize(line, isColumn)
        when {
            weight > 0f && isWrapContent -> GridTrackSize.Fixed((weight * maxWeightRatio).dp)
            weight > 0f -> GridTrackSize.MinMax((weight * maxWeightRatio).dp, Fr(weight))
            !isLineGrowable(line, isColumn) && fixedSize > 0f -> GridTrackSize.Fixed(fixedSize.dp)
            else -> GridTrackSize.Auto
        }
    }
}

@Composable
private fun List<Cell>.lineWeight(line: Int, isColumn: Boolean): Float {
    var max = 0f
    forEach { cell ->
        val span = if (isColumn) cell.columnSpan else cell.rowSpan
        val start = if (isColumn) cell.columnIndex else cell.rowIndex
        if (line !in start until start + span) return@forEach
        val size = if (isColumn) cell.base.width else cell.base.height
        if (size is DivSize.MatchParent) {
            max = maxOf(max, (size.value.weight?.observedFloatValue() ?: 0f) / span)
        }
    }
    return max
}

@Composable
private fun List<Cell>.lineFixedSize(
    line: Int,
    isColumn: Boolean,
    includeMultiSpan: Boolean = true,
): Float {
    var max = 0f
    forEach { cell ->
        val span = if (isColumn) cell.columnSpan else cell.rowSpan
        if (!includeMultiSpan && span > 1) return@forEach
        val start = if (isColumn) cell.columnIndex else cell.rowIndex
        if (line !in start until start + span) return@forEach
        val size = if (isColumn) cell.base.width else cell.base.height
        if (size !is DivSize.Fixed) return@forEach
        val margins = if (isColumn) cell.base.observeHorizontalMarginsSum()
                      else cell.base.observeVerticalMarginsSum()
        val itemSize = (size.value.observedValue() + margins).value
        max = maxOf(max, lineShareOfFixed(line, start, span, isColumn, itemSize))
    }
    return max
}

@Composable
private fun List<Cell>.lineShareOfFixed(
    line: Int,
    start: Int,
    span: Int,
    isColumn: Boolean,
    itemSize: Float,
): Float {
    if (span == 1) return itemSize
    val range = start until start + span
    val totalFlexWeight = range.sumOf { lineWeight(it, isColumn).toDouble() }.toFloat()
    if (totalFlexWeight > 0f) {
        val weight = lineWeight(line, isColumn)
        if (weight <= 0f) return 0f
        val nonFlexBaseSize = range.sumOf {
            if (lineWeight(it, isColumn) > 0f) 0.0
            else lineFixedSize(it, isColumn, includeMultiSpan = false).toDouble()
        }.toFloat()
        val flexibleSize = (itemSize - nonFlexBaseSize).coerceAtLeast(0f)
        return weight / totalFlexWeight * flexibleSize
    }
    val unusedCount = range.count { !isLineUsed(it, isColumn) }
    return if (unusedCount > 0) {
        if (!isLineUsed(line, isColumn)) itemSize / unusedCount else 0f
    } else {
        itemSize / span
    }
}

private fun List<Cell>.isLineUsed(line: Int, isColumn: Boolean): Boolean = any { cell ->
    val span = if (isColumn) cell.columnSpan else cell.rowSpan
    val start = if (isColumn) cell.columnIndex else cell.rowIndex
    if (span != 1 || start != line) return@any false
    val size = if (isColumn) cell.base.width else cell.base.height
    size !is DivSize.MatchParent
}

@Composable
private fun List<Cell>.isLineGrowable(line: Int, isColumn: Boolean): Boolean = any { cell ->
    val span = if (isColumn) cell.columnSpan else cell.rowSpan
    if (span <= 1) return@any false
    val start = if (isColumn) cell.columnIndex else cell.rowIndex
    if (line !in start until start + span) return@any false
    val range = start until start + span
    when {
        range.any { lineWeight(it, isColumn) > 0f } -> lineWeight(line, isColumn) > 0f
        range.any { !isLineUsed(it, isColumn) } -> !isLineUsed(line, isColumn)
        else -> true
    }
}
