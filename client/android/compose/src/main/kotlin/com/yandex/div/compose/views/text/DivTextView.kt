package com.yandex.div.compose.views.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.observedEnabledActions
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.gradient.observeLinearGradient
import com.yandex.div.compose.utils.gradient.observeRadialGradient
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAction
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
    val maxLines = data.maxLines.observedIntValue(Int.MAX_VALUE).coerceAtLeast(1)
    val textStyle = data.observeTextStyle(fontSize, horizontalAlignment, hyphens)
    val gradientBrush = data.textGradient?.observedValue()
    val ellipsis = data.ellipsis?.takeIf { data.maxLines != null }
    val ellipsisText = ellipsis?.text?.observedValue()
    val customEllipsis = if (ellipsis != null && ellipsisText != null &&
        (!ellipsis.isPlain() || ellipsisText != DEFAULT_ELLIPSIS)
    ) {
        ellipsis.reportUnsupportedProperties()
        buildAnnotatedText(
            text = ellipsisText,
            ranges = ellipsis.ranges,
            gradientBrush = gradientBrush,
            baseFontSize = fontSize,
            baseTextColorAlpha = textStyle.color.alpha,
            actions = ellipsis.actions
        ) ?: AnnotatedString(ellipsisText)
    } else {
        null
    }
    val overflow = if (data.maxLines == null || customEllipsis != null) {
        TextOverflow.Clip
    } else {
        data.truncate.observedValue().toTextOverflow()
    }
    val annotatedString = buildAnnotatedText(
        text = text,
        ranges = data.ranges,
        gradientBrush = gradientBrush,
        baseFontSize = fontSize,
        baseTextColorAlpha = textStyle.color.alpha
    )
    when {
        customEllipsis != null -> EllipsizedText(
            text = annotatedString ?: AnnotatedString(text),
            ellipsis = customEllipsis,
            style = textStyle,
            maxLines = maxLines
        )

        annotatedString == null -> BasicText(
            text = text,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines
        )

        else -> BasicText(
            text = annotatedString,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}

@Composable
private fun EllipsizedText(
    text: AnnotatedString,
    ellipsis: AnnotatedString,
    style: TextStyle,
    maxLines: Int
) {
    val measurer = rememberTextMeasurer()
    // The View renderer measures the truncation point with Layout.Alignment.ALIGN_NORMAL, so the
    // offsets below are relative to the start edge of the line regardless of the text alignment.
    val measuredStyle = remember(style) { style.copy(textAlign = TextAlign.Start) }
    val availableWidth = remember { mutableIntStateOf(Constraints.Infinity) }
    val onTextLayout = remember {
        { layout: TextLayoutResult -> availableWidth.intValue = layout.layoutInput.constraints.maxWidth }
    }
    val ellipsisWidth = remember(measurer, ellipsis, measuredStyle) {
        measurer.measure(text = ellipsis, style = measuredStyle, softWrap = false)
            .multiParagraph.getLineWidth(0)
    }
    val ellipsizedText = remember(measurer, text, ellipsis, measuredStyle, maxLines, availableWidth.intValue) {
        ellipsize(measurer, text, ellipsis, ellipsisWidth, measuredStyle, maxLines, availableWidth.intValue)
    }
    // Containers that size themselves by intrinsics must keep seeing the full text. Otherwise the
    // shorter ellipsized text reports a smaller intrinsic width, the container shrinks and the text
    // gets truncated again on every pass.
    val intrinsics = remember(measurer, text, measuredStyle) {
        UntruncatedTextIntrinsics(measurer, text, measuredStyle)
    }
    if (ellipsizedText.hasAnnotations()) {
        BasicText(
            modifier = intrinsics,
            text = ellipsizedText,
            style = style,
            overflow = TextOverflow.Clip,
            maxLines = maxLines,
            onTextLayout = onTextLayout
        )
    } else {
        BasicText(
            modifier = intrinsics,
            text = ellipsizedText.text,
            style = style,
            overflow = TextOverflow.Clip,
            maxLines = maxLines,
            onTextLayout = onTextLayout
        )
    }
}

// Only the maximum intrinsic width is overridden: it is the single intrinsic that both changes with
// truncation and is queried by the containers (`width(IntrinsicSize.Max)`). Truncation always cuts
// inside the last allowed line, so the ellipsized text keeps the same line count and reports the
// same heights as the full one.
private class UntruncatedTextIntrinsics(
    private val measurer: TextMeasurer,
    private val text: AnnotatedString,
    private val style: TextStyle
) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int {
        return measurer.measure(text = text, style = style, softWrap = false).size.width
    }
}

