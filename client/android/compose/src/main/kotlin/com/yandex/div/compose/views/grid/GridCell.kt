package com.yandex.div.compose.views.grid

import androidx.compose.runtime.Composable
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBase

/**
 * A grid child placed in the grid; [component] is the scope the properties of [base] are read in.
 */
internal data class Cell(
    val base: DivBase,
    val component: DivLocalComponent,
    val columnIndex: Int,
    val rowIndex: Int,
    val columnSpan: Int,
    val rowSpan: Int,
)

@Composable
internal fun DivBase.toCell(
    component: DivLocalComponent,
    freeAtRow: IntArray,
    columnCount: Int,
): Cell {
    val columnSpan = columnSpan.observedSpan(component)
    val rowSpan = rowSpan.observedSpan(component)
    val startColumn = freeAtRow.indexOfMin()
    val effectiveSpan = columnSpan.coerceAtMost(columnCount - startColumn)
    val rowAtColumn = freeAtRow[startColumn]
    for (column in startColumn until startColumn + effectiveSpan) {
        freeAtRow[column] = rowAtColumn + rowSpan
    }
    return Cell(this, component, startColumn, rowAtColumn, effectiveSpan, rowSpan)
}

@Composable
private fun Expression<Long>?.observedSpan(component: DivLocalComponent): Int {
    val span = this?.observedValue(component = component)?.toInt() ?: DEFAULT_SPAN
    return span.coerceAtLeast(DEFAULT_SPAN)
}

private fun IntArray.indexOfMin(): Int {
    var minIndex = 0
    for (i in 1 until size) if (this[i] < this[minIndex]) minIndex = i
    return minIndex
}

internal const val DEFAULT_SPAN = 1
