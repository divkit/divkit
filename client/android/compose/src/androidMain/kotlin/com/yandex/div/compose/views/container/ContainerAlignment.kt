package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
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
        else -> alignedArrangement(itemSpacing, toCrossAxisHorizontalAlignment())
    }

internal fun DivContentAlignmentVertical.toVerticalArrangement(
    itemSpacing: Long,
): Arrangement.Vertical = toVerticalArrangement(itemSpacing.toDp())

internal fun DivContentAlignmentVertical.toVerticalArrangement(itemSpacing: Dp): Arrangement.Vertical =
    when (this) {
        DivContentAlignmentVertical.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentVertical.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentVertical.SPACE_EVENLY -> Arrangement.SpaceEvenly
        else -> alignedArrangement(itemSpacing, toCrossAxisVerticalAlignment())
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
