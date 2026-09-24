package com.yandex.div.compose.views.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import com.yandex.div.core.text.TextTruncation
import com.yandex.div2.DivText
import java.util.Locale

/**
 * Resolves end truncation for a measured Compose text layout. It applies the requested word or
 * grapheme boundary and keeps inline placeholders whole. A custom ellipsis is measured together
 * with the retained text; a standard ellipsis may reuse native rendering when its cut is valid.
 *
 * [availableWidth] and [maxHeight] are layout constraints from the rendered text. The result
 * either contains the text to render or requests native ellipsis rendering.
 */
internal fun ellipsizeTextEnd(
    measurer: TextMeasurer,
    text: AnnotatedText,
    ellipsis: AnnotatedText,
    style: TextStyle,
    maxLines: Int,
    availableWidth: Int,
    policy: DivText.TruncatePolicy,
    inlineContent: Map<String, InlineTextContent>,
    maxHeight: Int,
): TextEllipsisResult {
    return EndTextEllipsizer(
        measurer = measurer,
        style = style,
        maxLines = maxLines,
        availableWidth = availableWidth,
        maxHeight = maxHeight,
        inlineContent = inlineContent,
    ).ellipsize(
        text = text,
        ellipsis = ellipsis,
        policy = policy,
    )
}

private class EndTextEllipsizer(
    private val measurer: TextMeasurer,
    private val style: TextStyle,
    private val maxLines: Int,
    private val availableWidth: Int,
    private val maxHeight: Int,
    private val inlineContent: Map<String, InlineTextContent>,
) {

    fun ellipsize(
        text: AnnotatedText,
        ellipsis: AnnotatedText,
        policy: DivText.TruncatePolicy,
    ): TextEllipsisResult {
        if (text.text.isEmpty()) {
            return TextEllipsisResult.Render(text)
        }
        if (availableWidth <= 0) {
            return TextEllipsisResult.Render(AnnotatedText(AnnotatedString(""), emptyList()))
        }

        val placeholders = text.text.placeholders(inlineContent)
        if (policy == DivText.TruncatePolicy.GRAPHEME && ellipsis.isStandardEllipsis()) {
            return ellipsizeAtNativeBoundary(
                text = text,
                ellipsis = ellipsis,
                placeholders = placeholders,
            )
        }

        return ellipsizeManually(
            text = text,
            ellipsis = ellipsis,
            policy = policy,
            placeholders = placeholders,
        )
    }

    private fun ellipsizeManually(
        text: AnnotatedText,
        ellipsis: AnnotatedText,
        policy: DivText.TruncatePolicy,
        placeholders: List<AnnotatedString.Range<Placeholder>>,
    ): TextEllipsisResult {
        val sourceLayout = measureSourceText(text, placeholders)
        val visibleLineCount = calculateVisibleLineCount(
            text = text.text,
            placeholders = placeholders,
        )
        val textFits = sourceLayout.lineCount <= visibleLineCount && !sourceLayout.hasVisualOverflow
        if (textFits) {
            return TextEllipsisResult.Render(text)
        }

        val visibleBounds = sourceLayout.calculateVisibleTextBounds(
            text = text.text,
            visibleLineCount = visibleLineCount,
        ) ?: return TextEllipsisResult.Render(AnnotatedText(AnnotatedString(""), emptyList()))
        val truncationOffset = findTruncationOffset(
            text = text,
            ellipsis = ellipsis,
            sourceLayout = sourceLayout,
            visibleBounds = visibleBounds,
            policy = policy,
            placeholders = placeholders,
        ) ?: return TextEllipsisResult.Render(AnnotatedText(AnnotatedString(""), emptyList()))

        return TextEllipsisResult.Render(
            buildTruncatedText(
                text = text,
                endOffset = truncationOffset,
                ellipsis = ellipsis,
                sourceLayout = sourceLayout,
                lastVisibleLine = visibleBounds.lastLine,
                softBreaks = visibleBounds.softBreaks,
            )
        )
    }

    private fun measureSourceText(
        text: AnnotatedText,
        placeholders: List<AnnotatedString.Range<Placeholder>>,
    ): TextLayoutResult {
        return measurer.measure(
            text = text.text,
            style = style,
            placeholders = placeholders,
            constraints = Constraints(maxWidth = availableWidth),
        )
    }

    private fun ellipsizeAtNativeBoundary(
        text: AnnotatedText,
        ellipsis: AnnotatedText,
        placeholders: List<AnnotatedString.Range<Placeholder>>,
    ): TextEllipsisResult {
        val sourceLayout = measurer.measure(
            text = text.text,
            style = style,
            placeholders = placeholders,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            constraints = Constraints(
                maxWidth = availableWidth,
                maxHeight = maxHeight,
            ),
        )
        if (!sourceLayout.hasVisualOverflow) {
            return TextEllipsisResult.UseNativeRendering
        }
        val lastVisibleLine = lastVisibleLineIndex(
            visibleLineCount = sourceLayout.lineCount,
            lineCount = sourceLayout.lineCount,
        ) ?: return TextEllipsisResult.Render(AnnotatedText(AnnotatedString(""), emptyList()))
        val lineStart = sourceLayout.getLineStart(lastVisibleLine)
        val nativeOffset = sourceLayout.getLineEnd(lastVisibleLine, visibleEnd = true)
        val safeOffset = TextTruncation(
            text = text.text.text,
            locale = style.resolveLocale(),
        ).findOffset(
            lineStart = lineStart,
            lineEnd = nativeOffset,
            byWord = false,
            fits = { offset -> !placeholders.splitsPlaceholder(offset) },
        ) ?: return TextEllipsisResult.Render(AnnotatedText(AnnotatedString(""), emptyList()))
        if (safeOffset == nativeOffset) {
            return TextEllipsisResult.UseNativeRendering
        }

        return TextEllipsisResult.Render(
            text.truncatedTo(
                length = safeOffset,
                ellipsis = ellipsis,
                softBreaks = sourceLayout.findSoftBreaks(
                    text = text.text,
                    lastLine = lastVisibleLine,
                ),
            )
        )
    }

    private fun findTruncationOffset(
        text: AnnotatedText,
        ellipsis: AnnotatedText,
        sourceLayout: TextLayoutResult,
        visibleBounds: VisibleTextBounds,
        policy: DivText.TruncatePolicy,
        placeholders: List<AnnotatedString.Range<Placeholder>>,
    ): Int? {
        val truncation = TextTruncation(
            text = text.text.text,
            locale = style.resolveLocale(),
        )
        return truncation.findOffset(
            lineStart = visibleBounds.lineStart,
            lineEnd = visibleBounds.lineEnd,
            byWord = policy == DivText.TruncatePolicy.WORD,
        ) { offset ->
            val splitsPlaceholder = placeholders.splitsPlaceholder(offset)
            if (splitsPlaceholder) {
                false
            } else if (offset == 0) {
                doesEllipsisFit(
                    ellipsis = ellipsis,
                    sourceLayout = sourceLayout,
                )
            } else {
                doesTruncatedTextFit(
                    text = text,
                    endOffset = offset,
                    ellipsis = ellipsis,
                    sourceLayout = sourceLayout,
                    lastVisibleLine = visibleBounds.lastLine,
                    softBreaks = visibleBounds.softBreaks,
                )
            }
        }
    }

    private fun doesEllipsisFit(
        ellipsis: AnnotatedText,
        sourceLayout: TextLayoutResult,
    ): Boolean {
        val ellipsisLayout = measurer.measure(
            text = ellipsis.text,
            style = style,
            placeholders = ellipsis.text.placeholders(inlineContent),
            maxLines = 1,
            constraints = sourceLayout.layoutInput.constraints,
        )
        return !ellipsisLayout.hasVisualOverflow
    }

    private fun calculateVisibleLineCount(
        text: AnnotatedString,
        placeholders: List<AnnotatedString.Range<Placeholder>>,
    ): Int {
        if (maxHeight <= 0) {
            return 0
        }
        if (maxHeight == Constraints.Infinity) {
            return maxLines
        }

        // BasicText's standard ellipsis responds to the height constraint. Preserve its decision
        // about the last visible line before selecting a different logical truncation boundary.
        return measurer.measure(
            text = text,
            style = style,
            placeholders = placeholders,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            constraints = Constraints(
                maxWidth = availableWidth,
                maxHeight = maxHeight,
            ),
        ).lineCount
    }

    private fun doesTruncatedTextFit(
        text: AnnotatedText,
        endOffset: Int,
        ellipsis: AnnotatedText,
        sourceLayout: TextLayoutResult,
        lastVisibleLine: Int,
        softBreaks: List<SoftBreak>,
    ): Boolean {
        val candidate = buildTruncatedText(
            text = text,
            endOffset = endOffset,
            ellipsis = ellipsis,
            sourceLayout = sourceLayout,
            lastVisibleLine = lastVisibleLine,
            softBreaks = softBreaks,
        ).text
        val candidateLayout = measurer.measure(
            text = candidate,
            style = style,
            placeholders = candidate.placeholders(inlineContent),
            constraints = sourceLayout.layoutInput.constraints,
        )
        return candidateLayout.lineCount == lastVisibleLine + 1 && !candidateLayout.hasVisualOverflow
    }

    private fun buildTruncatedText(
        text: AnnotatedText,
        endOffset: Int,
        ellipsis: AnnotatedText,
        sourceLayout: TextLayoutResult,
        lastVisibleLine: Int,
        softBreaks: List<SoftBreak>,
    ): AnnotatedText {
        val candidate = text.truncatedTo(
            length = endOffset,
            ellipsis = ellipsis,
        )
        if (lastVisibleLine == 0) {
            return candidate
        }

        val candidateLayout = measurer.measure(
            text = candidate.text,
            style = style,
            placeholders = candidate.text.placeholders(inlineContent),
            constraints = sourceLayout.layoutInput.constraints,
        )
        val retainsSourceLineStarts = candidateLayout.lineCount > lastVisibleLine &&
            (1..lastVisibleLine).all { line ->
                candidateLayout.getLineStart(line) == sourceLayout.getLineStart(line)
            }
        if (retainsSourceLineStarts) {
            return candidate
        }

        return text.truncatedTo(
            length = endOffset,
            ellipsis = ellipsis,
            softBreaks = softBreaks,
        )
    }
}

