package com.yandex.div.compose.views.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.gradient.observeLinearGradient
import com.yandex.div.compose.utils.gradient.observeRadialGradient
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivText
import com.yandex.div2.DivTextGradient

@Composable
internal fun DivTextView(
    modifier: Modifier,
    data: DivText
) {
    val textAlignmentHorizontal = data.textAlignmentHorizontal.observedValue()
    val textAlignmentVertical = data.textAlignmentVertical.observedValue()
    Box(
        modifier = modifier.semantics(mergeDescendants = true) {},
        contentAlignment = toAlignment(textAlignmentHorizontal, textAlignmentVertical)
    ) {
        if (data.selectable.observedValue()) {
            SelectionContainer {
                BasicText(data = data, horizontalAlignment = textAlignmentHorizontal)
            }
        } else {
            BasicText(data = data, horizontalAlignment = textAlignmentHorizontal)
        }
    }
}

@Composable
private fun BasicText(
    data: DivText,
    horizontalAlignment: DivAlignmentHorizontal
) {
    val text = data.text.observedValue()
    val fontSize = data.fontSize.observedIntValue()
    val hyphens = if (SOFT_HYPHEN in text) Hyphens.Auto else Hyphens.None
    val maxLines = data.maxLines?.observedIntValue()?.coerceAtLeast(1) ?: Int.MAX_VALUE
    val overflow = if (data.maxLines != null) {
        data.truncate.observedValue().toTextOverflow()
    } else {
        TextOverflow.Clip
    }
    val textStyle = data.observeTextStyle(fontSize, horizontalAlignment, hyphens)
    val annotatedString = buildAnnotatedText(text, textStyle, data, fontSize)
    if (annotatedString == null) {
        BasicText(
            text = text,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines
        )
    } else {
        BasicText(
            text = annotatedString,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}

@Composable
private fun buildAnnotatedText(
    text: String,
    textStyle: TextStyle,
    data: DivText,
    baseFontSize: Int,
): AnnotatedString? {
    val gradientBrush = data.observedTextGradient()
    val ranges = data.ranges ?: emptyList()
    if (gradientBrush == null && ranges.isEmpty()) {
        return null
    }

    val builder = AnnotatedString.Builder(text)
    if (gradientBrush != null) {
        builder.addStyle(SpanStyle(brush = gradientBrush), 0, text.length)
    }

    val baseTextColorAlpha = textStyle.color.alpha
    for (range in ranges) {
        val start = range.start.observedIntValue()
        val end = range.end?.observedIntValue() ?: text.length
        val safeStart = start.coerceIn(0, text.length)
        val safeEnd = end.coerceIn(safeStart, text.length)
        if (safeStart >= safeEnd) continue

        val spanStyle = range.observeSpanStyle(baseFontSize, baseTextColorAlpha)
        builder.addStyle(spanStyle, safeStart, safeEnd)
    }

    return builder.toAnnotatedString()
}

@Composable
private fun DivText.Truncate.toTextOverflow(): TextOverflow {
    return when (this) {
        DivText.Truncate.NONE -> TextOverflow.Clip
        DivText.Truncate.START -> TextOverflow.StartEllipsis
        DivText.Truncate.END -> TextOverflow.Ellipsis
        DivText.Truncate.MIDDLE -> TextOverflow.MiddleEllipsis
    }
}

@Composable
private fun DivText.observedTextGradient(): Brush? {
    val textGradient = textGradient ?: return null
    return when (textGradient) {
        is DivTextGradient.Linear -> textGradient.value.observeLinearGradient()
        is DivTextGradient.Radial -> textGradient.value.observeRadialGradient()
    }
}

private const val SOFT_HYPHEN = '\u00AD'
