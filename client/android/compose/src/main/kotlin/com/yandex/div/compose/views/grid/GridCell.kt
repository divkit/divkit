package com.yandex.div.compose.views.grid

import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivBase

internal data class Cell(
    val base: DivBase,
    val columnIndex: Int,
    val rowIndex: Int,
    val columnSpan: Int,
    val rowSpan: Int,
)

@Composable
internal fun DivBase.toCell(freeAtRow: IntArray, columnCount: Int): Cell {
    val columnSpan = (columnSpan?.observedValue()?.toInt() ?: DEFAULT_SPAN).coerceAtLeast(DEFAULT_SPAN)
    val rowSpan = (rowSpan?.observedValue()?.toInt() ?: DEFAULT_SPAN).coerceAtLeast(DEFAULT_SPAN)
    val startColumn = freeAtRow.indexOfMin()
    val effectiveSpan = columnSpan.coerceAtMost(columnCount - startColumn)
    val rowAtColumn = freeAtRow[startColumn]
    for (column in startColumn until startColumn + effectiveSpan) {
        freeAtRow[column] = rowAtColumn + rowSpan
    }
    return Cell(this, startColumn, rowAtColumn, effectiveSpan, rowSpan)
}

private fun IntArray.indexOfMin(): Int {
    var minIndex = 0
    for (i in 1 until size) if (this[i] < this[minIndex]) minIndex = i
    return minIndex
}

internal const val DEFAULT_SPAN = 1
