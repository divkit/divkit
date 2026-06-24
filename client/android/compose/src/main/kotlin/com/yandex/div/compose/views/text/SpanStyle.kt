package com.yandex.div.compose.views.text

import android.graphics.Color.argb
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div.compose.text.letterSpacing
import com.yandex.div.compose.text.observedTextDecoration
import com.yandex.div.compose.text.rememberFontFamily
import com.yandex.div.compose.text.toFontWeight
import com.yandex.div.compose.utils.toTextUnit
import com.yandex.div2.DivText

@Composable
internal fun DivText.Range.observeSpanStyle(
    baseFontSize: Int,
    baseTextColorAlpha: Float
): SpanStyle {
    val rangeFontSize = fontSize?.observedIntValue()
    val rangeFontWeight = fontWeight?.observedValue()
    val rangeFontWeightValue = fontWeightValue?.observedIntValue()
    val spanFontSize = rangeFontSize?.toTextUnit(fontSizeUnit.observedValue())
        ?: TextUnit.Unspecified
    val spanLetterSpacing = letterSpacing?.observedFloatValue()?.let {
        letterSpacing(it, rangeFontSize ?: baseFontSize)
    } ?: TextUnit.Unspecified
    val spanFontFamily = rememberFontFamily(
        fontFamily = fontFamily?.observedValue(),
        fontWeight = rangeFontWeight,
        fontWeightValue = rangeFontWeightValue,
        fontVariationSettings = fontVariationSettings?.observedValue(),
    )
    return SpanStyle(
        brush = textColor?.observedColorValue()?.let { SolidColorShaderBrush(it) },
        fontSize = spanFontSize,
        fontFamily = spanFontFamily,
        fontWeight = rangeFontWeight.toFontWeight(rangeFontWeightValue),
        letterSpacing = spanLetterSpacing,
        textDecoration = observedTextDecoration(strike, underline),
        shadow = textShadow?.observeShadow(baseTextColorAlpha),
        fontFeatureSettings = fontFeatureSettings?.observedValue()?.takeIf { it.isNotBlank() },
    )
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
