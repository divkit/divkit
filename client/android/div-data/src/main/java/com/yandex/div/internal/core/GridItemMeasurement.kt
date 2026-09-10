package com.yandex.div.internal.core

import com.yandex.div.core.annotations.InternalApi
import kotlin.math.ceil

@InternalApi
data class GridItemMeasurement(
    val lineIndex: Int,
    val contentSize: Int,
    val size: Int,
    val span: Int,
    val weight: Float,
)

@InternalApi
fun resolveGridTrackSizes(
    count: Int,
    items: List<GridItemMeasurement>,
    minimumSize: Int,
): IntArray {
    val lines = List(count) { Line() }
    items.forEach { item ->
        if (item.span == 1) {
            lines[item.lineIndex].include(item.contentSize, item.size, item.weight)
        } else {
            val weight = item.weight / item.span
            for (index in item.lineIndex until item.lineIndex + item.span) {
                lines[index].include(weight = weight)
            }
        }
    }
    applySpansToLines(items, lines)
    return resolveWeightedSizes(
        weights = FloatArray(count) { lines[it].weight },
        baseSizes = IntArray(count) { lines[it].size },
        minimumSize = minimumSize,
    )
}

private fun applySpansToLines(items: List<GridItemMeasurement>, lines: List<Line>) {
    val spannedItems = items.filter { it.span > 1 }.sortedByDescending { it.size / it.span }
    spannedItems.forEach { item ->
        val range = item.lineIndex until item.lineIndex + item.span
        var undistributedSize = item.size
        var flexibleSize = undistributedSize
        var totalWeight = 0f
        var unusedLineCount = 0
        for (index in range) {
            val line = lines[index]
            undistributedSize -= line.size
            if (line.isFlexible) {
                totalWeight += line.weight
            } else {
                if (line.contentSize == 0) unusedLineCount++
                flexibleSize -= line.size
            }
        }
        if (totalWeight > 0f) {
            for (index in range) {
                val line = lines[index]
                if (line.isFlexible) {
                    val size = ceil(line.weight / totalWeight * flexibleSize).toInt()
                    line.include(contentSize = size - line.marginSize, size = size)
                }
            }
        } else if (undistributedSize > 0) {
            val lineCount = if (unusedLineCount > 0) unusedLineCount else item.span
            val extraSize = undistributedSize / lineCount
            var remainder = undistributedSize % lineCount
            for (index in range) {
                val line = lines[index]
                if (unusedLineCount > 0 && (line.contentSize != 0 || line.isFlexible)) continue
                val addedSize = extraSize + if (remainder > 0) {
                    remainder--
                    1
                } else {
                    0
                }
                line.include(contentSize = line.contentSize + addedSize, size = line.size + addedSize)
            }
        }
    }
}

private class Line {
    var contentSize: Int = 0
        private set

    var size: Int = 0
        private set

    var weight: Float = 0f
        private set

    val marginSize: Int
        get() = size - contentSize

    val isFlexible: Boolean
        get() = weight > 0f

    fun include(contentSize: Int = 0, size: Int = 0, weight: Float = 0f) {
        this.contentSize = maxOf(this.contentSize, contentSize)
        this.size = maxOf(this.size, size)
        this.weight = maxOf(this.weight, weight)
    }
}
