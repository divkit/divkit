package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.Hyphens
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.text.observeBaseTextStyle
import com.yandex.div.compose.text.observedTextDecoration
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText

@Immutable
internal data class ObservedTextMetrics(
    val fontSizeUnit: DivSizeUnit,
    val lineHeight: Int?,
)

@Composable
internal fun DivText.observeTextMetrics(): ObservedTextMetrics {
    val fontSizeUnit = fontSizeUnit.observedValue()
    val lineHeight = lineHeight?.observedIntValue()
    return remember(fontSizeUnit, lineHeight) {
        ObservedTextMetrics(fontSizeUnit, lineHeight)
    }
}

@Composable
internal fun DivText.observeTextStyle(
    fontSize: Int,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    hyphens: Hyphens,
    metrics: ObservedTextMetrics,
): TextStyle {
    val baseStyle = observeBaseTextStyle(
        fontSize = fontSize,
        textAlignmentHorizontal = textAlignmentHorizontal,
        fontSizeUnit = metrics.fontSizeUnit,
        textColor = textColor,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        lineHeight = metrics.lineHeight,
        fontFeatureSettings = fontFeatureSettings,
        fontVariationSettings = fontVariationSettings,
    )
    return baseStyle.copy(
        hyphens = hyphens,
        shadow = textShadow?.observeShadow(baseStyle.color.alpha),
        textDecoration = observedTextDecoration(strike, underline)
    )
}
