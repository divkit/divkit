package com.yandex.div.compose.views.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.yandex.div.compose.font.LineMetrics
import com.yandex.div2.DivTextAlignmentVertical
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val HALF_PX = 0.5f
private const val PARAGRAPH_BREAK = ' '

internal class TextShift(val start: Int, val end: Int, val shiftPx: Int, val font: LineMetrics)

internal class TextLineBounds(val end: Int, val top: Int, val bottom: Int)

internal class LineMetricsText(val text: AnnotatedText, val lines: List<TextLineBounds>)

internal fun AnnotatedString.withParagraphsAtLineFeeds(): AnnotatedString {
    val trailing = text.trailingLineFeed
    val lineFeeds = text.indices.filter { text[it] == '\n' && (it < length - 1 || it == trailing) }
    if (lineFeeds.isEmpty()) return this
    val bounds = (listOf(0) + lineFeeds.map { if (it == trailing) it else it + 1 } + length).distinct()
    return AnnotatedString.Builder(replacingChars(lineFeeds.associateWith { PARAGRAPH_BREAK })).apply {
        bounds.zipWithNext { start, end -> addStyle(ParagraphStyle(), start, end) }
    }.toAnnotatedString()
}

internal fun AnnotatedText.withLineMetrics(
    layout: TextLayoutResult?,
    lineHeightPx: Int?,
    fontSizePx: Float,
    hyphens: Hyphens?,
    maxLines: Int,
    measureTail: (AnnotatedString) -> TextLayoutResult,
): LineMetricsText {
    val shifts = metrics.mapNotNull { range ->
        val font = range.font?.takeIf { range.baselineOffsetPx != 0 } ?: return@mapNotNull null
        TextShift(range.start, range.end, range.baselineOffsetPx, font)
    }
    if (layout == null || text.isEmpty()) {
        val styled = withLineStyles(shifts, emptyList(), emptyList(), emptyList())
        return LineMetricsText(styled, emptyList())
    }

    fun TextLayoutResult.lineMetrics(line: Int): IntRange {
        val baseline = getLineBaseline(line)
        return (getLineTop(line) - baseline).roundToInt()..(getLineBottom(line) - baseline).roundToInt()
    }

    val lines = layout.lineGroups(text.text, hyphens, maxLines)
    val boxes = lines.mapIndexed { index, line ->
        val natural = if (layout.lineCount > maxLines && index == lines.lastIndex) {
            measureTail(text.subSequence(line.start, line.end)).lineMetrics(0)
        } else {
            layout.lineMetrics(line.firstLine)
        }
        line.box(natural, metrics, lineHeightPx)
    }
    val paragraphs = if (boxes.none { it.lineHeightPx != null || it.topOffsetPx > 0 }) {
        emptyList()
    } else {
        layout.paragraphs(text.text, lines, boxes, fontSizePx, maxLines)
    }
    val bounds = lines.zip(boxes) { line, box -> TextLineBounds(line.end, -box.ascentPx, box.descentPx) }
    return LineMetricsText(withLineStyles(shifts, lines, boxes, paragraphs), bounds)
}

private fun TextLayoutResult.paragraphs(
    text: String,
    lines: List<LineGroup>,
    boxes: List<LineBox>,
    fontSizePx: Float,
    maxLines: Int,
): List<AnnotatedString.Range<ParagraphStyle>> {
    val result = mutableListOf<AnnotatedString.Range<ParagraphStyle>>()
    lines.zip(boxes) { line, box ->
        val style = box.paragraphStyle(fontSizePx, getParagraphDirection(line.start))
        val last = result.lastOrNull()
        if (last?.item == style && text[line.start - 1] != '\n') {
            result[result.lastIndex] = last.copy(end = line.end)
        } else {
            result += AnnotatedString.Range(style, line.start, line.end)
        }
    }
    val last = result.last()
    val endsWithLineFeed = text.endsWith('\n') && last.start < text.length - 1
    val hasTrailingLine = endsWithLineFeed && lineCount <= maxLines && last.item.lineHeight.isSpecified
    result[result.lastIndex] = last.copy(end = if (hasTrailingLine) text.length - 1 else text.length)
    return result
}