private fun ellipsize(
    measurer: TextMeasurer,
    text: AnnotatedString,
    ellipsis: AnnotatedString,
    ellipsisWidth: Float,
    style: TextStyle,
    maxLines: Int,
    availableWidth: Int
): AnnotatedString {
    if (text.isEmpty() || availableWidth <= 0) {
        return text
    }

    val layout = measurer.measure(
        text = text,
        style = style,
        overflow = TextOverflow.Clip,
        maxLines = maxLines,
        constraints = Constraints(maxWidth = availableWidth)
    )
    if (!layout.hasVisualOverflow) {
        return text
    }

    val lastLine = minOf(maxLines, layout.lineCount) - 1
    if (availableWidth == Constraints.Infinity) {
        // A wrap_content text is measured unbounded, so only the line count can overflow. There is
        // no width budget to reserve for the ellipsis: append it right after the last visible line.
        return text.truncatedTo(layout.getLineEnd(lastLine, visibleEnd = true), ellipsis)
    }

    val ellipsizedTextWidth = availableWidth - ellipsisWidth
    if (ellipsizedTextWidth <= 0f) {
        return text
    }

    val lastLineCenter = (layout.getLineTop(lastLine) + layout.getLineBottom(lastLine)) / 2
    var fittedSymbols = layout.getOffsetForPosition(Offset(ellipsizedTextWidth, lastLineCenter))
    // It may be required to remove the last symbol from the text to fit ellipsis
    // But there can be a non-printable zero-width symbol, so we need to iterate until ellipsis fits
    while (fittedSymbols > 0 && layout.getHorizontalPosition(fittedSymbols, true) > ellipsizedTextWidth) {
        fittedSymbols--
    }
    // Dropping last symbol if it represents a first byte of two-byte unicode symbol
    if (fittedSymbols > 0 && text[fittedSymbols - 1].isHighSurrogate()) {
        fittedSymbols--
    }
    if (fittedSymbols <= 0) {
        return text
    }

    return text.truncatedTo(fittedSymbols, ellipsis)
}

private fun AnnotatedString.truncatedTo(length: Int, ellipsis: AnnotatedString): AnnotatedString {
    return buildAnnotatedString {
        append(subSequence(0, length))
        append(ellipsis)
    }
}

@Composable
private fun buildAnnotatedText(
    text: String,
    ranges: List<DivText.Range>?,
    gradientBrush: Brush?,
    baseFontSize: Int,
    baseTextColorAlpha: Float,
    actions: List<DivAction>? = null
): AnnotatedString? {
    if (gradientBrush == null && ranges.isNullOrEmpty() && actions.isNullOrEmpty()) {
        return null
    }

    val length = text.length
    val builder = AnnotatedString.Builder(text)
    if (gradientBrush != null) {
        builder.addStyle(SpanStyle(brush = gradientBrush), 0, length)
    }
    if (!actions.isNullOrEmpty() && length > 0) {
        val enabledActions = actions.observedEnabledActions()
        if (enabledActions.isNotEmpty()) {
            builder.addLink(rememberActionsLink(enabledActions), 0, length)
        }
    }

    ranges?.forEach { range ->
        val start = range.start.observedIntValue().coerceIn(0, length)
        val end = range.end.observedIntValue(length).coerceIn(start, length)
        if (start < end) {
            builder.addStyle(
                style = range.observeSpanStyle(baseFontSize, baseTextColorAlpha),
                start = start,
                end = end
            )
            val rangeActions = range.actions.observedEnabledActions()
            if (rangeActions.isNotEmpty()) {
                // Added after the whole text link so that it wins the hit test: a tap reaches the
                // topmost link only, like on iOS where the range actions replace the ones of the
                // ellipsis. The View renderer instead runs every action span under the tap.
                builder.addLink(rememberActionsLink(rangeActions), start, end)
            }
        }
    }

    return builder.toAnnotatedString()
}

// A link consumes the tap it receives, so the actions of the text element itself do not run under
// a span and its long tap actions are unreachable there. The View renderer runs both: TextView
// performs the click of the element before handing the touch to the movement method.
@Composable
private fun rememberActionsLink(actions: List<DivAction>): LinkAnnotation.Clickable {
    val localComponent = LocalComponent.current
    return remember(actions) {
        LinkAnnotation.Clickable(
            tag = ACTIONS_LINK_TAG,
            linkInteractionListener = { localComponent.handleActions(actions, DivActionSource.TAP) }
        )
    }
}

private fun DivText.Ellipsis.isPlain(): Boolean {
    return ranges == null && rangeBuilder == null && images == null && imageBuilder == null && actions == null
}

@Composable
private fun DivText.Ellipsis.reportUnsupportedProperties() {
    if (images != null) {
        reportError("Text ellipsis property not supported: images")
    }
    if (imageBuilder != null) {
        reportError("Text ellipsis property not supported: image_builder")
    }
    if (rangeBuilder != null) {
        reportError("Text ellipsis property not supported: range_builder")
    }
}

private fun AnnotatedString.hasAnnotations(): Boolean {
    return spanStyles.isNotEmpty() || paragraphStyles.isNotEmpty() || hasLinkAnnotations(0, length)
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

private const val ACTIONS_LINK_TAG = "div-action"
private const val SOFT_HYPHEN = '\u00AD'
private const val DEFAULT_ELLIPSIS = "\u2026"
