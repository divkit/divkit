package com.yandex.div.compose.views.text

import android.graphics.Color.argb
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div2.DivText

@Composable
internal fun DivText.Range.observeSpanStyle(
    baseFontSize: Int,
    baseTextColorAlpha: Float,
    density: Density,
): SpanStyle {
    val rangeColor = textColor?.observedColorValue()
    val rangeFontSize = fontSize?.observedIntValue()
    val rangeFontWeight = fontWeight?.observedValue()
    val rangeFontWeightValue = fontWeightValue?.observedIntValue()
    val rangeLetterSpacing = letterSpacing?.observedFloatValue()
    val rangeStrike = strike?.observedValue()
    val rangeUnderline = underline?.observedValue()
    val rangeFontFamily = fontFamily?.observedValue()

    val spanFontWeight = rangeFontWeight.toFontWeight(rangeFontWeightValue)
    val spanTextDecoration = textDecoration(rangeStrike, rangeUnderline)
    val spanFontSize = rangeFontSize?.toTextUnit(fontSizeUnit.observedValue(), density)
        ?: TextUnit.Unspecified
    val spanLetterSpacing = rangeLetterSpacing?.let {
        letterSpacing(it, rangeFontSize ?: baseFontSize)
    } ?: TextUnit.Unspecified
    val spanFontFamily = fontFamily(rangeFontFamily, rangeFontWeight, rangeFontWeightValue)
    val spanShadow = textShadow?.observeShadow(baseTextColorAlpha)

    val spanBrush = rangeColor?.let { SolidColorShaderBrush(it) }

    return SpanStyle(
        brush = spanBrush,
        fontSize = spanFontSize,
        fontFamily = spanFontFamily,
        fontWeight = spanFontWeight,
        letterSpacing = spanLetterSpacing,
        textDecoration = spanTextDecoration,
        shadow = spanShadow,
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
