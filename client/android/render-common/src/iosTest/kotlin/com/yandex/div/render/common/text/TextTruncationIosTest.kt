package com.yandex.div.render.common.text

import kotlin.test.Test
import kotlin.test.assertEquals

class TextTruncationIosTest {

    @Test
    fun `uses UTF-16 grapheme boundaries including text edges`() {
        val cases = mapOf(
            "" to listOf(0),
            "a😀b" to listOf(0, 1, 1, 3, 4),
            "a\u0301b" to listOf(0, 0, 2, 3),
            "🇺🇳!" to listOf(0, 0, 0, 0, 4, 5),
            "a\r\nb" to listOf(0, 1, 1, 3, 4),
            "👨‍👩‍👧‍👦!" to listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12),
        )
        for ((text, expected) in cases) {
            val truncation = TextTruncation(text)

            val offsets = (0..text.length).map { end ->
                truncation.findOffset(lineStart = 0, lineEnd = end, byWord = false) { true }
            }

            assertEquals(expected, offsets, text)
        }
    }

    @Test
    fun `uses line break opportunities for word truncation`() {
        val cases = listOf(
            Triple("", 0, 0),
            Triple("first second", 10, 5),
            Triple("first-second", 10, 6),
            Triple("first   second", 12, 5),
            Triple("first\u00A0second", 10, 10),
            Triple("你好世界", 3, 3),
            Triple("first second", 12, 12),
        )
        for ((text, end, expected) in cases) {
            val truncation = TextTruncation(text)

            val offset = truncation.findOffset(lineStart = 0, lineEnd = end, byWord = true) { true }

            assertEquals(expected, offset, text)
        }
    }

    @Test
    fun `searches earlier line boundaries when the last word does not fit`() {
        val truncation = TextTruncation("first second third")

        val offset = truncation.findOffset(lineStart = 0, lineEnd = 17, byWord = true) { it <= 7 }

        assertEquals(5, offset)
    }
}
