package com.yandex.div.compose.views.input.mask

import com.yandex.div.compose.locale.CurrencyFormatInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyInputMaskTest {

    private val mask = CurrencyInputMask(CurrencyFormatInfo("en-KW", '.', ',', 0, 3))

    @Test
    fun `fraction is truncated when it exceeds three digits allowed by metadata`() {
        val raw = mask.sanitize("1,234.5678")

        assertEquals("1234.567", raw)
    }

    @Test
    fun `grouping is omitted when metadata disables grouping`() {
        val formatted = mask.format("1234.567")

        assertEquals("1234.567", formatted)
    }

    @Test
    fun `digits are grouped in pairs when metadata grouping size is two`() {
        val groupedMask = CurrencyInputMask(CurrencyFormatInfo("en-US", '.', ',', 2, 2))

        val formatted = groupedMask.format("1234567")

        assertEquals("1,23,45,67", formatted)
    }

    @Test
    fun `variable value retains the decimal point when the locale uses a point`() {
        val variableValue = mask.toVariableValue("1234.567")

        assertEquals("1234.567", variableValue)
    }
}
