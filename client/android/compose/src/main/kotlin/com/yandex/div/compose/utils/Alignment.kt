package com.yandex.div.compose.utils

import androidx.compose.ui.Alignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical

internal fun DivAlignmentHorizontal.toHorizontalAlignment(): Alignment.Horizontal =
    when (this) {
        DivAlignmentHorizontal.CENTER -> Alignment.CenterHorizontally
        DivAlignmentHorizontal.RIGHT, DivAlignmentHorizontal.END -> Alignment.End
        else -> Alignment.Start
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
): Alignment {
    return when (horizontal) {
        DivAlignmentHorizontal.LEFT,
        DivAlignmentHorizontal.START -> when (vertical) {
            DivAlignmentVertical.TOP -> Alignment.TopStart
            DivAlignmentVertical.CENTER,
            DivAlignmentVertical.BASELINE -> Alignment.CenterStart
            DivAlignmentVertical.BOTTOM -> Alignment.BottomStart
        }
        DivAlignmentHorizontal.CENTER -> when (vertical) {
            DivAlignmentVertical.TOP -> Alignment.TopCenter
            DivAlignmentVertical.CENTER,
            DivAlignmentVertical.BASELINE -> Alignment.Center
            DivAlignmentVertical.BOTTOM -> Alignment.BottomCenter
        }
        DivAlignmentHorizontal.RIGHT,
        DivAlignmentHorizontal.END -> when (vertical) {
            DivAlignmentVertical.TOP -> Alignment.TopEnd
            DivAlignmentVertical.CENTER,
            DivAlignmentVertical.BASELINE -> Alignment.CenterEnd
            DivAlignmentVertical.BOTTOM -> Alignment.BottomEnd
        }
    }
}
