package com.yandex.div.compose.views.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.font.LineMetrics
import kotlin.math.roundToInt

@Composable
internal fun getLineMetrics(
    textStyle: TextStyle,
    isRequired: Boolean,
): LineMetrics? {
    if (!isRequired) {
        return null
    }

    return divContext.component.lineMetricsCache.getOrCreate(
        fontFamilyResolver = LocalFontFamilyResolver.current,
        density = LocalDensity.current,
        style = textStyle,
    )
}

internal class TextLines(val text: AnnotatedText, val inlineContent: Map<String, InlineTextContent>)

internal fun AnnotatedText.hasLineMetrics(
    style: TextStyle,
    inlineContent: Map<String, InlineTextContent>,
): Boolean {
    val changesFontMetrics = inlineContent.isNotEmpty() || text.spanStyles.any { it.item.changesFontMetrics() }
    return metrics.isNotEmpty() || (style.lineHeight.isSpecified && changesFontMetrics)
}

private fun SpanStyle.changesFontMetrics(): Boolean {
    return fontSize.isSpecified || fontFamily != null || fontWeight != null
}

@Composable
internal fun rememberLineMetricsText(
    text: AnnotatedText,
    style: TextStyle,
    inlineContent: Map<String, InlineTextContent>,
    measurer: TextMeasurer,
    availableWidth: IntState,
    maxLines: Int,
): TextLines {
    val density = LocalDensity.current
    val fontSizePx = with(density) { style.fontSize.toPx() }
    val lineHeightPx = when {
        style.lineHeight.isEm -> (style.lineHeight.value * fontSizePx).roundToInt()
        style.lineHeight.isSpecified -> with(density) { style.lineHeight.roundToPx() }
        else -> null
    }
    val needsLayout = lineHeightPx != null || text.metrics.any { it.needsLayout } ||
        text.needsLineFrames
    val width = if (needsLayout) availableWidth.intValue else Constraints.Infinity
    return remember(text, style, inlineContent, measurer, width, fontSizePx, lineHeightPx, maxLines) {
        val naturalStyle = style.copy(lineHeight = TextUnit.Unspecified, lineHeightStyle = null)
        val measure = { value: AnnotatedString ->
            measurer.measure(
                text = value,
                style = naturalStyle,
                constraints = Constraints(maxWidth = width),
                placeholders = value.placeholders(inlineContent),
            )
        }
        val layout = if (needsLayout) measure(text.text.withParagraphsAtLineFeeds()) else null
        val lines = text.withLineMetrics(layout, lineHeightPx, fontSizePx, style.hyphens, maxLines, measure)
        TextLines(lines.text, inlineContent.alignedToLines(text.text, lines.lines))
    }
}
