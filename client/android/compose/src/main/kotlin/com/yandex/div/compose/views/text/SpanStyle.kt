package com.yandex.div.compose.views.text

import android.graphics.Color.argb
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.text.letterSpacing
import com.yandex.div.compose.text.observedTextDecoration
import com.yandex.div.compose.text.rememberFontFamily
import com.yandex.div.compose.text.toFontWeight
import com.yandex.div.compose.utils.observeShadow
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
): SpanStyle {
    val rangeFontSize = fontSize?.observedIntValue()
    val rangeFontWeight = fontWeight?.observedValue()
    val rangeFontWeightValue = fontWeightValue?.observedIntValue()
    val spanFontSize = rangeFontSize?.toTextUnit(fontSizeUnit)
        ?: TextUnit.Unspecified
    val spanLetterSpacing = letterSpacing?.observedFloatValue()?.let {
        letterSpacing(it, rangeFontSize ?: baseFontSize)
    } ?: TextUnit.Unspecified
    val spanFontFamily = observeFontFamily(text, rangeFontWeight, rangeFontWeightValue)
    val spanBrush = if (isTextMasked) {
        null
    } else {
        textColor?.observedColorValue()?.let { color ->
            remember(color) { SolidColorShaderBrush(color) }
        }
    }
    return SpanStyle(
        brush = spanBrush,
        alpha = 1f,
        fontSize = spanFontSize,
        fontFamily = spanFontFamily,
        fontWeight = rangeFontWeight.toFontWeight(rangeFontWeightValue),
        letterSpacing = spanLetterSpacing,
        textDecoration = if (isTextMasked) null else observedTextDecoration(strike, underline),
        shadow = if (isTextMasked) null else textShadow?.observeShadow(baseTextColorAlpha),
        fontFeatureSettings = fontFeatureSettings?.observedValue()?.takeIf { it.isNotBlank() },
    )
}

@Composable
private fun DivText.Range.observeFontFamily(
    text: DivText,
    rangeFontWeight: DivFontWeight?,
    rangeFontWeightValue: Int?,
): FontFamily? {
    val hasFontWeight = rangeFontWeight != null || rangeFontWeightValue != null
    if (fontFamily == null && !hasFontWeight && fontVariationSettings == null) {
        return null
    }
    return rememberFontFamily(
        fontFamily = (fontFamily ?: text.fontFamily)?.observedValue(),
        fontWeight = if (hasFontWeight) rangeFontWeight else text.fontWeight?.observedValue(),
        fontWeightValue = if (hasFontWeight) rangeFontWeightValue else text.fontWeightValue?.observedIntValue(),
        fontVariationSettings = fontVariationSettings?.observedValue(),
    )
}

@Composable
internal fun rememberMaskedSpanStyle(): SpanStyle {
    val brush = remember { SolidColorShaderBrush(Color.Transparent) }
    return remember(brush) {
        SpanStyle(
            brush = brush,
            textDecoration = androidx.compose.ui.text.style.TextDecoration.None,
            shadow = androidx.compose.ui.graphics.Shadow.None,
        )
    }
}

private class SolidColorShaderBrush(color: Color) : ShaderBrush() {
    private val argb = argb(
        (color.alpha * 255).toInt(),
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt(),
    )

    override fun createShader(size: Size): Shader {
        return LinearGradient(0f, 0f, 0f, size.height, argb, argb, Shader.TileMode.CLAMP)
    }
}
