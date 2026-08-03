package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

private val leftArrangement = Arrangement.spacedBy(0.dp, AbsoluteAlignment.Left)
private val rightArrangement = Arrangement.spacedBy(0.dp, AbsoluteAlignment.Right)

internal fun DivContentAlignmentHorizontal.toHorizontalArrangement(
    itemSpacing: Long,
): Arrangement.Horizontal = toHorizontalArrangement(itemSpacing.toDp())

internal fun DivContentAlignmentHorizontal.toHorizontalArrangement(
    itemSpacing: Dp,
): Arrangement.Horizontal {
    return when (this) {
        DivContentAlignmentHorizontal.LEFT ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, AbsoluteAlignment.Left)
            else leftArrangement
        DivContentAlignmentHorizontal.CENTER ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, Alignment.CenterHorizontally)
            else Arrangement.Center
        DivContentAlignmentHorizontal.RIGHT ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, AbsoluteAlignment.Right)
            else rightArrangement
        DivContentAlignmentHorizontal.START ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, Alignment.Start)
            else Arrangement.Start
        DivContentAlignmentHorizontal.END ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, Alignment.End)
            else Arrangement.End
        DivContentAlignmentHorizontal.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentHorizontal.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentHorizontal.SPACE_EVENLY -> Arrangement.SpaceEvenly
    }
}

internal fun DivContentAlignmentVertical.toVerticalArrangement(
    itemSpacing: Long,
): Arrangement.Vertical = toVerticalArrangement(itemSpacing.toDp())

internal fun DivContentAlignmentVertical.toVerticalArrangement(
    itemSpacing: Dp,
): Arrangement.Vertical {
    return when (this) {
        DivContentAlignmentVertical.CENTER ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, Alignment.CenterVertically)
            else Arrangement.Center
        DivContentAlignmentVertical.BOTTOM ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, Alignment.Bottom)
            else Arrangement.Bottom
        DivContentAlignmentVertical.TOP,
        DivContentAlignmentVertical.BASELINE ->
            if (itemSpacing > 0.dp) Arrangement.spacedBy(itemSpacing, Alignment.Top)
            else Arrangement.Top
        DivContentAlignmentVertical.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentVertical.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentVertical.SPACE_EVENLY -> Arrangement.SpaceEvenly
    }
}

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