private fun TextLayoutResult.calculateVisibleTextBounds(
    text: AnnotatedString,
    visibleLineCount: Int,
): VisibleTextBounds? {
    val lastLine = lastVisibleLineIndex(visibleLineCount, lineCount) ?: return null
    val lineStart = getLineStart(lastLine)
    val nextLineEnd = getLineEnd(
        lineIndex = minOf(lastLine + 1, lineCount - 1),
        visibleEnd = true,
    )
    val paragraphEnd = text.text.indexOf('\n', lineStart)
        .takeIf { offset -> offset >= 0 }
        ?: text.length
    return VisibleTextBounds(
        lastLine = lastLine,
        lineStart = lineStart,
        lineEnd = minOf(nextLineEnd, paragraphEnd),
        softBreaks = findSoftBreaks(
            text = text,
            lastLine = lastLine,
        ),
    )
}

internal fun lastVisibleLineIndex(visibleLineCount: Int, lineCount: Int): Int? {
    return minOf(visibleLineCount, lineCount)
        .takeIf { visibleLines -> visibleLines > 0 }
        ?.minus(1)
}

internal fun List<AnnotatedString.Range<Placeholder>>.splitsPlaceholder(offset: Int): Boolean {
    return any { placeholder ->
        placeholder.start < offset && placeholder.end > offset
    }
}

