package com.yandex.div.render.common.text

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextCharacterPropertiesTest {

    @Test
    fun `trims Java whitespace including control separators`() {
        val whitespace = listOf(
            0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x001C, 0x001D, 0x001E, 0x001F,
            0x0020, 0x1680, 0x2000, 0x2001, 0x2002, 0x2003, 0x2004, 0x2005, 0x2006,
            0x2008, 0x2009, 0x200A, 0x2028, 0x2029, 0x205F, 0x3000,
        )

        for (codePoint in whitespace) {
            assertTrue(isTrimmableWhitespace(codePoint), "code point $codePoint")
        }
    }

    @Test
    fun `does not trim non breaking spaces or other Unicode characters`() {
        val characters = listOf(0x0085, 0x00A0, 0x2007, 0x200B, 0x202F, 0x0041, 0x10020)

        for (codePoint in characters) {
            assertFalse(isTrimmableWhitespace(codePoint), "code point $codePoint")
        }
    }

    @Test
    fun `recognizes all mark categories including supplementary code points`() {
        for (codePoint in listOf(0x0301, 0x093E, 0x20DD, 0x1D165, 0x1D185, 0xE0100)) {
            assertTrue(isCombiningMark(codePoint), "code point $codePoint")
        }
        for (codePoint in listOf(0x0041, 0x00E9, 0x0020, 0x0905, 0x1F600, 0x10301)) {
            assertFalse(isCombiningMark(codePoint), "code point $codePoint")
        }
    }
}
