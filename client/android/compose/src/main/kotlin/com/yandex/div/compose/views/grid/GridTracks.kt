package com.yandex.div.compose.views.grid

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Fr
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.observeHorizontalMarginsSum
import com.yandex.div.compose.utils.observeVerticalMarginsSum
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.core.resolveWeightedSizes
import com.yandex.div2.Div
import com.yandex.div2.DivSize

@OptIn(ExperimentalGridApi::class)
@Composable
internal fun List<Div>.computeTracks(
    columnCount: Int,
    widthWrapContent: Boolean,
    heightWrapContent: Boolean,
): Pair<AxisTracks, AxisTracks> {
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
): AxisTracks {
    val weights = observeLineWeights(lineCount, isColumn)
    val hasIntrinsicSizes = BooleanArray(lineCount) { line -> hasIntrinsicSize(line, isColumn) }
    val growableLines = BooleanArray(lineCount) { line -> isLineGrowable(line, isColumn, weights) }
    val supportsConstrainedResolution = !isWrapContent &&
        hasIntrinsicSizes.none { it } &&
        weights.any { it > 0f } &&
        (0 until lineCount).all { line -> weights[line] > 0f || !growableLines[line] }
    val itemBaseSizes = observeItemBaseSizes(
        isColumn = isColumn,
        includeMatchParentMargins = supportsConstrainedResolution,
    )
    val fallbackBaseSizes = FloatArray(lineCount) { line ->
        lineBaseSize(
            line = line,
            isColumn = isColumn,
            weights = weights,
            itemBaseSizes = itemBaseSizes,
            includeMatchParentMargins = false,
        )
    }
    val resolvedBaseSizes = if (supportsConstrainedResolution) {
        FloatArray(lineCount) { line ->
            lineBaseSize(
                line = line,
                isColumn = isColumn,
                weights = weights,
                itemBaseSizes = itemBaseSizes,
                includeMatchParentMargins = true,
            )
        }
    } else {
        fallbackBaseSizes
    }
    val maxWeightRatio = (0 until lineCount).maxOfOrNull { line ->
        val weight = weights[line]
        if (weight > 0f) fallbackBaseSizes[line] / weight else 0f
    } ?: 0f
    val tracks = List(lineCount) { line ->
        val weight = weights[line]
        val baseSize = fallbackBaseSizes[line]
        when {
            weight > 0f && isWrapContent -> GridTrackSize.Fixed((weight * maxWeightRatio).dp)
            weight > 0f -> GridTrackSize.MinMax((weight * maxWeightRatio).dp, Fr(weight))
            !growableLines[line] && baseSize > 0f -> GridTrackSize.Fixed(baseSize.dp)
            else -> GridTrackSize.Auto
        }
    }
    val canResolveConstrainedWeights = supportsConstrainedResolution &&
        (0 until lineCount).all { line ->
            weights[line] > 0f || resolvedBaseSizes[line] > 0f
        }
    return AxisTracks(
        tracks = tracks,
        weights = weights,
        baseSizes = resolvedBaseSizes,
        isWrapContent = isWrapContent,
        canResolveConstrainedWeights = canResolveConstrainedWeights,
    )
}

@OptIn(ExperimentalGridApi::class, InternalApi::class)
internal class AxisTracks(
    private val tracks: List<GridTrackSize>,
    private val weights: FloatArray,
    private val baseSizes: FloatArray,
    private val isWrapContent: Boolean,
    private val canResolveConstrainedWeights: Boolean,
) {
    fun resolve(availableSize: Int, density: Density): List<GridTrackSize> {
        if (
            isWrapContent ||
            availableSize == Constraints.Infinity ||
            !canResolveConstrainedWeights ||
            weights.none { it > 0f }
        ) {
            return tracks
        }

        val baseSizesPx = IntArray(baseSizes.size) { line ->
            with(density) { baseSizes[line].dp.roundToPx() }
        }
        val resolvedSizes = resolveWeightedSizes(weights, baseSizesPx, availableSize)
        return resolvedSizes.map { size ->
            GridTrackSize.Fixed(with(density) { size.toDp() })
        }
    }
}

