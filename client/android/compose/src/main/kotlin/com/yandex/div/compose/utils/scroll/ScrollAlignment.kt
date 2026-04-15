package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical

internal enum class CrossAxisAlignment {
    START, CENTER, END;

    fun toVerticalAlignment(): Alignment.Vertical = when (this) {
        START -> Alignment.Top
        CENTER -> Alignment.CenterVertically
        END -> Alignment.Bottom
    }

    fun toHorizontalAlignment(): Alignment.Horizontal = when (this) {
        START -> Alignment.Start
        CENTER -> Alignment.CenterHorizontally
        END -> Alignment.End
    }

    fun toBoxAlignment(isHorizontal: Boolean): Alignment =
        if (isHorizontal) {
            when (this) {
                START -> Alignment.TopStart
                CENTER -> Alignment.CenterStart
                END -> Alignment.BottomStart
            }
        } else {
            when (this) {
                START -> Alignment.TopStart
                CENTER -> Alignment.TopCenter
                END -> Alignment.TopEnd
            }
        }
}

internal fun DivAlignmentHorizontal.toCrossAxisAlignment(): CrossAxisAlignment =
    when (this) {
        DivAlignmentHorizontal.LEFT,
        DivAlignmentHorizontal.START -> CrossAxisAlignment.START
        DivAlignmentHorizontal.CENTER -> CrossAxisAlignment.CENTER
        DivAlignmentHorizontal.RIGHT,
        DivAlignmentHorizontal.END -> CrossAxisAlignment.END
    }

internal fun DivAlignmentVertical.toCrossAxisAlignment(): CrossAxisAlignment =
    when (this) {
        DivAlignmentVertical.TOP,
        DivAlignmentVertical.BASELINE -> CrossAxisAlignment.START
        DivAlignmentVertical.CENTER -> CrossAxisAlignment.CENTER
        DivAlignmentVertical.BOTTOM -> CrossAxisAlignment.END
    }

internal fun PaddingValues.getScrollAxisPaddings(
    isHorizontal: Boolean,
    layoutDirection: LayoutDirection,
): Pair<Dp, Dp> {
    return if (isHorizontal) {
        Pair(calculateStartPadding(layoutDirection), calculateEndPadding(layoutDirection))
    } else {
        Pair(calculateTopPadding(), calculateBottomPadding())
    }
}