private fun TextStyle.resolveLocale(): Locale {
    val primaryLocale = localeList?.get(0) ?: return Locale.getDefault()
    return Locale.forLanguageTag(primaryLocale.toLanguageTag())
}

private fun AnnotatedText.isStandardEllipsis(): Boolean {
    return text.text == DEFAULT_ELLIPSIS && !text.hasAnnotations() && decorations.isEmpty()
}

/**
 * Returns soft wraps before [lastLine] that must be frozen when the final word is shortened.
 * [text] is the full laid-out text and is used to exclude explicit newline boundaries.
 */
private fun TextLayoutResult.findSoftBreaks(
    text: AnnotatedString,
    lastLine: Int,
): List<SoftBreak> {
    return (1..lastLine)
        .map(::getLineStart)
        .filter { offset -> text[offset - 1] != '\n' }
        .map { offset ->
            val directionMark = if (getParagraphDirection(offset) == ResolvedTextDirection.Rtl) {
                RIGHT_TO_LEFT_MARK
            } else {
                LEFT_TO_RIGHT_MARK
            }
            SoftBreak(
                offset = offset,
                directionMark = directionMark,
            )
        }
}

internal fun AnnotatedString.placeholders(
    inlineContent: Map<String, InlineTextContent>,
): List<AnnotatedString.Range<Placeholder>> {
    return getStringAnnotations(0, length)
        .mapNotNull { annotation ->
            val inlineTextContent = inlineContent[annotation.item] ?: return@mapNotNull null
            AnnotatedString.Range(
                item = inlineTextContent.placeholder,
                start = annotation.start,
                end = annotation.end,
            )
        }
}

