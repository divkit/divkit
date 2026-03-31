package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toColor
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivText

@Composable
internal fun DivText.observeTextStyle(
    fontSize: Long,
    textAlignmentHorizontal: DivAlignmentHorizontal
): TextStyle {
    val textColor = textColor.observedValue().toColor()
    val fontSizeUnit = fontSizeUnit.observedValue()
    val fontWeight = fontWeight?.observedValue()
    val fontWeightValue = fontWeightValue?.observedValue()
    val textAlignmentHorizontal = textAlignmentHorizontal.toTextAlign()
    val density = LocalDensity.current

    val shadow = textShadow?.observeShadow(textColor.alpha)
    val lineHeight = lineHeight?.observedValue()?.toFloat()?.toTextUnit(fontSizeUnit, density)
    val textSize = fontSize.toFloat().toTextUnit(fontSizeUnit, density)
    val composeFontWeight = fontWeight.toFontWeight(fontWeightValue)
    val letterSpacing = letterSpacing(letterSpacing.observedValue(), fontSize)
    val textDecoration = textDecoration(
        strike.observedValue(),
        underline.observedValue()
    )
    val fontFamily = fontFamily(
        fontFamily?.observedValue(),
        fontWeight,
        fontWeightValue
    )

    val lineHeightStyle = lineHeightStyle(lineHeight, textSize)

    return TextStyle(
        color = textColor,
        fontSize = textSize,
        fontFamily = fontFamily,
        fontWeight = composeFontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlignmentHorizontal,
        lineHeight = lineHeight ?: TextUnit.Unspecified,
        lineHeightStyle = lineHeightStyle,
        shadow = shadow,
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