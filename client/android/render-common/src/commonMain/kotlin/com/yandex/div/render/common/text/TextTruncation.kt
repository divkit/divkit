package com.yandex.div.render.common.text

import com.yandex.div.core.annotations.InternalApi

/**
 * Selects logical text boundaries; the renderer owns shaping, spans and the width budget.
 */
@InternalApi
public class TextTruncation internal constructor(
    private val text: String,
    boundaryIterator: (character: Boolean) -> TextBoundaryIterator,
) {

    private val graphemeBoundaries by lazy { boundaryIterator(true) }
    private val lineBreakBoundaries by lazy { boundaryIterator(false) }

    /**
     * Returns the last safe boundary whose prefix and marker satisfy [fits], or `null` if none fit.
     * [fits] must include replacement content when measuring the prefix together with its marker.
     * For offset `0`, it must report whether the marker fits. This check avoids scanning every
     * boundary when the marker is already too large; other offsets may additionally enforce layout
     * constraints such as keeping the marker on the last visible line.
     */
    public fun findOffset(
        lineStart: Int,
        lineEnd: Int,
        byWord: Boolean,
        fits: (Int) -> Boolean,
    ): Int? {
        if (!fits(0)) {
            return null
        }

        if (byWord) {
            findBoundaryBackward(lineBreakBoundaries, lineStart, lineEnd) { boundary ->
                val end = trimLineEnd(boundary, lineStart)
                if (end > lineStart && graphemeBoundaries.isBoundary(end) &&
                    isSafeBoundary(end) && fits(end)
                ) {
                    end
                } else {
                    null
                }
            }?.let { return it }
        }

        findBoundaryBackward(graphemeBoundaries, lineStart, lineEnd) { boundary ->
            if (isSafeBoundary(boundary) && fits(boundary)) {
                boundary
            } else {
                null
            }
        }?.let { return it }

        // Old platform layouts can start a line inside a newer emoji cluster. Only test the
        // closest safe boundary: this fallback repairs that layout offset, it must not turn into
        // an unbounded sequence of expensive layout measurements over the preceding text.
        val boundaryBeforeLine = findBoundaryBackward(
            iterator = graphemeBoundaries,
            startExclusive = -1,
            endInclusive = lineStart,
        ) { boundary ->
            boundary.takeIf(::isSafeBoundary)
        } ?: return null
        return boundaryBeforeLine.takeIf(fits)
    }

    private fun trimLineEnd(boundary: Int, lineStart: Int): Int {
        var end = boundary
        while (end > lineStart) {
            val codePoint = text.codePointBefore(end)
            if (codePoint != ZERO_WIDTH_SPACE && codePoint != SOFT_HYPHEN &&
                !isTrimmableWhitespace(codePoint)
            ) {
                break
            }
            end -= codePointSize(codePoint)
        }
        return end
    }

    private fun findBoundaryBackward(
        iterator: TextBoundaryIterator,
        startExclusive: Int,
        endInclusive: Int,
        select: (Int) -> Int?,
    ): Int? {
        var boundary = if (iterator.isBoundary(endInclusive)) {
            endInclusive
        } else {
            iterator.preceding(endInclusive)
        }
        while (boundary != TextBoundaryIterator.DONE && boundary > startExclusive) {
            select(boundary)?.let { return it }
            boundary = iterator.preceding(boundary)
        }
        return null
    }

    // Older Android Unicode tables split some emoji sequences. Never cut those sequences even
    // when the system iterator does not know the newer grapheme rule or emoji code point yet.
    private fun isSafeBoundary(offset: Int): Boolean {
        if (offset == 0 || offset == text.length) {
            return true
        }
        if (text[offset].isLowSurrogate() && text[offset - 1].isHighSurrogate()) {
            return false
        }
        val next = text.codePointAt(offset)
        val previous = text.codePointBefore(offset)
        if (previous == ZERO_WIDTH_JOINER || isGraphemeExtension(next)) {
            return false
        }
        return !isRegionalIndicatorContinuation(offset, next)
    }

    private fun isRegionalIndicatorContinuation(offset: Int, next: Int): Boolean {
        if (next !in REGIONAL_INDICATORS) {
            return false
        }
        var previousOffset = offset
        var precedingIndicators = 0
        while (previousOffset > 0) {
            val previous = text.codePointBefore(previousOffset)
            if (previous !in REGIONAL_INDICATORS) {
                break
            }
            precedingIndicators++
            previousOffset -= codePointSize(previous)
        }
        return precedingIndicators % 2 != 0
    }

    private fun isGraphemeExtension(codePoint: Int): Boolean = when (codePoint) {
        ZERO_WIDTH_JOINER,
        in VARIATION_SELECTORS,
        in SUPPLEMENTARY_VARIATION_SELECTORS,
        in EMOJI_MODIFIERS,
        in TAG_CHARACTERS -> true
        else -> isCombiningMark(codePoint)
    }

    private fun String.codePointAt(offset: Int): Int {
        val first = this[offset]
        return if (first.isHighSurrogate() && offset + 1 < length &&
            this[offset + 1].isLowSurrogate()
        ) {
            combineSurrogates(first, this[offset + 1])
        } else {
            first.code
        }
    }

    private fun String.codePointBefore(offset: Int): Int {
        val second = this[offset - 1]
        return if (second.isLowSurrogate() && offset >= 2 &&
            this[offset - 2].isHighSurrogate()
        ) {
            combineSurrogates(this[offset - 2], second)
        } else {
            second.code
        }
    }

    private fun combineSurrogates(high: Char, low: Char): Int =
        MIN_SUPPLEMENTARY_CODE_POINT +
            (high.code - Char.MIN_HIGH_SURROGATE.code) * SURROGATE_RANGE_SIZE +
            (low.code - Char.MIN_LOW_SURROGATE.code)

    private fun codePointSize(codePoint: Int): Int =
        if (codePoint >= MIN_SUPPLEMENTARY_CODE_POINT) 2 else 1

    private companion object {
        const val MIN_SUPPLEMENTARY_CODE_POINT = 0x10000
        const val SURROGATE_RANGE_SIZE = 0x400

        const val ZERO_WIDTH_SPACE = 0x200B
        const val SOFT_HYPHEN = 0x00AD
        const val ZERO_WIDTH_JOINER = 0x200D

        // Variation selectors request a specific glyph presentation for the preceding code point.
        // Both BMP and supplementary ranges therefore belong to the same grapheme as that point.
        val VARIATION_SELECTORS = 0xFE00..0xFE0F
        val SUPPLEMENTARY_VARIATION_SELECTORS = 0xE0100..0xE01EF
        val EMOJI_MODIFIERS = 0x1F3FB..0x1F3FF
        val TAG_CHARACTERS = 0xE0020..0xE007F
        val REGIONAL_INDICATORS = 0x1F1E6..0x1F1FF
    }
}
