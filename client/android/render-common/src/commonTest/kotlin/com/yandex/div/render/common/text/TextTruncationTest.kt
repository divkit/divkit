package com.yandex.div.render.common.text

import com.yandex.div.core.annotations.InternalApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(InternalApi::class)
class TextTruncationTest {

    @Test
    fun `does not request boundaries when marker does not fit`() {
        val truncation = TextTruncation("text") { error("Boundaries must stay lazy") }
        val measured = mutableListOf<Int>()

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 4, byWord = true) {
            measured += it
            false
        }

        assertNull(offset)
        assertEquals(listOf(0), measured)
    }

    @Test
    fun `searches only boundaries in the layout window`() {
        val truncation = truncation("abcdefgh", graphemeOffsets = listOf(0, 2, 4, 6, 8))
        val measured = mutableListOf<Int>()

        val offset = truncation.findOffset(lineStart = 2, lineEnd = 7, byWord = false) {
            measured += it
            it <= 4
        }

        assertEquals(4, offset)
        assertEquals(listOf(0, 6, 4), measured)
    }

    @Test
    fun `prefers a complete word over a longer partial word`() {
        val truncation = truncation("first second", lineOffsets = listOf(0, 6, 12))

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 10, byWord = true) { true }

        assertEquals(5, offset)
    }

    @Test
    fun `falls back to grapheme boundaries when words do not fit`() {
        val truncation = truncation("first second", lineOffsets = listOf(0, 6, 12))

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 10, byWord = true) { it <= 3 }

        assertEquals(3, offset)
    }

    @Test
    fun `trims whitespace zero width space and soft hyphen at a line break`() {
        val text = "word \t\n\u2003\u200B\u00AD"
        val truncation = truncation(text)
        val measured = mutableListOf<Int>()

        val offset = truncation.findOffset(lineStart = 0, lineEnd = text.length, byWord = true) {
            measured += it
            true
        }

        assertEquals(4, offset)
        assertEquals(listOf(0, 4), measured)
    }

    @Test
    fun `does not trim non breaking spaces`() {
        for (space in listOf('\u00A0', '\u2007', '\u202F')) {
            val text = "word$space"
            val truncation = truncation(text)

            val offset = truncation.findOffset(
                lineStart = 0, lineEnd = text.length, byWord = true,
            ) { true }

            assertEquals(text.length, offset, "space ${space.code}")
        }
    }

    @Test
    fun `does not trim characters in grapheme mode`() {
        val truncation = truncation("word ")

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 5, byWord = false) { true }

        assertEquals(5, offset)
    }

    @Test
    fun `rejects a trimmed word offset inside a platform grapheme`() {
        val truncation = truncation(
            "word x", graphemeOffsets = listOf(0, 3, 5, 6), lineOffsets = listOf(0, 5, 6),
        )
        val measured = mutableListOf<Int>()

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 5, byWord = true) {
            measured += it
            true
        }

        assertEquals(5, offset)
        assertEquals(listOf(0, 5), measured)
    }

    @Test
    fun `does not split Unicode clusters even when the platform offers every code unit`() {
        val clusters = listOf(
            "😀", "👨‍👩‍👧‍👦", "👍🏽", "🇺🇳", "a\u0301", "क\u093E", "a\u20DD",
            "a\uFE0F", "a\uDB40\uDD00", "🏴\uDB40\uDC67\uDB40\uDC7F", "a\uD834\uDD65",
        )
        for (cluster in clusters) {
            val text = "ok $cluster tail"
            val truncation = truncation(text)
            for (end in 4 until 3 + cluster.length) {
                val measured = mutableListOf<Int>()

                val offset = truncation.findOffset(lineStart = 0, lineEnd = end, byWord = false) {
                    measured += it
                    true
                }

                assertEquals(3, offset, "cluster $cluster at $end")
                assertEquals(listOf(0, 3), measured, "cluster $cluster at $end")
            }
        }
    }

    @Test
    fun `allows a boundary after a complete Unicode cluster`() {
        val text = "ok 👨‍👩‍👧‍👦"
        val truncation = truncation(text)

        val offset = truncation.findOffset(
            lineStart = 0, lineEnd = text.length, byWord = false,
        ) { true }

        assertEquals(text.length, offset)
    }

    @Test
    fun `pairs regional indicators from the start of the run`() {
        val truncation = truncation("x🇺🇳🇺🇸!")

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 7, byWord = false) { true }

        assertEquals(5, offset)
    }

    @Test
    fun `unpaired surrogates remain individual code units`() {
        for (text in listOf("a\uD800b", "a\uDC00b")) {
            val truncation = truncation(text)

            val offset = truncation.findOffset(lineStart = 0, lineEnd = 2, byWord = false) { true }

            assertEquals(2, offset)
        }
    }

    @Test
    fun `unpaired surrogates at text edges remain individual code units`() {
        for (text in listOf("\uDC00a", "a\uD800")) {
            val truncation = truncation(text)

            val offset = truncation.findOffset(lineStart = 0, lineEnd = 1, byWord = false) { true }

            assertEquals(1, offset)
        }
    }

    @Test
    fun `fallback repairs a line start inside a newer emoji cluster`() {
        val truncation = truncation("prefix 👨‍👩‍👧‍👦 tail")
        val measured = mutableListOf<Int>()

        val offset = truncation.findOffset(lineStart = 9, lineEnd = 12, byWord = false) {
            measured += it
            true
        }

        assertEquals(7, offset)
        assertEquals(listOf(0, 7), measured)
    }

    @Test
    fun `fallback never measures earlier candidates after the closest safe boundary fails`() {
        val prefix = "a".repeat(10_000)
        val truncation = truncation("$prefix👨‍👩‍👧‍👦 tail")
        val measured = mutableListOf<Int>()

        val offset = truncation.findOffset(
            lineStart = prefix.length + 2, lineEnd = prefix.length + 5, byWord = true,
        ) {
            measured += it
            it < prefix.length
        }

        assertNull(offset)
        assertEquals(listOf(0, prefix.length), measured)
    }

    @Test
    fun `empty text uses the marker boundary`() {
        val truncation = truncation("")

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 0, byWord = true) { true }

        assertEquals(0, offset)
    }

    private fun truncation(
        text: String,
        graphemeOffsets: List<Int> = (0..text.length).toList(),
        lineOffsets: List<Int> = listOf(0, text.length),
    ): TextTruncation = TextTruncation(text) { character ->
        FixedBoundaries(if (character) graphemeOffsets else lineOffsets)
    }

    private class FixedBoundaries(private val offsets: List<Int>) : TextBoundaryIterator {
        override fun isBoundary(offset: Int): Boolean = offset in offsets

        override fun preceding(offset: Int): Int = offsets.lastOrNull { it < offset }
            ?: TextBoundaryIterator.DONE
    }
}
