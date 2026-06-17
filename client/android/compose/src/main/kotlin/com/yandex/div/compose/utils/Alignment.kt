package com.yandex.div.compose.utils

import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical

internal fun DivAlignmentHorizontal.toHorizontalAlignment(): Alignment.Horizontal =
    when (this) {
        DivAlignmentHorizontal.LEFT -> AbsoluteAlignment.Left
        DivAlignmentHorizontal.RIGHT -> AbsoluteAlignment.Right
        DivAlignmentHorizontal.CENTER -> Alignment.CenterHorizontally
        DivAlignmentHorizontal.END -> Alignment.End
        DivAlignmentHorizontal.START -> Alignment.Start
    }

internal fun DivAlignmentVertical.toVerticalAlignment(): Alignment.Vertical =
    when (this) {
        DivAlignmentVertical.CENTER -> Alignment.CenterVertically
        DivAlignmentVertical.BOTTOM -> Alignment.Bottom
        else -> Alignment.Top
    }

internal fun toAlignment(
    horizontal: DivAlignmentHorizontal,
    vertical: DivAlignmentVertical
): Alignment = combineAlignment(
    horizontal.toHorizontalAlignment(),
    vertical.toVerticalAlignment(),
)

internal fun combineAlignment(
    horizontal: Alignment.Horizontal,
    vertical: Alignment.Vertical,
): Alignment = when (horizontal) {
    AbsoluteAlignment.Left -> when (vertical) {
        Alignment.Top -> AbsoluteAlignment.TopLeft
        Alignment.CenterVertically -> AbsoluteAlignment.CenterLeft
        else -> AbsoluteAlignment.BottomLeft
    }
    AbsoluteAlignment.Right -> when (vertical) {
        Alignment.Top -> AbsoluteAlignment.TopRight
        Alignment.CenterVertically -> AbsoluteAlignment.CenterRight
        else -> AbsoluteAlignment.BottomRight
    }
    Alignment.CenterHorizontally -> when (vertical) {
        Alignment.Top -> Alignment.TopCenter
        Alignment.CenterVertically -> Alignment.Center
        else -> Alignment.BottomCenter
    }
    Alignment.End -> when (vertical) {
        Alignment.Top -> Alignment.TopEnd
        Alignment.CenterVertically -> Alignment.CenterEnd
        else -> Alignment.BottomEnd
    }
    else -> when (vertical) {
        Alignment.Top -> Alignment.TopStart
        Alignment.CenterVertically -> Alignment.CenterStart
        else -> Alignment.BottomStart
    }
}