private fun AnnotatedText.truncatedTo(
    length: Int,
    ellipsis: AnnotatedText,
    softBreaks: List<SoftBreak> = emptyList(),
): AnnotatedText {
    val offsets = TruncatedTextOffsetMapper(length, softBreaks)
    return AnnotatedText(
        text = buildTruncatedAnnotatedString(length, ellipsis, softBreaks),
        decorations = mapTruncatedDecorations(length, ellipsis, offsets),
    )
}

private fun AnnotatedText.buildTruncatedAnnotatedString(
    length: Int,
    ellipsis: AnnotatedText,
    softBreaks: List<SoftBreak>,
): AnnotatedString {
    return buildAnnotatedString {
        var sourceStart = 0
        for (softBreak in softBreaks) {
            val sourceEnd = minOf(softBreak.offset, length)
            val sourceSegment = text.subSequence(sourceStart, sourceEnd)
            if (sourceSegment.lastOrNull() == SOFT_HYPHEN) {
                // An explicit newline no longer draws a discretionary hyphen. Retain its glyph
                // and all annotations at the same offset when freezing that original break.
                val annotations = mutableListOf<AnnotatedString.Range<out AnnotatedString.Annotation>>()
                sourceSegment.mapAnnotations { annotation ->
                    annotations += annotation
                    annotation
                }
                append(
                    AnnotatedString(
                        text = sourceSegment.text.dropLast(1) + VISIBLE_HYPHEN,
                        annotations = annotations,
                    )
                )
            } else {
                append(sourceSegment)
            }
            append("\n")
            // A frozen wrap must retain the original paragraph's base direction.
            append(softBreak.directionMark)
            sourceStart = sourceEnd
        }
        append(text.subSequence(sourceStart, length))
        append(ellipsis.text)
    }
}

private fun AnnotatedText.mapTruncatedDecorations(
    length: Int,
    ellipsis: AnnotatedText,
    offsets: TruncatedTextOffsetMapper,
): List<TextRangeDecoration> {
    val result = ArrayList<TextRangeDecoration>(decorations.size + ellipsis.decorations.size)
    for (decoration in decorations) {
        val decorationEnd = decoration.end.coerceAtMost(length)
        if (decoration.start < decorationEnd) {
            result += decoration.copy(
                start = offsets.mapStart(decoration.start),
                end = offsets.mapEnd(decorationEnd),
            )
        }
    }
    for (decoration in ellipsis.decorations) {
        result += decoration.copy(
            start = offsets.mapEllipsis(decoration.start),
            end = offsets.mapEllipsis(decoration.end),
        )
    }
    return result
}

internal const val SOFT_HYPHEN = '\u00AD'
internal const val DEFAULT_ELLIPSIS = "\u2026"

private const val VISIBLE_HYPHEN = '\u2010'
private const val LEFT_TO_RIGHT_MARK = '\u200E'
private const val RIGHT_TO_LEFT_MARK = '\u200F'

private data class VisibleTextBounds(
    val lastLine: Int,
    val lineStart: Int,
    val lineEnd: Int,
    val softBreaks: List<SoftBreak>,
)

private data class SoftBreak(
    val offset: Int,
    val directionMark: Char,
)

private class TruncatedTextOffsetMapper(
    private val sourceLength: Int,
    private val softBreaks: List<SoftBreak>,
) {
    fun mapStart(offset: Int): Int {
        return offset + INSERTED_BREAK_LENGTH * softBreaks.count { it.offset <= offset }
    }

    fun mapEnd(offset: Int): Int {
        return offset + INSERTED_BREAK_LENGTH * softBreaks.count { it.offset < offset }
    }

    fun mapEllipsis(offset: Int): Int {
        return sourceLength + INSERTED_BREAK_LENGTH * softBreaks.size + offset
    }

    private companion object {
        const val INSERTED_BREAK_LENGTH = 2
    }
}