private fun AnnotatedText.withLineStyles(
    shifts: List<TextShift>,
    lines: List<LineGroup>,
    boxes: List<LineBox>,
    paragraphs: List<AnnotatedString.Range<ParagraphStyle>>,
): AnnotatedText {
    val lineShifts = shifts + boxes.flatMap { it.shifts }
    val trailingLine = paragraphs.lastOrNull()?.end?.takeIf { it < text.length }
    val lineBreaks = (paragraphs.drop(1).map { it.start - 1 } + listOfNotNull(trailingLine))
        .mapNotNull { offset ->
            when (text.text[offset]) {
                '\n' -> offset to PARAGRAPH_BREAK
                SOFT_HYPHEN -> offset to VISIBLE_HYPHEN
                else -> null
            }
        }
        .toMap()
    val styled = AnnotatedString.Builder(text.replacingChars(lineBreaks)).apply {
        lineShifts.forEach { addStyle(it.font.shiftStyle(it.shiftPx), it.start, it.end) }
        paragraphs.forEach { addStyle(it.item, it.start, it.end) }
        paragraphs.dropLast(1).forEach {
            val line = text.text.substring(it.start, it.end)
            val start = it.start + max(1, if (line.endsWith('\n')) line.length - 1 else line.trimEnd().length)
            if (start < it.end) addStyle(SpanStyle(fontSize = 0.sp), start, it.end)
        }
    }.toAnnotatedString()
    val boxedDecorations = decorations.map { decoration ->
        if (decoration.hasFragmentBoxes) {
            decoration.withFragmentBoxes(lines, boxes, shifts, fonts)
        } else {
            decoration.withGlyphBoxes(lines, lineShifts)
        }
    }
    return copy(text = styled, decorations = boxedDecorations)
}

private fun TextRangeDecoration.withGlyphBoxes(lines: List<LineGroup>, shifts: List<TextShift>): TextRangeDecoration {
    if (!followsGlyphs) return this
    val parts = if (lines.isEmpty()) listOf(start to end) else lines.map { max(start, it.start) to min(end, it.end) }
    val boxes = parts.filter { (from, to) -> from < to }.mapNotNull { (from, to) ->
        val covering = shifts.filter { it.start < to && it.end > from }
        if (covering.sumOf { min(it.end, to) - max(it.start, from) } < to - from) return@mapNotNull null
        GlyphBox(
            start = from,
            end = to,
            ascentPx = covering.maxOf { it.font.ascentPx + it.shiftPx },
            descentPx = covering.maxOf { it.font.descentPx - it.shiftPx },
        )
    }
    return if (boxes.isEmpty()) this else copy(glyphBoxes = boxes)
}

internal class LineGroup(val start: Int, val end: Int, val firstLine: Int)

private val String.trailingLineFeed: Int?
    get() = (length - 1).takeIf { length > 1 && this[it] == '\n' && this[it - 1] != '\n' }

private fun TextLayoutResult.lineGroups(text: String, hyphens: Hyphens?, maxLines: Int): List<LineGroup> {
    val trailing = text.trailingLineFeed ?: text.length
    val starts = (0 until minOf(lineCount, maxLines)).filter { line ->
        val start = getLineStart(line)
        val previous = text.getOrNull(start - 1)
        line == 0 || start < trailing &&
            (hyphens != Hyphens.Auto || previous == SOFT_HYPHEN || previous?.isWhitespace() == true)
    }
    val visibleEnd = if (lineCount > maxLines) getLineEnd(maxLines - 1) else text.length
    return starts.mapIndexed { index, line ->
        val end = starts.getOrNull(index + 1)?.let(::getLineStart) ?: visibleEnd
        LineGroup(getLineStart(line), end, line)
    }
}

internal class LineBox(
    val lineHeightPx: Int?,
    val topOffsetPx: Int,
    val ascentPx: Int,
    val descentPx: Int,
    val frameAscentPx: Int,
    val frameDescentPx: Int,
    val contentAscentPx: Int,
    val contentDescentPx: Int,
    val shifts: List<TextShift>,
)

