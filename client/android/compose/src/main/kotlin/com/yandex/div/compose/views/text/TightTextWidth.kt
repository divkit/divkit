package com.yandex.div.compose.views.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeIsConstrained
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.DivSize
import com.yandex.div2.DivText
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Narrowing of the text to its widest line by `tighten_width`, or `null` when the property does
 * not apply. [text], [style], [inlineContent], [limits] and [truncation] describe how the text is
 * rendered.
 */
@Composable
internal fun DivText.observeTightTextWidth(
    text: AnnotatedText,
    style: TextStyle,
    inlineContent: Map<String, InlineTextContent>,
    limits: TextLineLimits,
    truncation: TextTruncation,
): TightTextWidth? {
    val maxWidth = observeTightTextMaxWidth() ?: return null
    val latestLayout = remember { LatestTextLayout() }
    val measurer = rememberTextMeasurer()
    val intrinsics = TightTextIntrinsics(measurer, text, style, inlineContent, limits, truncation, maxWidth)
    return TightTextWidth(latestLayout, intrinsics)
}

/**
 * How a text that does not fit its lines is cut: natively by `BasicText` with [truncate], or,
 * when the text [isMeasured], by the custom or the standard ellipsis measured with [policy].
 */
internal data class TextTruncation(
    val truncate: DivText.Truncate,
    val policy: DivText.TruncatePolicy,
    private val customEllipsis: AnnotatedText?,
    private val isMeasured: Boolean,
) {
    val ellipsis: AnnotatedText?
        get() = if (isMeasured) customEllipsis ?: defaultEllipsis else null
}

/**
 * The width available to the lines of a text that `tighten_width` limits to its widest line, or
 * `null` when the property does not apply. It applies only to a `wrap_content` width with
 * `constrained` and `max_size`, unless `min_size` exceeds `max_size` and both are ignored.
 */
@Composable
private fun DivText.observeTightTextMaxWidth(): TextMaxWidth? {
    val size = (width as? DivSize.WrapContent)?.value ?: return null
    val maxSize = size.maxSize ?: return null
    if (!tightenWidth.observedValue() || !width.observeIsConstrained()) return null
    val max = maxSize.observedValue()
    val min = size.minSize?.observedValue()
    if (min != null && min > max) return null
    val (start, end) = paddings.observeHorizontalInsets()
    return TextMaxWidth(maxSize = max, startPadding = start, endPadding = end)
}

/**
 * `max_size` and the horizontal paddings inside it, rounded to pixels one by one as the size and
 * padding modifiers do.
 */
internal data class TextMaxWidth(val maxSize: Dp, val startPadding: Dp, val endPadding: Dp) {
    fun Density.textWidthPx(): Int =
        (maxSize.roundToPx() - startPadding.roundToPx() - endPadding.roundToPx()).coerceAtLeast(0)
}

/**
 * Narrows a text to its widest line. A text that wraps takes the whole width available to it,
 * so [modifier], applied to the text node, reads the lines of the layout the text has just made
 * through [onTextLayout] and places the text so that its lines stay where they are drawn.
 */
internal class TightTextWidth(
    private val latestLayout: LatestTextLayout,
    private val intrinsics: TightTextIntrinsics,
) {
    val onTextLayout: (TextLayoutResult) -> Unit
        get() = latestLayout.onTextLayout

    val modifier: Modifier
        get() = TightTextWidthElement(latestLayout, intrinsics)
}

/**
 * Calls both callbacks with the layout of a text, either of which may be absent.
 */
@Composable
internal fun rememberTextLayoutCallback(
    first: ((TextLayoutResult) -> Unit)?,
    second: ((TextLayoutResult) -> Unit)?,
): ((TextLayoutResult) -> Unit)? {
    if (first == null) return second
    if (second == null) return first
    return remember(first, second) {
        { layout: TextLayoutResult ->
            first(layout)
            second(layout)
        }
    }
}

/**
 * The latest layout of a text.
 */
internal class LatestTextLayout {

    private var layout: TextLayoutResult? = null

    val onTextLayout: (TextLayoutResult) -> Unit = { layout = it }

