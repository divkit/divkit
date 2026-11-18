package com.yandex.div.compose.views.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivText
import kotlin.math.ceil

/**
 * Limits on the visible lines of a text: `max_lines`, `min_hidden_lines` and `auto_ellipsize`.
 */
internal data class TextLineLimits(
    /**
     * `max_lines`, or `null` when the number of lines is not limited.
     */
    val maxLines: Int?,
    val minHiddenLines: Int,
    /**
     * `auto_ellipsize`, off when `truncate` is `none`: the container clips without a marker.
     */
    val autoEllipsize: Boolean,
) {

    /**
     * Whether the text may be cropped with a marker.
     */
    val truncates: Boolean
        get() = maxLines != null || autoEllipsize

    /**
     * Whether the number of visible lines depends on the layout: the container height with
     * `auto_ellipsize`, or the height limit of `min_hidden_lines`.
     */
    val limitsLinesByLayout: Boolean
        get() = autoEllipsize || minHiddenLines > 0

    /**
     * Lines the text is laid out with. Cropping to `max_lines` with hidden lines depends on the
     * measured width. Native text uses [textHeightLimit]; measured text resolves the line limit
     * before appending its ellipsis, so the marker can change the final line height.
     */
    val layoutMaxLines: Int
        get() = when {
            maxLines == null -> Int.MAX_VALUE
            minHiddenLines > 0 ->
                (maxLines.toLong() + minHiddenLines).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            else -> maxLines
        }
}

@Composable
internal fun DivText.observeLineLimits(): TextLineLimits {
    val maxLines = maxLines?.observedIntValue()?.coerceAtLeast(1)
    return TextLineLimits(
        maxLines = maxLines,
        minHiddenLines = if (maxLines != null) minHiddenLines.observedSaturatedIntValue() else 0,
        autoEllipsize = autoEllipsize.observedValue(false) && truncate.observedValue().hasMarker(),
    )
}

private fun DivText.Truncate.hasMarker(): Boolean {
    return when (this) {
        DivText.Truncate.NONE -> false
        DivText.Truncate.START, DivText.Truncate.MIDDLE, DivText.Truncate.END -> true
    }
}