private fun LineGroup.box(
    natural: IntRange,
    metrics: List<TextRangeMetrics>,
    textLineHeightPx: Int?,
): LineBox {
    val naturalAscent = -natural.first
    val naturalDescent = natural.last
    val ranges = metrics.filter { it.start < end && it.end > start }
    val lineHeight = lineHeight(ranges, textLineHeightPx)
    val topOffset = ranges.filter { it.start >= start }.maxOfOrNull { it.topOffsetPx } ?: 0

    var contentAscent = naturalAscent
    var contentDescent = naturalDescent
    ranges.forEach { range ->
        val font = range.font ?: return@forEach
        if (range.baselineOffsetPx != 0) {
            contentAscent = max(contentAscent, font.ascentPx + range.baselineOffsetPx)
            contentDescent = max(contentDescent, font.descentPx - range.baselineOffsetPx)
        }
    }

    val ascent: Int
    val descent: Int
    val frameAscent: Int
    val frameDescent: Int
    if (lineHeight == null) {
        ascent = contentAscent
        descent = contentDescent
        frameAscent = naturalAscent
        frameDescent = naturalDescent
    } else {
        val extra = lineHeight - naturalAscent - naturalDescent
        if (extra >= 0) {
            descent = naturalDescent + extra / 2
            ascent = lineHeight - descent
        } else {
            ascent = naturalAscent + extra / 2
            descent = lineHeight - ascent
        }
        frameAscent = ascent
        frameDescent = descent
    }

    val shifts = ranges.mapNotNull { range ->
        val alignment = range.alignment ?: return@mapNotNull null
        val font = range.font ?: return@mapNotNull null
        val shift = alignment.shift(font, ascent, descent)
            .takeIf { it != 0 } ?: return@mapNotNull null
        contentAscent = max(contentAscent, font.ascentPx + shift)
        contentDescent = max(contentDescent, font.descentPx - shift)
        TextShift(max(range.start, start), min(range.end, end), shift, font)
    }
    return LineBox(
        lineHeightPx = lineHeight,
        topOffsetPx = topOffset,
        ascentPx = ascent + topOffset,
        descentPx = descent,
        frameAscentPx = frameAscent,
        frameDescentPx = frameDescent,
        contentAscentPx = contentAscent,
        contentDescentPx = contentDescent,
        shifts = shifts,
    )
}

private fun LineGroup.lineHeight(ranges: List<TextRangeMetrics>, textLineHeightPx: Int?): Int? {
    val rangeLineHeights = ranges.filter { it.lineHeightPx != null }
    val coveredLength = rangeLineHeights.sumOf { min(it.end, end) - max(it.start, start) }
    val textLineHeight = textLineHeightPx?.takeIf { coveredLength < end - start }
    return (rangeLineHeights.mapNotNull { it.lineHeightPx } + listOfNotNull(textLineHeight))
        .maxOrNull()
}

private fun DivTextAlignmentVertical.shift(font: LineMetrics, lineAscent: Int, lineDescent: Int): Int {
    return when (this) {
        DivTextAlignmentVertical.TOP -> lineAscent - font.ascentPx
        DivTextAlignmentVertical.CENTER ->
            (font.descentPx - font.ascentPx) / 2 - (lineDescent - lineAscent) / 2
        DivTextAlignmentVertical.BOTTOM -> font.descentPx - lineDescent
        DivTextAlignmentVertical.BASELINE -> 0
    }
}

private fun LineMetrics.shiftStyle(shiftPx: Int): SpanStyle {
    return SpanStyle(baselineShift = BaselineShift((shiftPx + HALF_PX) / fontAscentPx))
}

private fun LineBox.paragraphStyle(
    fontSizePx: Float,
    direction: ResolvedTextDirection,
): ParagraphStyle {
    val textDirection = when (direction) {
        ResolvedTextDirection.Ltr -> TextDirection.Ltr
        ResolvedTextDirection.Rtl -> TextDirection.Rtl
    }
    if (lineHeightPx == null && topOffsetPx <= 0) {
        return ParagraphStyle(textDirection = textDirection)
    }
    val height = ascentPx + descentPx
    val diff = height - contentAscentPx - contentDescentPx
    val descentDiff = (descentPx - contentDescentPx).coerceIn(min(diff, 0), max(diff, 0))
    val topRatio = when {
        diff > 0 -> 1f - (descentDiff - HALF_PX) / diff
        diff < 0 -> (descentDiff - HALF_PX) / diff
        else -> 1f
    }.coerceAtMost(1f)
    val alignment = LineHeightStyle.Alignment(topRatio)
    val lineHeightStyle = if (diff < 0) {
        LineHeightStyle(alignment, LineHeightStyle.Trim.Both, LineHeightStyle.Mode.Tight)
    } else {
        LineHeightStyle(alignment, LineHeightStyle.Trim.None, LineHeightStyle.Mode.Fixed)
    }
    return ParagraphStyle(
        textDirection = textDirection,
        lineHeight = exactLineHeight(height.toFloat(), fontSizePx),
        lineHeightStyle = lineHeightStyle,
    )
}
