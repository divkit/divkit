package com.yandex.div.core.text

import com.yandex.div.core.annotations.InternalApi
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

@OptIn(InternalApi::class)
class TextTruncationTest {

    @Test
    fun `finds boundary in small layout window of long text`() {
        val text = "a".repeat(LONG_TEXT_LENGTH)
        val truncation = TextTruncation(text, Locale.US)

        val offset = truncation.findOffset(
            lineStart = 0,
            lineEnd = 4,
            byWord = false,
            fits = { true },
        )

        assertEquals(4, offset)
    }

    @Test
    fun `returns no boundary when ellipsis does not fit`() {
        val truncation = TextTruncation("text", Locale.US)

        val offset = truncation.findOffset(
            lineStart = 0,
            lineEnd = 4,
            byWord = false,
            fits = { false },
        )

        assertNull(offset)
    }

    @Test
    fun `does not search fitting boundary before closest safe line boundary`() {
        val truncation = TextTruncation("text", Locale.US)

        val offset = truncation.findOffset(
            lineStart = 2,
            lineEnd = 4,
            byWord = false,
            fits = { it <= 1 },
        )

        assertNull(offset)
    }

    @Test
    fun `does not split emoji grapheme`() {
        val familyEmoji = "👨‍👩‍👧‍👦"
        val prefix = "prefix "
        val text = "$prefix$familyEmoji suffix"
        val truncation = TextTruncation(text, Locale.US)

        val offset = truncation.findOffset(
            lineStart = 0,
            lineEnd = prefix.length + Character.charCount(familyEmoji.codePointAt(0)),
            byWord = false,
            fits = { true },
        )

        assertEquals(prefix.length, offset)
    }

    @Test
    fun `does not scan boundaries when ellipsis does not fit`() {
        val text = "a".repeat(FALLBACK_PREFIX_LENGTH) + " tail"
        val truncation = TextTruncation(text, Locale.US)
        var fitChecks = 0

        val offset = truncation.findOffset(
            lineStart = FALLBACK_PREFIX_LENGTH,
            lineEnd = text.length,
            byWord = true,
        ) {
            fitChecks++
            false
        }

        assertNull(offset)
        assertEquals(1, fitChecks)
    }

    @Test
    fun `continues search when marker fits but shorter layout candidate does not`() {
        val truncation = TextTruncation("first\nsecond", Locale.US)

        val offset = truncation.findOffset(
            lineStart = 6,
            lineEnd = 12,
            byWord = false,
            fits = { it == 0 || it >= 6 },
        )

        assertEquals(12, offset)
    }

    @Test
    fun `fallback measures only closest safe boundary before current line`() {
        val lineStart = FALLBACK_PREFIX_LENGTH
        val text = "a".repeat(lineStart) + " tail"
        val truncation = TextTruncation(text, Locale.US)
        val measuredFallbackOffsets = mutableListOf<Int>()

        val offset = truncation.findOffset(
            lineStart = lineStart,
            lineEnd = text.length,
            byWord = true,
        ) { candidate ->
            if (candidate in 1..lineStart) {
                measuredFallbackOffsets += candidate
            }
            candidate == 0
        }

        assertNull(offset)
        assertEquals(listOf(lineStart), measuredFallbackOffsets)
    }

    private companion object {
        const val FALLBACK_PREFIX_LENGTH = 10_000
        const val LONG_TEXT_LENGTH = 5_000_000
    }
}