@Composable
private fun Expression<Long>?.observedSaturatedIntValue(): Int {
    return observedValue(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}

internal fun TextMeasurer.measureMaxLines(
    limits: TextLineLimits,
    text: AnnotatedString,
    style: TextStyle,
    width: Int,
    inlineContent: Map<String, InlineTextContent>,
): Int {
    if (limits.minHiddenLines == 0) return limits.layoutMaxLines
    val layout = measure(
        text = text,
        style = style,
        maxLines = limits.layoutMaxLines,
        placeholders = text.placeholders(inlineContent),
        constraints = Constraints(maxWidth = width),
    )
    return if (layout.didOverflowHeight) requireNotNull(limits.maxLines) else limits.layoutMaxLines
}

/**
 * Applies the height limits of a text with `max_lines`. Without `auto_ellipsize` the text is
 * measured without the height constraint, so that the standard ellipsis does not move to the
 * last line that fits the container, and is clipped instead. With `min_hidden_lines` the text
 * longer than `max_lines + min_hidden_lines` lines is limited to the height of `max_lines` lines.
 *
 * [text] and [inlineContent] are the source text and its inline content laid out with [style].
 */
@Composable
internal fun Modifier.textHeightLimit(
    limits: TextLineLimits,
    text: AnnotatedString,
    style: TextStyle,
    inlineContent: Map<String, InlineTextContent>,
): Modifier {
    val maxLines = limits.maxLines
    if (maxLines == null || limits.autoEllipsize && limits.minHiddenLines == 0) {
        return this
    }

    val hiddenLines = if (limits.minHiddenLines > 0) {
        HiddenLines(
            measurer = rememberTextMeasurer(),
            text = text,
            style = style,
            placeholders = remember(text, inlineContent) { text.placeholders(inlineContent) },
            maxLines = maxLines,
            minHiddenLines = limits.minHiddenLines,
        )
    } else {
        null
    }
    val clipsContent = remember { mutableStateOf(false) }
    return graphicsLayer { clip = clipsContent.value }
        .then(TextHeightLimitElement(
            autoEllipsize = limits.autoEllipsize,
            hiddenLines = hiddenLines,
            clipsContent = clipsContent,
        ))
}

/**
 * Android draws the start and middle markers only on a single-line layout. When the height
 * available to the text fits exactly one of its lines (the container with `auto_ellipsize`, or
 * the height limit of `min_hidden_lines`), the text has to be laid out with `maxLines = 1`. The
 * height is known only after layout, so [SingleLineTruncation.onTextLayout] reports it and the
 * next composition applies the limit.
 */
@Composable
internal fun rememberSingleLineTruncation(
    text: AnnotatedString,
    style: TextStyle,
    inlineContent: Map<String, InlineTextContent>,
): SingleLineTruncation {
    val measurer = rememberTextMeasurer()
    val placeholders = remember(text, inlineContent) { text.placeholders(inlineContent) }
    val currentText = rememberUpdatedState(text)
    val currentStyle = rememberUpdatedState(style)
    val currentPlaceholders = rememberUpdatedState(placeholders)
    val isSingleLine = remember { mutableStateOf(false) }
    val onTextLayout = remember(measurer) {
        { layout: TextLayoutResult ->
            isSingleLine.value = measurer.fitsOneLine(
                text = currentText.value,
                style = currentStyle.value,
                placeholders = currentPlaceholders.value,
                constraints = layout.layoutInput.constraints,
            )
        }
    }
    return SingleLineTruncation(isSingleLine = isSingleLine.value, onTextLayout = onTextLayout)
}

internal data class SingleLineTruncation(
    val isSingleLine: Boolean,
    val onTextLayout: (TextLayoutResult) -> Unit,
)

/**
 * Whether the text needs more than one line and only the first one fits [constraints].
 */
private fun TextMeasurer.fitsOneLine(
    text: AnnotatedString,
    style: TextStyle,
    placeholders: List<AnnotatedString.Range<Placeholder>>,
    constraints: Constraints,
): Boolean {
    if (!constraints.hasBoundedHeight) {
        return false
    }
    val layout = measure(
        text = text,
        style = style,
        maxLines = 2,
        placeholders = placeholders,
        constraints = Constraints(maxWidth = constraints.maxWidth),
    )
    return layout.lineCount == 2 &&
        layout.getLineBottom(0) <= constraints.maxHeight &&
        layout.getLineBottom(1) > constraints.maxHeight
}

private data class HiddenLines(
    val measurer: TextMeasurer,
    val text: AnnotatedString,
    val style: TextStyle,
    val placeholders: List<AnnotatedString.Range<Placeholder>>,
    val maxLines: Int,
    val minHiddenLines: Int,
) {

    /**
     * Height of [maxLines] lines when the text needs more than `maxLines + minHiddenLines` lines.
     */
    fun croppedHeight(width: Int): Int {
        val uncroppedLines = maxLines.toLong() + minHiddenLines
        if (uncroppedLines >= Int.MAX_VALUE) {
            return Constraints.Infinity
        }
        val layout = measurer.measure(
            text = text,
            style = style,
            maxLines = uncroppedLines.toInt() + 1,
            placeholders = placeholders,
            constraints = Constraints(maxWidth = width),
        )
        if (layout.lineCount <= uncroppedLines) {
            return Constraints.Infinity
        }
        return ceil(layout.getLineBottom(maxLines - 1)).toInt()
    }
}

private data class TextHeightLimitElement(
    val autoEllipsize: Boolean,
    val hiddenLines: HiddenLines?,
    val clipsContent: MutableState<Boolean>,
) : ModifierNodeElement<TextHeightLimitNode>() {
    override fun create() = TextHeightLimitNode(this)

    override fun update(node: TextHeightLimitNode) {
        node.params = this
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "textHeightLimit"
    }
}

private class TextHeightLimitNode(
    var params: TextHeightLimitElement,
) : Modifier.Node(), LayoutModifierNode {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val containerHeight = if (params.autoEllipsize) constraints.maxHeight else Constraints.Infinity
        val maxHeight = minOf(containerHeight, croppedHeight(constraints.maxWidth))
        val placeable = measurable.measure(
            constraints.copy(
                minHeight = minOf(constraints.minHeight, maxHeight),
                maxHeight = maxHeight,
            )
        )
        val height = constraints.constrainHeight(placeable.height)
        params.clipsContent.value = height < placeable.height
        return layout(placeable.width, height) {
            placeable.placeRelative(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        minOf(measurable.minIntrinsicHeight(width), croppedHeight(width))

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        minOf(measurable.maxIntrinsicHeight(width), croppedHeight(width))

    private fun croppedHeight(width: Int): Int {
        return params.hiddenLines?.croppedHeight(width) ?: Constraints.Infinity
    }
}
