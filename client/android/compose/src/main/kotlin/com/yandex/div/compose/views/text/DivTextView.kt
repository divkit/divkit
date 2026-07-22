package com.yandex.div.compose.views.text

import android.annotation.SuppressLint
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
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivText
import com.yandex.div2.DivTextGradient

@Composable
internal fun DivTextView(
    modifier: Modifier,
    data: DivText
) {
    checkUnsupportedFeatures(data)

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
    val maxLines = data.maxLines.observedIntValue(Int.MAX_VALUE).coerceAtLeast(1)
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
    val gradientBrush = data.textGradient?.observedValue()
    val ranges = data.ranges.orEmpty()
    if (gradientBrush == null && ranges.isEmpty()) {
        return null
    }

    val length = text.length
    val builder = AnnotatedString.Builder(text)
    if (gradientBrush != null) {
        builder.addStyle(SpanStyle(brush = gradientBrush), 0, length)
    }

    val baseTextColorAlpha = textStyle.color.alpha
    for (range in ranges) {
        checkUnsupportedFeatures(range)
        val start = range.start.observedIntValue().coerceIn(0, length)
        val end = range.end.observedIntValue(length).coerceIn(start, length)
        if (start >= end) {
            continue
        }
        builder.addStyle(
            style = range.observeSpanStyle(baseFontSize, baseTextColorAlpha),
            start = start,
            end = end
        )
    }

    return builder.toAnnotatedString()
}

private fun DivText.Truncate.toTextOverflow(): TextOverflow {
    return when (this) {
        DivText.Truncate.NONE -> TextOverflow.Clip
        DivText.Truncate.START -> TextOverflow.StartEllipsis
        DivText.Truncate.END -> TextOverflow.Ellipsis
        DivText.Truncate.MIDDLE -> TextOverflow.MiddleEllipsis
    }
}

@Composable
private fun DivTextGradient.observedValue(): Brush? {
    return when (this) {
        is DivTextGradient.Linear -> value.observeLinearGradient()
        is DivTextGradient.Radial -> value.observeRadialGradient()
    }
}

@SuppressLint("ComposableNaming")
@Composable
private fun checkUnsupportedFeatures(data: DivText) {
    if (!data.images.isNullOrEmpty()) {
        reportError("div-text.images not supported")
    }

    if (data.rangeBuilder != null) {
        reportError("div-text.range_builder not supported")
    }
}

@SuppressLint("ComposableNaming")
@Composable
private fun checkUnsupportedFeatures(range: DivText.Range) {
    if (range.background != null) {
        reportError("div-text.range.background not supported")
    }

    if (range.alignmentVertical != null) {
        reportError("div-text.range.alignment_vertical not supported")
    }

    if (range.topOffset != null) {
        reportError("div-text.range.top_offset not supported")
    }
}

private const val SOFT_HYPHEN = '\u00AD'
