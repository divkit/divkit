package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

internal fun alignedArrangement(spacing: Dp, alignment: Alignment.Horizontal): Arrangement.Horizontal =
    AlignedHorizontalArrangement(spacing, alignment)

internal fun alignedArrangement(spacing: Dp, alignment: Alignment.Vertical): Arrangement.Vertical =
    AlignedVerticalArrangement(spacing, alignment)

/**
 * Places children one after another with [spacing] between them and aligns them as a group. A group
 * larger than the space keeps its alignment and overflows; `Arrangement.spacedBy` piles the
 * overflowing children up at the end of the space instead.
 */
private data class AlignedHorizontalArrangement(
    override val spacing: Dp,
    private val alignment: Alignment.Horizontal,
) : Arrangement.Horizontal {
    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        layoutDirection: LayoutDirection,
        outPositions: IntArray,
    ) {
        val groupSize = groupSize(sizes, spacing.roundToPx())
        placeSequentially(
            start = alignment.align(groupSize, totalSize, layoutDirection),
            sizes = sizes,
            spacing = spacing.roundToPx(),
            reversed = layoutDirection == LayoutDirection.Rtl,
            outPositions = outPositions,
        )
    }
}

/** The vertical counterpart of [AlignedHorizontalArrangement]. */
private data class AlignedVerticalArrangement(
    override val spacing: Dp,
    private val alignment: Alignment.Vertical,
) : Arrangement.Vertical {
    override fun Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
        val groupSize = groupSize(sizes, spacing.roundToPx())
        placeSequentially(
            start = alignment.align(groupSize, totalSize),
            sizes = sizes,
            spacing = spacing.roundToPx(),
            reversed = false,
            outPositions = outPositions,
        )
    }
}

private fun groupSize(sizes: IntArray, spacing: Int): Int =
    sizes.sum() + spacing * (sizes.size - 1).coerceAtLeast(0)

private fun placeSequentially(start: Int, sizes: IntArray, spacing: Int, reversed: Boolean, outPositions: IntArray) {
    var position = start
    for (step in sizes.indices) {
        val index = if (reversed) sizes.lastIndex - step else step
        outPositions[index] = position
        position += sizes[index] + spacing
    }
}
