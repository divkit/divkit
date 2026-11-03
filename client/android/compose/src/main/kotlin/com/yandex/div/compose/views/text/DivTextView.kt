package com.yandex.div.compose.views.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.observedEnabledActions
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.handleActions
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivText
import kotlin.math.roundToInt

@Composable
internal fun DivTextView(
    modifier: Modifier,
    data: DivText,
    focusState: State<Boolean>?,
) {
    val textAlignmentHorizontal = data.textAlignmentHorizontal.observedValue()
    val textAlignmentVertical = data.textAlignmentVertical.observedValue()
    BasicText(
        data = data,
        horizontalAlignment = textAlignmentHorizontal,
        focusState = focusState,
        modifier = modifier,
        alignment = toAlignment(textAlignmentHorizontal, textAlignmentVertical),
        selectable = data.selectable.observedValue(),
    )
}

@Composable
private fun BasicText(
    data: DivText,
    horizontalAlignment: DivAlignmentHorizontal,
    focusState: State<Boolean>?,
    modifier: Modifier,
    alignment: Alignment,
    selectable: Boolean,
) {
    val text = data.text.observedValue()
    data.id?.let { id -> LabelText(id, text) }
    val fontSize = data.fontSize.observedIntValue()
    val hyphens = if (SOFT_HYPHEN in text) Hyphens.Auto else Hyphens.None
    val maxLines = data.maxLines.observedIntValue(Int.MAX_VALUE).coerceAtLeast(1)
    val textMetrics = data.observeTextMetrics()
    val textStyle = data.observeTextStyle(fontSize, horizontalAlignment, hyphens, textMetrics)
    val inlineImages = data.observeInlineImages(text, fontSize, textMetrics, textStyle)
    val gradientBrush = data.textGradient?.observedValue()
    val customEllipsis = data.observeCustomEllipsis(gradientBrush, fontSize, textStyle)
    if (data.hasActionLinks()) {
        CompositionLocalProvider(LocalViewConfiguration provides rememberExactTouchTargetViewConfiguration()) {
            RenderTextContent(
                data, text, fontSize, textStyle, inlineImages, gradientBrush, customEllipsis, maxLines,
                focusState, modifier, alignment, selectable,
            )
        }
    } else {
        RenderTextContent(
            data, text, fontSize, textStyle, inlineImages, gradientBrush, customEllipsis, maxLines,
            focusState, modifier, alignment, selectable,
        )
    }
}

/** Keeps the [text] of the element with [id] in the storage: a validator of an input names its label by `label_id`. */
@Composable
private fun LabelText(id: String, text: String) {
    val storage = LocalDivViewContext.current.component.labelTextStorage
    // The label withdraws its text when the composition forgets it.
    val label = remember(storage, id) { storage.label(id) }
    SideEffect { label.set(text) }
}

@Composable
private fun rememberExactTouchTargetViewConfiguration(): ViewConfiguration {
    val viewConfiguration = LocalViewConfiguration.current
    return remember(viewConfiguration) {
        object : ViewConfiguration by viewConfiguration {
            override val minimumTouchTargetSize = DpSize.Zero
        }
    }
}

private fun DivText.hasActionLinks(): Boolean {
    return ranges.hasActions() || ellipsis?.actions != null || ellipsis?.ranges.hasActions()
}

private fun List<DivText.Range>?.hasActions(): Boolean = this?.any { it.actions != null } == true

