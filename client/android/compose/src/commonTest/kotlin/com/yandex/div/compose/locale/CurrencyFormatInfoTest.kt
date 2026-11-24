package com.yandex.div.compose.locale

import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyFormatInfoTest {

    @Test
    fun `point decimal and comma grouping are used when locale is en-US`() {
        val actual = CurrencyFormatInfo("en-US")

        assertEquals(CurrencyFormatInfo("en-US", '.', ',', 3, 2), actual)
    }

    @Test
    fun `comma decimal and point grouping are used when locale is de-DE`() {
        val actual = CurrencyFormatInfo("de-DE")

        assertEquals(CurrencyFormatInfo("de-DE", ',', '.', 3, 2), actual)
    }

    @Test
    fun `non-breaking space grouping is used when locale is ru-RU`() {
        val actual = CurrencyFormatInfo("ru-RU")

        assertEquals(CurrencyFormatInfo("ru-RU", ',', '\u00A0', 3, 2), actual)
    }

    @Test
    fun `fraction digits are disabled when locale is ja-JP`() {
        val actual = CurrencyFormatInfo("ja-JP")

        assertEquals(CurrencyFormatInfo("ja-JP", '.', ',', 3, 0), actual)
    }

    @Test
    fun `three fraction digits are allowed when locale is en-KW`() {
        val actual = CurrencyFormatInfo("en-KW")

        assertEquals(CurrencyFormatInfo("en-KW", '.', ',', 3, 3), actual)
    }

    @Test
    fun `language tag is normalized`() {
        val actual = CurrencyFormatInfo("EN-us")

        assertEquals(CurrencyFormatInfo("en-US", '.', ',', 3, 2), actual)
    }
}
