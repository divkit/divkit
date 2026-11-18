package com.yandex.div.compose.text

import androidx.compose.ui.text.font.FontWeight
import com.yandex.div2.DivFontWeight

private const val MAX_FONT_WEIGHT = 1000

internal fun DivFontWeight?.toFontWeight(weightValue: Int?): FontWeight? {
    if (weightValue != null) {
        return FontWeight(weightValue.coerceIn(1, MAX_FONT_WEIGHT))
    }
    return when (this) {
        DivFontWeight.LIGHT -> FontWeight.Light
        DivFontWeight.REGULAR -> FontWeight.Normal
        DivFontWeight.MEDIUM -> FontWeight.Medium
        DivFontWeight.BOLD -> FontWeight.Bold
        null -> null
    }
}