@Composable
private fun RenderTextContent(
    data: DivText,
    text: String,
    fontSize: Int,
    baseTextStyle: TextStyle,
    inlineImages: List<InlineImageData>,
    gradientBrush: Brush?,
    customEllipsis: AnnotatedText?,
    maxLines: Int,
    focusState: State<Boolean>?,
    modifier: Modifier,
    alignment: Alignment,
    selectable: Boolean,
) {
    val hasMaxLines = data.maxLines != null
    val truncate = data.observeTruncate(hasMaxLines, customEllipsis)
    val truncatePolicy = data.observeTruncatePolicy(hasMaxLines, customEllipsis, truncate)
    val overflow = if (!hasMaxLines || customEllipsis != null) {
        TextOverflow.Clip
    } else {
        truncate.toTextOverflow()
    }
    val annotatedText = data.buildAnnotatedText(
        text = text,
        ranges = data.ranges,
        gradientBrush = gradientBrush,
        baseFontSize = fontSize,
        textStyle = baseTextStyle,
        inlineImages = inlineImages,
    )
    val needsMeasuredEndEllipsis = customEllipsis == null && shouldMeasureEndEllipsis(
        hasMaxLines = hasMaxLines,
        truncate = truncate,
        truncatePolicy = truncatePolicy,
        containsInlineImages = inlineImages.isNotEmpty(),
        text = text,
    )
    val textStyle = data.observeFocusedTextStyle(baseTextStyle, focusState)
    val needsMeasuredText = customEllipsis != null || needsMeasuredEndEllipsis
    if (!selectable && !needsMeasuredText && annotatedText == null) {
        BasicText(
            modifier = modifier.plainTextLayout(alignment),
            text = text,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines,
        )
    } else {
        Box(
            modifier = modifier.semantics(mergeDescendants = true) {},
            contentAlignment = alignment,
        ) {
            if (selectable) {
                SelectionContainer {
                    RenderComplexTextContent(
                        text, annotatedText, customEllipsis, textStyle, maxLines, truncatePolicy,
                        inlineImages, overflow, needsMeasuredText,
                    )
                }
            } else {
                RenderComplexTextContent(
                    text, annotatedText, customEllipsis, textStyle, maxLines, truncatePolicy,
                    inlineImages, overflow, needsMeasuredText,
                )
            }
        }
    }
}

// A single-child Box without propagated minimum constraints has the same measure and placement
// behavior. Keeping its wrapper in modifiers avoids an additional LayoutNode for plain text.
internal fun Modifier.plainTextLayout(alignment: Alignment): Modifier =
    semantics(mergeDescendants = true) {}.wrapContentSize(align = alignment, unbounded = false)

@Composable
private fun RenderComplexTextContent(
    text: String,
    annotatedText: AnnotatedText?,
    customEllipsis: AnnotatedText?,
    textStyle: TextStyle,
    maxLines: Int,
    truncatePolicy: DivText.TruncatePolicy,
    inlineImages: List<InlineImageData>,
    overflow: TextOverflow,
    needsMeasuredText: Boolean,
) {
    if (needsMeasuredText) {
        RenderMeasuredText(
            text, annotatedText, customEllipsis, textStyle, maxLines, truncatePolicy, inlineImages,
        )
    } else if (annotatedText == null) {
        BasicText(text = text, style = textStyle, overflow = overflow, maxLines = maxLines)
    } else {
        RenderAnnotatedText(annotatedText, inlineImages, textStyle, overflow, maxLines)
    }
}

@Composable
private fun DivText.observeTruncate(hasMaxLines: Boolean, customEllipsis: AnnotatedText?): DivText.Truncate {
    return if (hasMaxLines && customEllipsis == null) {
        truncate.observedValue()
    } else {
        DivText.Truncate.END
    }
}

@Composable
private fun DivText.observeTruncatePolicy(
    hasMaxLines: Boolean,
    customEllipsis: AnnotatedText?,
    truncate: DivText.Truncate,
): DivText.TruncatePolicy {
    return if (hasMaxLines && (customEllipsis != null || truncate == DivText.Truncate.END)) {
        truncatePolicy.observedValue()
    } else {
        DivText.TruncatePolicy.GRAPHEME
    }
}

@Composable
private fun RenderMeasuredText(
    text: String,
    annotatedText: AnnotatedText?,
    customEllipsis: AnnotatedText?,
    textStyle: TextStyle,
    maxLines: Int,
    truncatePolicy: DivText.TruncatePolicy,
    inlineImages: List<InlineImageData>,
) {
    val inlineContent = rememberInlineContent(inlineImages)
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val rendering = rememberEllipsizedTextRendering(
        sourceText = annotatedText ?: AnnotatedText(AnnotatedString(text), emptyList()),
        ellipsis = customEllipsis ?: AnnotatedText(AnnotatedString(DEFAULT_ELLIPSIS), emptyList()),
        textStyle = textStyle,
        maxLines = maxLines,
        truncatePolicy = truncatePolicy,
        inlineContent = inlineContent,
        layoutResult = layoutResult,
    )
    RenderBasicText(rendering, textStyle, maxLines, layoutResult)
}

