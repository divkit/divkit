package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

internal fun DivContentAlignmentHorizontal.toHorizontalArrangement(
    itemSpacing: Long,
): Arrangement.Horizontal = toHorizontalArrangement(itemSpacing.toDp())

internal fun DivContentAlignmentHorizontal.toHorizontalArrangement(itemSpacing: Dp): Arrangement.Horizontal =
    when (this) {
        DivContentAlignmentHorizontal.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentHorizontal.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentHorizontal.SPACE_EVENLY -> Arrangement.SpaceEvenly
        else -> AlignedHorizontalArrangement(itemSpacing, toCrossAxisHorizontalAlignment())
    }

internal fun DivContentAlignmentVertical.toVerticalArrangement(
    itemSpacing: Long,
): Arrangement.Vertical = toVerticalArrangement(itemSpacing.toDp())

internal fun DivContentAlignmentVertical.toVerticalArrangement(itemSpacing: Dp): Arrangement.Vertical =
    when (this) {
        DivContentAlignmentVertical.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentVertical.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentVertical.SPACE_EVENLY -> Arrangement.SpaceEvenly
        else -> AlignedVerticalArrangement(itemSpacing, toCrossAxisVerticalAlignment())
    }

/** The alignment of children across a container; distributing free space applies along it only. */
internal fun DivContentAlignmentVertical.toCrossAxisVerticalAlignment(): Alignment.Vertical =
    when (this) {
        DivContentAlignmentVertical.CENTER -> Alignment.CenterVertically
        DivContentAlignmentVertical.BOTTOM -> Alignment.Bottom
        DivContentAlignmentVertical.TOP,
        DivContentAlignmentVertical.BASELINE,
        DivContentAlignmentVertical.SPACE_BETWEEN,
        DivContentAlignmentVertical.SPACE_AROUND,
        DivContentAlignmentVertical.SPACE_EVENLY -> Alignment.Top
    }

internal fun DivContentAlignmentHorizontal.toCrossAxisHorizontalAlignment(): Alignment.Horizontal =
    when (this) {
        DivContentAlignmentHorizontal.LEFT -> AbsoluteAlignment.Left
        DivContentAlignmentHorizontal.CENTER -> Alignment.CenterHorizontally
        DivContentAlignmentHorizontal.RIGHT -> AbsoluteAlignment.Right
        DivContentAlignmentHorizontal.END -> Alignment.End
        DivContentAlignmentHorizontal.START,
        DivContentAlignmentHorizontal.SPACE_BETWEEN,
        DivContentAlignmentHorizontal.SPACE_AROUND,
        DivContentAlignmentHorizontal.SPACE_EVENLY -> Alignment.Start
    }

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
