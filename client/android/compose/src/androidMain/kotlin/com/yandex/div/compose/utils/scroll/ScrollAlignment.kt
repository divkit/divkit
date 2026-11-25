package com.yandex.div.compose.utils.scroll

import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical

internal fun DivAlignmentHorizontal.toCrossAxisAlignment(layoutDirection: LayoutDirection): CrossAxisAlignment =
    when (this) {
        DivAlignmentHorizontal.LEFT ->
            if (layoutDirection == LayoutDirection.Ltr) CrossAxisAlignment.START else CrossAxisAlignment.END
        DivAlignmentHorizontal.START -> CrossAxisAlignment.START
        DivAlignmentHorizontal.CENTER -> CrossAxisAlignment.CENTER
        DivAlignmentHorizontal.RIGHT ->
            if (layoutDirection == LayoutDirection.Ltr) CrossAxisAlignment.END else CrossAxisAlignment.START
        DivAlignmentHorizontal.END -> CrossAxisAlignment.END
    }

internal fun DivAlignmentVertical.toCrossAxisAlignment(): CrossAxisAlignment =
    when (this) {
        DivAlignmentVertical.TOP,
        DivAlignmentVertical.BASELINE -> CrossAxisAlignment.START
        DivAlignmentVertical.CENTER -> CrossAxisAlignment.CENTER
        DivAlignmentVertical.BOTTOM -> CrossAxisAlignment.END
    }