    /**
     * Bounds of the lines laid out [width] wide that start above [height], or `null` when they
     * fill the width.
     */
    fun bounds(width: Int, height: Int): LineBounds? {
        val layout = layout?.takeIf { it.size.width == width } ?: return null
        return layout.lineBounds(visibleHeight = height)?.takeIf { it.width < width }
    }
}

/**
 * Intrinsic width of a narrowed text: the widest of its visible lines laid out [maxWidth] wide,
 * cut and ellipsized as the text is rendered.
 */
internal data class TightTextIntrinsics(
    private val measurer: TextMeasurer,
    private val text: AnnotatedText,
    private val style: TextStyle,
    private val inlineContent: Map<String, InlineTextContent>,
    private val limits: TextLineLimits,
    private val truncation: TextTruncation,
    private val maxWidth: TextMaxWidth,
) {

    fun Density.maxIntrinsicWidth(textWidth: Int): Int {
        val width = with(maxWidth) { textWidthPx() }
        // Without a line limit every paragraph that fits stays a line of its own.
        if (textWidth <= width && limits.maxLines == null) return textWidth
        val layout = layout(width)
        val bounds = layout.lineBounds(visibleHeight = Constraints.Infinity) ?: return layout.size.width
        return minOf(bounds.width, layout.size.width)
    }

    private fun layout(width: Int): TextLayoutResult {
        val maxLines = measurer.measureMaxLines(limits, text.text, style, width, inlineContent)
        val ellipsis = truncation.ellipsis
        val rendered = if (ellipsis == null) {
            null
        } else {
            ellipsizeTextEnd(
                measurer, text, ellipsis, style, maxLines, width, truncation.policy, inlineContent,
                maxHeight = Constraints.Infinity,
            )
        }
        val renderedText = (rendered as? TextEllipsisResult.Render)?.text?.text ?: text.text
        val overflow = when {
            rendered is TextEllipsisResult.Render -> TextOverflow.Clip
            rendered == TextEllipsisResult.UseNativeRendering -> TextOverflow.Ellipsis
            limits.truncates -> truncation.truncate.toTextOverflow()
            else -> TextOverflow.Clip
        }
        return measurer.measure(
            text = renderedText,
            style = style,
            overflow = overflow,
            maxLines = maxLines,
            placeholders = renderedText.placeholders(inlineContent),
            constraints = Constraints(maxWidth = width),
        )
    }
}

internal data class LineBounds(val left: Int, val right: Int) {
    val width: Int
        get() = right - left
}

private fun TextLayoutResult.lineBounds(visibleHeight: Int): LineBounds? {
    var left = Float.POSITIVE_INFINITY
    var right = Float.NEGATIVE_INFINITY
    for (line in 0 until lineCount) {
        if (getLineTop(line) >= visibleHeight) break
        left = minOf(left, getLineLeft(line))
        right = maxOf(right, getLineRight(line))
    }
    if (left > right) return null
    return LineBounds(left = floor(left).toInt(), right = ceil(right).toInt())
}

private data class TightTextWidthElement(
    val latestLayout: LatestTextLayout,
    val intrinsics: TightTextIntrinsics,
) : ModifierNodeElement<TightTextWidthNode>() {
    override fun create() = TightTextWidthNode(latestLayout, intrinsics)

    override fun update(node: TightTextWidthNode) {
        node.latestLayout = latestLayout
        node.intrinsics = intrinsics
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "tightTextWidth"
    }
}

private class TightTextWidthNode(
    var latestLayout: LatestTextLayout,
    var intrinsics: TightTextIntrinsics,
) : Modifier.Node(), LayoutModifierNode {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        val bounds = latestLayout.bounds(placeable.width, placeable.height)
            ?: return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
        // The lines are positioned in the layout of the text, so the offset is not mirrored in RTL.
        return layout(constraints.constrainWidth(bounds.width), placeable.height) {
            placeable.place(-bounds.left, 0)
        }
    }

    // A parent that sizes itself by its widest child, like a wrap_content column with a
    // match_parent child, needs the narrowed width before the text is measured.
    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int =
        with(intrinsics) { maxIntrinsicWidth(measurable.maxIntrinsicWidth(height)) }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int) =
        measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int) =
        measurable.maxIntrinsicHeight(width)
}
