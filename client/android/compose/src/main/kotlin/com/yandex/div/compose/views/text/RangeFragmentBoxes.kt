package com.yandex.div.compose.views.text

import kotlin.math.max
import kotlin.math.min

internal fun AnnotatedText.withFontBoxes(): AnnotatedText {
    val boxed = decorations.map { if (it.hasFragmentBoxes) it.copy(glyphBoxes = fonts) else it }
    return copy(decorations = boxed)
}

internal fun TextRangeDecoration.withFragmentBoxes(
    lines: List<LineGroup>,
    boxes: List<LineBox>,
    shifts: List<TextShift>,
    fonts: List<GlyphBox>,
): TextRangeDecoration {
    if (lines.isEmpty()) return copy(glyphBoxes = fonts)
    val fragments = lines.zip(boxes).flatMap { (line, box) ->
        lineFragments(line, box, shifts, fonts)
    }
    return copy(glyphBoxes = fragments)
}

private fun TextRangeDecoration.lineFragments(
    line: LineGroup,
    box: LineBox,
    shifts: List<TextShift>,
    fonts: List<GlyphBox>,
): List<GlyphBox> {
    val from = max(start, line.start)
    val to = min(end, line.end)
    val fontBounds = fonts.flatMap { listOf(it.start, it.end) }
    val shiftBounds = (shifts + box.shifts).flatMap { listOf(it.start, it.end) }
    val bounds = (fontBounds + shiftBounds + from + to)
        .filter { it in from..to }
        .distinct()
        .sorted()
    return bounds.zipWithNext { fragmentStart, fragmentEnd ->
        val font = fonts.find { fragmentStart in it.start until it.end }
        val baselineShift = shifts.shiftAt(fragmentStart)
        val alignmentShift = box.shifts.shiftAt(fragmentStart)
        val shift = if (font == null) baselineShift else baselineShift + alignmentShift
        val (ascent, descent) = extendedToLineHeight(
            ascent = font?.ascentPx ?: box.frameAscentPx,
            descent = font?.descentPx ?: box.frameDescentPx,
        )
        GlyphBox(
            start = fragmentStart,
            end = fragmentEnd,
            ascentPx = ascent + shift,
            descentPx = descent - shift,
        )
    }
}

private fun List<TextShift>.shiftAt(offset: Int): Int {
    return find { offset in it.start until it.end }?.shiftPx ?: 0
}

private fun TextRangeDecoration.extendedToLineHeight(ascent: Int, descent: Int): Pair<Int, Int> {
    val lineHeight = lineHeightPx ?: return ascent to descent
    val extra = lineHeight - ascent - descent
    val extraTop = extra / 2
    return ascent + extraTop to descent + extra - extraTop
}
