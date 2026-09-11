package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.text.observeBaseTextStyle
import com.yandex.div.compose.text.observedTextDecoration
import com.yandex.div.compose.utils.observeShadow
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import kotlin.math.nextDown

@Immutable
internal data class ObservedTextMetrics(
    val fontSizeUnit: DivSizeUnit,
    val lineHeight: Int?,
)

@Composable
internal fun DivText.observeTextMetrics(): ObservedTextMetrics {
    val fontSizeUnit = fontSizeUnit.observedValue()
    val lineHeight = lineHeight?.observedIntValue()?.takeIf { it > 0 }
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
    val textStyle = if (ranges.isNullOrEmpty() && images.isNullOrEmpty() && ellipsis?.ranges.isNullOrEmpty()) {
        rememberExactLineHeight(baseStyle)
    } else {
        baseStyle
    }
    return textStyle.copy(
        hyphens = hyphens,
        shadow = textShadow?.observeShadow(baseStyle.color.alpha),
        textDecoration = observedTextDecoration(strike, underline)
    )
}

@Composable
private fun rememberExactLineHeight(textStyle: TextStyle): TextStyle {
    val fontMetrics = rememberFontLineMetrics(
        textStyle = textStyle,
        isRequired = textStyle.lineHeight.isSpecified,
    ) ?: return textStyle
    val density = LocalDensity.current
    return remember(textStyle, fontMetrics, density) {
        val lineHeightPx = with(density) { textStyle.lineHeight.toPx() }
        val fontSizePx = with(density) { textStyle.fontSize.toPx() }
        val lineHeight = if (density.fontScale > 1f && fontSizePx > 0) {
            // Compose's nonlinear SP scaling preserves the line-height/font-size ratio.
            // At scale 2 and density 1, 20sp/30sp becomes 34px/51px instead of 34px/38px.
            // EM preserves the independently converted line height.
            val multiplier = lineHeightPx / fontSizePx
            // Prevent rounding from adding a pixel when Compose applies ceil(lineHeight).
            if (multiplier * fontSizePx > lineHeightPx) multiplier.nextDown().em else multiplier.em
        } else {
            textStyle.lineHeight
        }
        textStyle.copy(
            lineHeight = lineHeight,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = if (lineHeightPx < fontMetrics.heightPx) {
                    LineHeightStyle.Trim.Both
                } else {
                    LineHeightStyle.Trim.None
                },
                mode = LineHeightStyle.Mode.Tight,
            ),
        )
    }
}
