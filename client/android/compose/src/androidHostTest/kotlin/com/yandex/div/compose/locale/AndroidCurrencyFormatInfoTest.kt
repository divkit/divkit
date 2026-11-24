package com.yandex.div.compose.locale

import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidCurrencyFormatInfoTest {

    private val previousLocale = Locale.getDefault()
    private val previousDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY)
    private val previousFormatLocale = Locale.getDefault(Locale.Category.FORMAT)

    @AfterTest
    fun restoreDefaultLocales() {
        Locale.setDefault(previousLocale)
        Locale.setDefault(Locale.Category.DISPLAY, previousDisplayLocale)
        Locale.setDefault(Locale.Category.FORMAT, previousFormatLocale)
    }

    @Test
    fun `missing language tag uses the default locale`() {
        Locale.setDefault(Locale.JAPAN)
        Locale.setDefault(Locale.Category.FORMAT, Locale.US)

        val actual = CurrencyFormatInfo(null)

        assertEquals(CurrencyFormatInfo("ja-JP", '.', ',', 3, 0), actual)
    }

    @Test
    fun `undefined language is used when the tag contains an invalid separator`() {
        val actual = CurrencyFormatInfo("en_US")

        assertEquals("und", actual.languageTag)
    }

    @Test
    fun `valid language tag prefix is kept when the suffix is malformed`() {
        val actual = CurrencyFormatInfo("en-US-!")

        assertEquals(CurrencyFormatInfo("en-US", '.', ',', 3, 2), actual)
    }
}
