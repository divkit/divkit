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
): Alignment = combineAlignment(
    horizontal.toHorizontalAlignment(),
    vertical.toVerticalAlignment(),
)

internal fun combineAlignment(
    horizontal: Alignment.Horizontal,
    vertical: Alignment.Vertical,
): Alignment = when (horizontal) {
    Alignment.Start -> when (vertical) {
        Alignment.Top -> Alignment.TopStart
        Alignment.CenterVertically -> Alignment.CenterStart
        else -> Alignment.BottomStart
    }
    Alignment.CenterHorizontally -> when (vertical) {
        Alignment.Top -> Alignment.TopCenter
        Alignment.CenterVertically -> Alignment.Center
        else -> Alignment.BottomCenter
    }
    else -> when (vertical) {
        Alignment.Top -> Alignment.TopEnd
        Alignment.CenterVertically -> Alignment.CenterEnd
        else -> Alignment.BottomEnd
    }
}
