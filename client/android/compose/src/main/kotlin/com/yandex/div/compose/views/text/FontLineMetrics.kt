package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit

@Immutable
internal data class FontLineMetrics(
    val ascent: TextUnit,
    val descent: TextUnit,
    val heightPx: Float,
)

@Composable
internal fun rememberFontLineMetrics(
    textStyle: TextStyle,
    isRequired: Boolean,
): FontLineMetrics? {
    if (!isRequired) {
        return null
    }

    val textMeasurer = rememberTextMeasurer()
    val measuredStyle = remember(textStyle) {
        textStyle.copy(
            lineHeight = TextUnit.Unspecified,
            lineHeightStyle = null,
        )
    }
    val layout = remember(textMeasurer, measuredStyle) {
        textMeasurer.measure(
            text = "M",
            style = measuredStyle,
            maxLines = 1,
        )
    }
    return with(LocalDensity.current) {
        val baseline = layout.firstBaseline
        FontLineMetrics(
            ascent = (baseline - layout.getLineTop(0)).toSp(),
            descent = (layout.getLineBottom(0) - baseline).toSp(),
            heightPx = layout.getLineBottom(0) - layout.getLineTop(0),
        )
    }
}
