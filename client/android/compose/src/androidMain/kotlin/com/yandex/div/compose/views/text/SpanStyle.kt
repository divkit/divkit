package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.text.FontParams
import com.yandex.div.compose.text.letterSpacing
import com.yandex.div.compose.text.observedTextDecoration
import com.yandex.div.compose.text.rememberFontFamily
import com.yandex.div.compose.text.toFontWeight
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div.compose.utils.toColor
import com.yandex.div.compose.utils.toTextUnit
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText

@Composable
internal fun DivText.Range.observeSpanStyle(
    text: DivText,
    baseFontSize: Int,
    baseTextColorAlpha: Float,
    fontSizeUnit: DivSizeUnit,
    isTextMasked: Boolean,
    component: DivLocalComponent,
): SpanStyle {
    val rangeFontSize = fontSize?.observedValue(component)?.toInt()
    val rangeFontWeight = fontWeight?.observedValue(component)
    val rangeFontWeightValue = fontWeightValue?.observedValue(component)?.toInt()
    val spanFontSize = rangeFontSize?.toTextUnit(fontSizeUnit)
        ?: TextUnit.Unspecified
    val spanLetterSpacing = letterSpacing?.observedValue(component)?.toFloat()?.let {
        letterSpacing(it, rangeFontSize ?: baseFontSize)
    } ?: TextUnit.Unspecified
    val spanFontFamily = observeFontFamily(text, rangeFontWeight, rangeFontWeightValue, component)
    val spanBrush = if (isTextMasked) {
        null
    } else {
        textColor?.observedValue(component)?.toColor()?.let { color ->
            remember(color) { Brush.verticalGradient(listOf(color, color)) }
        }
    }
    return SpanStyle(
        brush = spanBrush,
        alpha = 1f,
        fontSize = spanFontSize,
        fontFamily = spanFontFamily,
        fontWeight = rangeFontWeight.toFontWeight(rangeFontWeightValue),
        letterSpacing = spanLetterSpacing,
        textDecoration = if (isTextMasked) {
            null
        } else {
            observedTextDecoration(strike, underline, component)
        },
        shadow = if (isTextMasked) {
            null
        } else {
            textShadow?.observeShadow(baseTextColorAlpha, component)
        },
        fontFeatureSettings = fontFeatureSettings?.observedValue(component)
            ?.takeIf { it.isNotBlank() },
    )
}

@Composable
private fun DivText.Range.observeFontFamily(
    text: DivText,
    rangeFontWeight: DivFontWeight?,
    rangeFontWeightValue: Int?,
    component: DivLocalComponent,
): FontFamily? {
    val hasFontWeight = rangeFontWeight != null || rangeFontWeightValue != null
    if (fontFamily == null && !hasFontWeight && fontVariationSettings == null) {
        return null
    }
    val fontParams = FontParams(
        family = fontFamily?.observedValue(component) ?: text.fontFamily?.observedValue(),
        weight = if (hasFontWeight) rangeFontWeight else text.fontWeight?.observedValue(),
        weightValue = if (hasFontWeight) {
            rangeFontWeightValue
        } else {
            text.fontWeightValue?.observedIntValue()
        },
        variationSettings = fontVariationSettings?.observedValue(component),
    )
    return rememberFontFamily(fontParams)
}

@Composable
internal fun rememberMaskedSpanStyle(): SpanStyle {
    return remember {
        SpanStyle(
            brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)),
            textDecoration = TextDecoration.None,
            shadow = Shadow.None,
        )
    }
}