@Composable
private fun RenderAnnotatedText(
    annotatedText: AnnotatedText,
    inlineImages: List<InlineImageData>,
    textStyle: TextStyle,
    overflow: TextOverflow,
    maxLines: Int,
) {
    val inlineContent = rememberInlineContent(inlineImages)
    if (annotatedText.hasLineMetrics(textStyle, inlineContent)) {
        RenderTextLines(annotatedText, inlineContent, textStyle, overflow, maxLines)
    } else if (annotatedText.decorations.isEmpty()) {
        BasicText(
            text = annotatedText.text,
            inlineContent = inlineContent,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines,
        )
    } else {
        val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
        val onTextLayout = remember {
            { layout: TextLayoutResult -> layoutResult.value = layout }
        }
        BasicText(
            modifier = Modifier.drawTextRangeDecorations(layoutResult, annotatedText.decorations),
            text = annotatedText.text,
            inlineContent = inlineContent,
            style = textStyle,
            overflow = overflow,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
        )
    }
}

@Composable
private fun RenderTextLines(
    annotatedText: AnnotatedText,
    inlineContent: Map<String, InlineTextContent>,
    textStyle: TextStyle,
    overflow: TextOverflow,
    maxLines: Int,
) {
    val measurer = rememberTextMeasurer()
    val availableWidth = remember { mutableIntStateOf(Constraints.Infinity) }
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val singleLine = maxLines == 1 &&
        (overflow == TextOverflow.StartEllipsis || overflow == TextOverflow.MiddleEllipsis)
    val onTextLayout = remember(singleLine) {
        { layout: TextLayoutResult ->
            availableWidth.intValue = if (singleLine) Constraints.Infinity else layout.layoutInput.constraints.maxWidth
            layoutResult.value = layout
        }
    }
    val intrinsics = remember(measurer, annotatedText, textStyle, inlineContent) {
        SourceTextIntrinsics(measurer, annotatedText.text, textStyle, annotatedText.text.placeholders(inlineContent))
    }
    val lines = rememberLineMetricsText(annotatedText, textStyle, inlineContent, measurer, availableWidth, maxLines)
    val modifier = if (lines.text.text.text == annotatedText.text.text) {
        intrinsics
    } else {
        intrinsics.semantics { contentDescription = annotatedText.text.text }
    }
    BasicText(
        modifier = modifier.drawTextRangeDecorations(layoutResult, lines.text.decorations),
        text = lines.text.text,
        inlineContent = lines.inlineContent,
        style = textStyle,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    )
}

