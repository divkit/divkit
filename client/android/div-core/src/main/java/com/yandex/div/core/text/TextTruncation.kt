package com.yandex.div.core.text

import android.os.Build
import androidx.annotation.RequiresApi
import com.yandex.div.core.annotations.InternalApi
import java.text.BreakIterator
import java.util.Locale

/** Selects logical text boundaries; the renderer owns shaping, spans and the width budget. */
@InternalApi
public class TextTruncation(private val text: String, private val locale: Locale) {

    private val graphemeBoundaries by lazy { boundaryIterator(character = true) }
    private val lineBreakBoundaries by lazy { boundaryIterator(character = false) }

    /**
     * Returns the last safe boundary whose prefix and marker satisfy [fits], or `null` when none fit.
     * [fits] must include replacement content when measuring the prefix together with its marker.
     * For offset `0`, it must report whether the marker itself fits. This check avoids scanning every
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
                if (end > lineStart && graphemeBoundaries.isBoundary(end) && isSafeBoundary(end) && fits(end)) {
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
            if (codePoint != ZERO_WIDTH_SPACE && codePoint != SOFT_HYPHEN && !Character.isWhitespace(codePoint)) {
                break
            }
            end -= Character.charCount(codePoint)
        }
        return end
    }

    private fun boundaryIterator(character: Boolean): TextBoundaryIterator {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val iterator = if (character) {
                android.icu.text.BreakIterator.getCharacterInstance(locale)
            } else {
                android.icu.text.BreakIterator.getLineInstance(locale)
            }
            iterator.setText(text)
            return IcuTextBoundaryIterator(iterator)
        }
        val iterator = if (character) {
            BreakIterator.getCharacterInstance(locale)
        } else {
            BreakIterator.getLineInstance(locale)
        }
        iterator.setText(text)
        return JavaTextBoundaryIterator(iterator)
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
        val type = Character.getType(next)
        if (type == Character.NON_SPACING_MARK.toInt() || type == Character.COMBINING_SPACING_MARK.toInt() ||
            type == Character.ENCLOSING_MARK.toInt() || next == ZERO_WIDTH_JOINER || previous == ZERO_WIDTH_JOINER ||
            next in VARIATION_SELECTORS || next in SUPPLEMENTARY_VARIATION_SELECTORS ||
            next in EMOJI_MODIFIERS || next in TAG_CHARACTERS
        ) {
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
            previousOffset -= Character.charCount(previous)
        }
        return precedingIndicators % 2 != 0
    }

    private interface TextBoundaryIterator {
        fun isBoundary(offset: Int): Boolean
        fun preceding(offset: Int): Int

        companion object {
            const val DONE = -1
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private class IcuTextBoundaryIterator(
        private val iterator: android.icu.text.BreakIterator,
    ) : TextBoundaryIterator {
        override fun isBoundary(offset: Int): Boolean = iterator.isBoundary(offset)

        override fun preceding(offset: Int): Int = iterator.preceding(offset)
    }

    private class JavaTextBoundaryIterator(
        private val iterator: BreakIterator,
    ) : TextBoundaryIterator {
        override fun isBoundary(offset: Int): Boolean = iterator.isBoundary(offset)

        override fun preceding(offset: Int): Int = iterator.preceding(offset)
    }

    private companion object {
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
