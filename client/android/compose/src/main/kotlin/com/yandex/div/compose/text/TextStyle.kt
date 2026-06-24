package com.yandex.div.compose.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.toTextUnit
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivSizeUnit
import org.json.JSONObject

@Composable
internal fun observeBaseTextStyle(
    fontSize: Int,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    fontSizeUnit: Expression<DivSizeUnit>,
    textColor: Expression<Int>,
    fontWeight: Expression<DivFontWeight>?,
    fontWeightValue: Expression<Long>?,
    fontFamily: Expression<String>?,
    letterSpacing: Expression<Double>,
    lineHeight: Expression<Long>?,
    fontFeatureSettings: Expression<String>? = null,
    fontVariationSettings: Expression<JSONObject>? = null,
): TextStyle {
    val sizeUnit = fontSizeUnit.observedValue()
    val weight = fontWeight?.observedValue()
    val weightValue = fontWeightValue?.observedIntValue()
    val textSize = fontSize.toTextUnit(sizeUnit)
    val resolvedLineHeight = lineHeight?.observedIntValue()?.toTextUnit(sizeUnit)
    val resolvedLetterSpacing = letterSpacing(letterSpacing.observedFloatValue(), fontSize)
    val resolvedFontFamily = rememberFontFamily(
        fontFamily = fontFamily?.observedValue(),
        fontWeight = weight,
        fontWeightValue = weightValue,
        fontVariationSettings = fontVariationSettings?.observedValue(),
    )
    return TextStyle(
        color = textColor.observedColorValue(),
        fontSize = textSize,
        fontFamily = resolvedFontFamily,
        fontWeight = weight.toFontWeight(weightValue),
        letterSpacing = resolvedLetterSpacing,
        textAlign = textAlignmentHorizontal.toTextAlign(),
        lineHeight = resolvedLineHeight ?: TextUnit.Unspecified,
        lineHeightStyle = lineHeightStyle(resolvedLineHeight, textSize),
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        fontFeatureSettings = fontFeatureSettings?.observedValue()?.takeIf { it.isNotBlank() },
    )
}

private fun lineHeightStyle(lineHeight: TextUnit?, fontSize: TextUnit): LineHeightStyle? {
    if (lineHeight == null) return null

    val trim = if (lineHeight < fontSize) {
        LineHeightStyle.Trim.Both
    } else {
        LineHeightStyle.Trim.None
    }

    return LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = trim,
        mode = LineHeightStyle.Mode.Tight,
    )
}

private fun DivAlignmentHorizontal.toTextAlign(): TextAlign {
    return when (this) {
        DivAlignmentHorizontal.LEFT, DivAlignmentHorizontal.START -> TextAlign.Start
        DivAlignmentHorizontal.CENTER -> TextAlign.Center
        DivAlignmentHorizontal.RIGHT, DivAlignmentHorizontal.END -> TextAlign.End
    }
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

internal fun letterSpacing(letterSpacing: Float, fontSize: Int): TextUnit {
    return if (letterSpacing != 0f) {
        (letterSpacing / fontSize).em
    } else {
        TextUnit.Unspecified
    }
}

@Composable
internal fun observedTextDecoration(
    strike: Expression<DivLineStyle>?,
    underline: Expression<DivLineStyle>?,
): TextDecoration? {
    val hasStrike = strike?.observedValue() == DivLineStyle.SINGLE
    val hasUnderline = underline?.observedValue() == DivLineStyle.SINGLE
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
