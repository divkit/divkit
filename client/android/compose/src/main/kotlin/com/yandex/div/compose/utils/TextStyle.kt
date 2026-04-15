package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.yandex.div.compose.views.text.fontFamily
import com.yandex.div.compose.views.text.letterSpacing
import com.yandex.div.compose.views.text.toFontWeight
import com.yandex.div.compose.views.text.toTextUnit
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSizeUnit

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
): TextStyle {
    val color = textColor.observedColorValue()
    val sizeUnit = fontSizeUnit.observedValue()
    val weight = fontWeight?.observedValue()
    val weightValue = fontWeightValue?.observedIntValue()
    val density = LocalDensity.current

    val textSize = fontSize.toTextUnit(sizeUnit, density)
    val resolvedLineHeight = lineHeight?.observedIntValue()?.toTextUnit(sizeUnit, density)
    val resolvedFontWeight = weight.toFontWeight(weightValue)
    val resolvedLetterSpacing = letterSpacing(letterSpacing.observedFloatValue(), fontSize)
    val resolvedFontFamily = fontFamily(fontFamily?.observedValue(), weight, weightValue)
    val textAlign = textAlignmentHorizontal.toTextAlign()
    val lineHeightStyle = lineHeightStyle(resolvedLineHeight, textSize)

    return TextStyle(
        color = color,
        fontSize = textSize,
        fontFamily = resolvedFontFamily,
        fontWeight = resolvedFontWeight,
        letterSpacing = resolvedLetterSpacing,
        textAlign = textAlign,
        lineHeight = resolvedLineHeight ?: TextUnit.Unspecified,
        lineHeightStyle = lineHeightStyle,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
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
