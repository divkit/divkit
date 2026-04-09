package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yandex.div.compose.utils.fontFamilyProvider
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivSizeUnit

@Composable
internal fun fontFamily(
    fontFamily: String?,
    fontWeight: DivFontWeight?,
    fontWeightValue: Int?,
): FontFamily {
    val weight = fontWeight.toFontWeight(fontWeightValue) ?: FontWeight.Normal
    return fontFamilyProvider.getFontFamily(fontFamily, weight)
}

internal fun DivFontWeight?.toFontWeight(
    fontWeightValue: Int?,
): FontWeight? {
    if (fontWeightValue != null) {
        return FontWeight(fontWeightValue.coerceIn(1, 1000))
    }
    return when (this) {
        DivFontWeight.LIGHT -> FontWeight.Light
        DivFontWeight.REGULAR -> FontWeight.Normal
        DivFontWeight.MEDIUM -> FontWeight.Medium
        DivFontWeight.BOLD -> FontWeight.Bold
        null -> null
    }
}

internal fun Int.toTextUnit(unit: DivSizeUnit, density: Density): TextUnit {
    return when (unit) {
        DivSizeUnit.SP -> this.sp
        DivSizeUnit.DP -> this.sp // DP and SP are the same at default font scale
        DivSizeUnit.PX -> with(density) { this@toTextUnit.toDp().toSp() }
    }
}

internal fun letterSpacing(letterSpacing: Float, fontSize: Int): TextUnit {
    return if (letterSpacing != 0f) {
        (letterSpacing / fontSize).em
    } else {
        TextUnit.Unspecified
    }
}

internal fun textDecoration(
    strike: DivLineStyle?,
    underline: DivLineStyle?,
): TextDecoration? {
    val hasStrike = strike == DivLineStyle.SINGLE
    val hasUnderline = underline == DivLineStyle.SINGLE
    return when {
        hasStrike && hasUnderline -> TextDecoration.combine(
            listOf(TextDecoration.LineThrough, TextDecoration.Underline)
        )
        hasStrike -> TextDecoration.LineThrough
        hasUnderline -> TextDecoration.Underline
        strike != null || underline != null -> TextDecoration.None
        else -> null
    }
}