@Composable
private fun List<Cell>.observeLineWeights(lineCount: Int, isColumn: Boolean): FloatArray {
    val weights = FloatArray(lineCount)
    forEach { cell ->
        val span = if (isColumn) cell.columnSpan else cell.rowSpan
        val start = if (isColumn) cell.columnIndex else cell.rowIndex
        val size = if (isColumn) cell.base.width else cell.base.height
        if (size is DivSize.MatchParent) {
            val weight = (size.value.weight?.observedFloatValue() ?: 0f) / span
            for (line in start until start + span) {
                weights[line] = maxOf(weights[line], weight)
            }
        }
    }
    return weights
}

@Composable
private fun List<Cell>.observeItemBaseSizes(
    isColumn: Boolean,
    includeMatchParentMargins: Boolean,
): FloatArray = FloatArray(size) { index ->
    val cell = this[index]
    val size = if (isColumn) cell.base.width else cell.base.height
    when (size) {
        is DivSize.Fixed -> {
            val margins = if (isColumn) cell.base.observeHorizontalMarginsSum()
                          else cell.base.observeVerticalMarginsSum()
            (size.value.observedValue() + margins).value
        }
        is DivSize.MatchParent -> {
            if (!includeMatchParentMargins) return@FloatArray 0f
            val margins = if (isColumn) cell.base.observeHorizontalMarginsSum()
                          else cell.base.observeVerticalMarginsSum()
            margins.value
        }
        is DivSize.WrapContent -> 0f
    }
}

private fun List<Cell>.lineBaseSize(
    line: Int,
    isColumn: Boolean,
    weights: FloatArray,
    itemBaseSizes: FloatArray,
    includeMatchParentMargins: Boolean,
    includeMultiSpan: Boolean = true,
): Float {
    var max = 0f
    forEachIndexed { index, cell ->
        val span = if (isColumn) cell.columnSpan else cell.rowSpan
        if (!includeMultiSpan && span > 1) return@forEachIndexed
        val start = if (isColumn) cell.columnIndex else cell.rowIndex
        if (line !in start until start + span) return@forEachIndexed
        val size = if (isColumn) cell.base.width else cell.base.height
        val itemSize = when (size) {
            is DivSize.Fixed -> itemBaseSizes[index]
            is DivSize.MatchParent -> {
                if (!includeMatchParentMargins) return@forEachIndexed
                itemBaseSizes[index]
            }
            is DivSize.WrapContent -> return@forEachIndexed
        }
        max = maxOf(
            max,
            lineShareOfBase(
                line = line,
                start = start,
                span = span,
                isColumn = isColumn,
                itemSize = itemSize,
                weights = weights,
                itemBaseSizes = itemBaseSizes,
                includeMatchParentMargins = includeMatchParentMargins,
            )
        )
    }
    return max
}

private fun List<Cell>.lineShareOfBase(
    line: Int,
    start: Int,
    span: Int,
    isColumn: Boolean,
    itemSize: Float,
    weights: FloatArray,
    itemBaseSizes: FloatArray,
    includeMatchParentMargins: Boolean,
): Float {
    if (span == 1) return itemSize
    val range = start until start + span
    val totalFlexWeight = range.sumOf { weights[it].toDouble() }.toFloat()
    if (totalFlexWeight > 0f) {
        val weight = weights[line]
        if (weight <= 0f) return 0f
        val nonFlexBaseSize = range.sumOf {
            if (weights[it] > 0f) 0.0
            else lineBaseSize(
                line = it,
                isColumn = isColumn,
                weights = weights,
                itemBaseSizes = itemBaseSizes,
                includeMatchParentMargins = includeMatchParentMargins,
                includeMultiSpan = false,
            ).toDouble()
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

private fun List<Cell>.hasIntrinsicSize(line: Int, isColumn: Boolean): Boolean = any { cell ->
    val span = if (isColumn) cell.columnSpan else cell.rowSpan
    val start = if (isColumn) cell.columnIndex else cell.rowIndex
    if (line !in start until start + span) return@any false
    val size = if (isColumn) cell.base.width else cell.base.height
    size is DivSize.WrapContent
}

private fun List<Cell>.isLineGrowable(
    line: Int,
    isColumn: Boolean,
    weights: FloatArray,
): Boolean = any { cell ->
    val span = if (isColumn) cell.columnSpan else cell.rowSpan
    if (span <= 1) return@any false
    val start = if (isColumn) cell.columnIndex else cell.rowIndex
    if (line !in start until start + span) return@any false
    val range = start until start + span
    when {
        range.any { weights[it] > 0f } -> weights[line] > 0f
        range.any { !isLineUsed(it, isColumn) } -> !isLineUsed(line, isColumn)
        else -> true
    }
}