@Composable
private fun rememberLineMetricsText(
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
    val needsLayout = lineHeightPx != null || text.metrics.any { it.needsLayout }
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

private class TextLines(val text: AnnotatedText, val inlineContent: Map<String, InlineTextContent>)

// Native ellipsis can split words, inline images, or complex grapheme clusters.
// Measure the end offset ourselves when any of these need to be preserved.
@Composable
private fun shouldMeasureEndEllipsis(
    hasMaxLines: Boolean,
    truncate: DivText.Truncate,
    truncatePolicy: DivText.TruncatePolicy,
    containsInlineImages: Boolean,
    text: String,
): Boolean {
    if (!hasMaxLines) {
        return false
    }
    if (truncate != DivText.Truncate.END) {
        return false
    }

    if (truncatePolicy == DivText.TruncatePolicy.WORD) {
        return true
    }
    if (containsInlineImages) {
        return true
    }
    return remember(text) { text.hasComplexGraphemes() }
}

@Composable
private fun rememberEllipsizedTextRendering(
    sourceText: AnnotatedText,
    ellipsis: AnnotatedText,
    textStyle: TextStyle,
    maxLines: Int,
    truncatePolicy: DivText.TruncatePolicy,
    inlineContent: Map<String, InlineTextContent>,
    layoutResult: MutableState<TextLayoutResult?>,
): TextRendering {
    val measurer = rememberTextMeasurer()
    // The View renderer measures with ALIGN_NORMAL, so offsets are relative to the start edge.
    val measuredStyle = remember(textStyle) { textStyle.copy(textAlign = TextAlign.Start) }
    val availableWidth = remember { mutableIntStateOf(Constraints.Infinity) }
    val availableHeight = remember { mutableIntStateOf(Constraints.Infinity) }
    val onTextLayout = remember {
        { layout: TextLayoutResult ->
            availableWidth.intValue = layout.layoutInput.constraints.maxWidth
            availableHeight.intValue = layout.layoutInput.constraints.maxHeight
            layoutResult.value = layout
        }
    }
    val ellipsisResult = remember(
        measurer, sourceText, ellipsis, measuredStyle, maxLines,
        availableWidth.intValue, availableHeight.intValue, truncatePolicy, inlineContent,
    ) {
        ellipsizeTextEnd(
            measurer, sourceText, ellipsis, measuredStyle, maxLines,
            availableWidth.intValue, truncatePolicy, inlineContent,
            maxHeight = availableHeight.intValue,
        )
    }
    // Intrinsic-size containers must keep measuring the full text to avoid repeated truncation.
    val intrinsics = remember(measurer, sourceText, measuredStyle, inlineContent) {
        SourceTextIntrinsics(
            measurer, sourceText.text, measuredStyle, sourceText.text.placeholders(inlineContent),
        )
    }
    val rendering = ellipsisResult.toTextRendering(
        sourceText = sourceText,
        inlineContent = inlineContent,
        modifier = intrinsics.semantics { contentDescription = sourceText.text.text },
        onTextLayout = onTextLayout,
    )
    if (!rendering.text.hasLineMetrics(textStyle, inlineContent)) {
        return rendering
    }
    val lines = rememberLineMetricsText(rendering.text, textStyle, inlineContent, measurer, availableWidth, maxLines)
    return rendering.copy(text = lines.text, inlineContent = lines.inlineContent)
}

@Composable
private fun RenderBasicText(
    rendering: TextRendering,
    textStyle: TextStyle,
    maxLines: Int,
    layoutResult: MutableState<TextLayoutResult?>,
) {
    val textModifier = rendering.modifier.drawTextRangeDecorations(layoutResult, rendering.text.decorations)
    if (rendering.text.text.hasAnnotations() || rendering.inlineContent.isNotEmpty()) {
        BasicText(
            modifier = textModifier,
            text = rendering.text.text,
            inlineContent = rendering.inlineContent,
            style = textStyle,
            overflow = rendering.overflow,
            maxLines = maxLines,
            onTextLayout = rendering.onTextLayout,
        )
    } else {
        BasicText(
            modifier = textModifier,
            text = rendering.text.text.text,
            style = textStyle,
            overflow = rendering.overflow,
            maxLines = maxLines,
            onTextLayout = rendering.onTextLayout,
        )
    }
}

private fun TextEllipsisResult.toTextRendering(
    sourceText: AnnotatedText,
    inlineContent: Map<String, InlineTextContent>,
    modifier: Modifier,
    onTextLayout: (TextLayoutResult) -> Unit,
): TextRendering {
    return when (this) {
        TextEllipsisResult.UseNativeRendering -> TextRendering(
            text = sourceText,
            inlineContent = inlineContent,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
            onTextLayout = onTextLayout,
        )

        is TextEllipsisResult.Render -> TextRendering(
            text = text,
            inlineContent = inlineContent,
            overflow = TextOverflow.Clip,
            modifier = modifier,
            onTextLayout = onTextLayout,
        )
    }
}

/** Parameters passed to BasicText after selecting native or measured ellipsis rendering. */
private data class TextRendering(
    val text: AnnotatedText,
    val inlineContent: Map<String, InlineTextContent>,
    val overflow: TextOverflow,
    val modifier: Modifier,
    val onTextLayout: (TextLayoutResult) -> Unit,
)

@Composable
private fun DivText.observeCustomEllipsis(
    gradientBrush: Brush?,
    fontSize: Int,
    textStyle: TextStyle,
): AnnotatedText? {
    val ellipsis = ellipsis?.takeIf { maxLines != null }
    val ellipsisText = ellipsis?.text?.observedValue()
    return if (ellipsis != null && ellipsisText != null &&
        (!ellipsis.isPlain() || ellipsisText != DEFAULT_ELLIPSIS)
    ) {
        ellipsis.reportUnsupportedProperties()
        buildAnnotatedText(
            text = ellipsisText,
            ranges = ellipsis.ranges,
            gradientBrush = gradientBrush,
            baseFontSize = fontSize,
            textStyle = textStyle,
            actions = ellipsis.actions
        ) ?: AnnotatedText(AnnotatedString(ellipsisText), emptyList())
    } else {
        null
    }
}

// Only the maximum intrinsic width is overridden: it is the single intrinsic that both changes with
// truncation or line splitting and is queried by the containers (`width(IntrinsicSize.Max)`).
// Truncation always cuts inside the last allowed line, so the ellipsized text keeps the same line
// count and reports the same heights as the full one.
private class SourceTextIntrinsics(
    private val measurer: TextMeasurer,
    private val text: AnnotatedString,
    private val style: TextStyle,
    private val placeholders: List<AnnotatedString.Range<Placeholder>>,
) : LayoutModifier {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int {
        return measurer.measure(text = text, style = style, softWrap = false, placeholders = placeholders).size.width
    }
}

@Composable
private fun DivText.buildAnnotatedText(
    text: String,
    ranges: List<DivText.Range>?,
    gradientBrush: Brush?,
    baseFontSize: Int,
    textStyle: TextStyle,
    actions: List<DivAction>? = null,
    inlineImages: List<InlineImageData> = emptyList()
): AnnotatedText? {
    if (gradientBrush == null && ranges.isNullOrEmpty() && actions.isNullOrEmpty() && inlineImages.isEmpty()) {
        return null
    }

    val length = text.length
    val builder = AnnotatedString.Builder()
    val decorations = mutableListOf<TextRangeDecoration>()
    val offsets = builder.appendTextWithInlineImages(text, inlineImages)
    builder.addTextGradient(gradientBrush)
    builder.addTextActions(actions, 0, builder.length)
    val metrics = builder.addTextRanges(this, ranges, length, offsets, baseFontSize, textStyle.color.alpha, decorations)
    builder.maskDecoratedRanges(decorations)
    return AnnotatedText(builder.toAnnotatedString(), decorations, rememberRangeMetrics(metrics, textStyle))
}

private fun AnnotatedString.Builder.addTextGradient(gradientBrush: Brush?) {
    if (gradientBrush == null) return
    addStyle(SpanStyle(brush = gradientBrush, alpha = 1f), 0, length)
}

@Composable
@NonRestartableComposable
private fun AnnotatedString.Builder.addTextActions(
    actions: List<DivAction>?,
    start: Int,
    end: Int,
) {
    if (actions.isNullOrEmpty() || start >= end) return
    val enabledActions = actions.observedEnabledActions()
    if (enabledActions.isNotEmpty()) {
        addLink(rememberActionsLink(enabledActions), start, end)
    }
}

@Composable
@NonRestartableComposable
private fun AnnotatedString.Builder.addTextRanges(
    data: DivText,
    ranges: List<DivText.Range>?,
    textLength: Int,
    offsets: OriginalTextOffsets,
    baseFontSize: Int,
    baseTextColorAlpha: Float,
    decorations: MutableList<TextRangeDecoration>,
): List<RangeMetricsInput> {
    val metrics = mutableListOf<RangeMetricsInput>()
    val length = textLength.toLong()
    ranges?.forEach { range ->
        val start = range.start.observedValue().coerceIn(0L, length)
        val end = range.end.observedValue(length).coerceIn(start, length)
        if (start < end) {
            val decorationStart = offsets.rangeStart(start.toInt())
            val decorationEnd = offsets.rangeEnd(end.toInt())
            val decoration = range.observeDecoration(decorationStart, decorationEnd)
            val fontSizeUnit = range.observeFontSizeUnit()
            val spanStyle = range.observeSpanStyle(
                data,
                baseFontSize,
                baseTextColorAlpha,
                fontSizeUnit,
                decoration?.hidesText == true,
            )
            addStyle(spanStyle, decorationStart, decorationEnd)
            if (decoration != null) decorations += decoration
            metrics += range.observeMetrics(decorationStart, decorationEnd, spanStyle, fontSizeUnit)
            val rangeActions = range.actions.observedEnabledActions()
            if (rangeActions.isNotEmpty()) {
                // Added after the whole text link so that it wins the hit test: a tap reaches the
                // topmost link only, like on iOS where the range actions replace the ones of the
                // ellipsis. The View renderer instead runs every action span under the tap.
                addLink(rememberActionsLink(rangeActions), decorationStart, decorationEnd)
            }
        }
    }
    return metrics
}

@Composable
@NonRestartableComposable
private fun AnnotatedString.Builder.maskDecoratedRanges(decorations: List<TextRangeDecoration>) {
    if (decorations.any(TextRangeDecoration::hidesText)) {
        val maskedSpanStyle = rememberMaskedSpanStyle()
        decorations.forEach { decoration ->
            if (decoration.hidesText) {
                addStyle(maskedSpanStyle, decoration.start, decoration.end)
            }
        }
    }
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
            linkInteractionListener = {
                localComponent.handleActions(actions, DivActionSource.TAP)
            }
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

internal fun AnnotatedString.replacingChars(chars: Map<Int, Char>): AnnotatedString {
    if (chars.isEmpty()) return this
    val annotations = mutableListOf<AnnotatedString.Range<out AnnotatedString.Annotation>>()
    mapAnnotations { annotation ->
        annotations += annotation
        annotation
    }
    val text = text.toCharArray()
    chars.forEach { (offset, char) -> text[offset] = char }
    return AnnotatedString(String(text), annotations)
}

internal fun AnnotatedString.hasAnnotations(): Boolean {
    return spanStyles.isNotEmpty() || paragraphStyles.isNotEmpty() || hasLinkAnnotations(0, length)
}

private fun AnnotatedText.hasLineMetrics(
    style: TextStyle,
    inlineContent: Map<String, InlineTextContent>,
): Boolean {
    val changesFontMetrics = inlineContent.isNotEmpty() || text.spanStyles.any { it.item.changesFontMetrics() }
    return metrics.isNotEmpty() || (style.lineHeight.isSpecified && changesFontMetrics)
}

private fun SpanStyle.changesFontMetrics(): Boolean {
    return fontSize.isSpecified || fontFamily != null || fontWeight != null
}

private fun String.hasComplexGraphemes(): Boolean {
    var offset = 0
    while (offset < length) {
        val codePoint = codePointAt(offset)
        val type = Character.getType(codePoint)
        val isCombiningMark = type == Character.NON_SPACING_MARK.toInt() ||
            type == Character.COMBINING_SPACING_MARK.toInt() ||
            type == Character.ENCLOSING_MARK.toInt()
        if (codePoint > Char.MAX_VALUE.code || codePoint == ZERO_WIDTH_JOINER || isCombiningMark) {
            return true
        }
        offset += Character.charCount(codePoint)
    }
    return false
}

private fun DivText.Truncate.toTextOverflow(): TextOverflow {
    return when (this) {
        DivText.Truncate.NONE -> TextOverflow.Clip
        DivText.Truncate.START -> TextOverflow.StartEllipsis
        DivText.Truncate.END -> TextOverflow.Ellipsis
        DivText.Truncate.MIDDLE -> TextOverflow.MiddleEllipsis
    }
}

private const val ACTIONS_LINK_TAG = "div-action"
private const val ZERO_WIDTH_JOINER = 0x200D
