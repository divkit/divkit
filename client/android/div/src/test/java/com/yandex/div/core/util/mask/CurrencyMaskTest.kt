package com.yandex.div.core.util.mask

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class CurrencyMaskTest {
    private val russianLocale = Locale.forLanguageTag("ru-RU")
    private val deutschLocale = Locale.forLanguageTag("de-DE")
    private val englishLocale = Locale.forLanguageTag("en-US")

    @Test
    fun `insert decimal to empty string`() {
        val currencyMask = createCurrencyMask()

        currencyMask.applyChangesFromWithNbsp("1", 1)

        currencyMask.assertMask("1", 1)
    }

    @Test
    fun `insert multiple decimals at once to empty string`() {
        val currencyMask = createCurrencyMask()

        currencyMask.applyChangesFromWithNbsp("12345", 5)

        currencyMask.assertMask("12 345", 6)
    }

    @Test
    fun `insert one decimal`() {
        val currencyMask = createCurrencyMask().withValue("1")

        currencyMask.applyChangesFromWithNbsp("12", 2)

        currencyMask.assertMask("12", 2)
    }

    @Test
    fun `insert multiple decimals`() {
        val currencyMask = createCurrencyMask().withValue("1")

        currencyMask.applyChangesFromWithNbsp("12345", 5)

        currencyMask.assertMask("12 345", 6)
    }

    @Test
    fun `remove one decimal`() {
        val currencyMask = createCurrencyMask().withValue("12345")

        currencyMask.applyChangesFromWithNbsp("12 45", 3)

        currencyMask.assertMask("1 245", 3)
    }

    @Test
    fun `remove multiple decimals`() {
        val currencyMask = createCurrencyMask().withValue("123456")

        currencyMask.applyChangesFromWithNbsp("12 56", 3)

        currencyMask.assertMask("1 256", 3)
    }

    @Test
    fun `deutsch currency mask`() {
        val currencyMask = createCurrencyMask(deutschLocale)

        currencyMask.applyChangesFromWithNbsp("1.234,56", 8)

        currencyMask.assertMask("1.234,56", 8)
    }

    @Test
    fun `english currency mask`() {
        val currencyMask = createCurrencyMask(englishLocale)

        currencyMask.applyChangesFromWithNbsp("1,234.56", 8)

        currencyMask.assertMask("1,234.56", 8)
    }

    @Test
    fun `insert comma english currency mask`() {
        val currencyMask = createCurrencyMask(englishLocale).withValue("123456")

        currencyMask.applyChangesFromWithNbsp("123,4,56", 6)

        currencyMask.assertMask("1,234.56", 6)
    }

    @Test
    fun `input separator`() {
        val currencyMask = createCurrencyMask().withValue("1234")

        currencyMask.applyChangesFromWithNbsp("1 234,", 6)

        currencyMask.assertMask("1 234,", 6)
    }

    @Test
    fun `remove separator`() {
        val currencyMask = createCurrencyMask().withValue("1234,")

        currencyMask.applyChangesFromWithNbsp("1 234", 5)

        currencyMask.assertMask("1 234", 5)
    }

    @Test
    fun `insert decimal after separator`() {
        val currencyMask = createCurrencyMask().withValue("1234,")

        currencyMask.applyChangesFromWithNbsp("1 234,5", 7)

        currencyMask.assertMask("1 234,5", 7)
    }

    @Test
    fun `remove decimal after separator`() {
        val currencyMask = createCurrencyMask().withValue("1234,56")

        currencyMask.applyChangesFromWithNbsp("1 234,6", 6)

        currencyMask.assertMask("1 234,6", 6)
    }

    @Test
    fun `insert separator with overflow not allowed`() {
        val currencyMask = createCurrencyMask().withValue("12345678")

        currencyMask.applyChangesFromWithNbsp("12 34,5 678", 6)

        currencyMask.assertMask("12 345 678", 5)
    }

    @Test
    fun `insert separator without overflow allowed`() {
        val currencyMask = createCurrencyMask().withValue("123456")

        currencyMask.applyChangesFromWithNbsp("123 4,56", 6)

        currencyMask.assertMask("1 234,56", 6)
    }

    @Test
    fun `remove separator from a middle`() {
        val currencyMask = createCurrencyMask().withValue("1234,56")

        currencyMask.applyChangesFromWithNbsp("1 23456", 5)

        currencyMask.assertMask("123 456", 5)
    }

    @Test
    fun `change locale with value`() {
        val currencyMask = createCurrencyMask().withValue("1 234,56")

        currencyMask.assertMask("1 234,56", 0)

        currencyMask.updateCurrencyParams(deutschLocale)

        currencyMask.assertMask("1.234,56", 0)
    }

    @Test
    fun `input dot as separator for deutsch locale`() {
        val currencyMask = createCurrencyMask(deutschLocale).withValue("1234567")

        currencyMask.applyChangesFromWithNbsp("1.234.5.67", 8)

        currencyMask.assertMask("12.345,67", 7)
    }

    @Test
    fun `input dot as separator for english locale`() {
        val currencyMask = createCurrencyMask(englishLocale).withValue("1234567")

        currencyMask.applyChangesFromWithNbsp("1,234,5.67", 8)

        currencyMask.assertMask("12,345.67", 7)
    }

    @Test
    fun `input comma as separator for english locale`() {
        val currencyMask = createCurrencyMask(englishLocale).withValue("1234567")

        currencyMask.applyChangesFromWithNbsp("1,234,5,67", 8)

        currencyMask.assertMask("12,345.67", 7)
    }

    @Test
    fun `input invalid character`() {
        val currencyMask = createCurrencyMask().withValue("1234,5")

        currencyMask.applyChangesFromWithNbsp("1 23a4,5", 5)

        currencyMask.assertMask("1 234,5", 4)
    }

    @Test
    fun `input second separator before one`() {
        val currencyMask = createCurrencyMask().withValue("1234,56")

        currencyMask.applyChangesFromWithNbsp("1 2,34,56", 4)

        currencyMask.assertMask("1 234,56", 3)
    }

    @Test
    fun `input second separator after one`() {
        val currencyMask = createCurrencyMask().withValue("1234,56")

        currencyMask.applyChangesFromWithNbsp("1 234,5,6", 8)

        currencyMask.assertMask("1 234,56", 7)
    }

    @Test
    fun `fully clear field`() {
        val currencyMask = createCurrencyMask().withValue("1234,56")

        currencyMask.applyChangesFromWithNbsp("", 0)

        currencyMask.assertMask("0", 1)
    }

    @Test
    fun `type digit after leading zero`() {
        val currencyMask = createCurrencyMask()

        currencyMask.applyChangesFromWithNbsp("01", 1)

        currencyMask.assertMask("1", 0)
    }

    @Test
    fun `type zero digits as leading`() {
        val currencyMask = createCurrencyMask().withValue("1 234")

        currencyMask.applyChangesFromWithNbsp("0 001 234", 4)

        currencyMask.assertMask("1 234", 0)
    }

    @Test
    fun `type char at end with overflow`() {
        val currencyMask = createCurrencyMask().withValue("123,45")

        currencyMask.applyChangesFromWithNbsp("123,465", 6)

        currencyMask.assertMask("123,45", 5)
    }

    @Test
    fun `type char and separator at end with overflow`() {
        val currencyMask = createCurrencyMask().withValue("12345")

        currencyMask.applyChangesFromWithNbsp("12 3,645", 6)

        currencyMask.assertMask("123,45", 4)
    }

    @Test
    fun `type char and separator at end without overflow`() {
        val currencyMask = createCurrencyMask().withValue("123,4")

        currencyMask.applyChangesFromWithNbsp("123,45", 6)

        currencyMask.assertMask("123,45", 6)
    }

    @Test
    fun `add character in repeated substring`() {
        val currencyMask = createCurrencyMask().withValue("11111")

        currencyMask.applyChangesFromWithNbsp("11 1111", 5)

        currencyMask.assertMask("111 111", 5)
    }

    @Test
    fun `remove character in repeated substring`() {
        val currencyMask = createCurrencyMask().withValue("11111")

        currencyMask.applyChangesFromWithNbsp("11 11", 4)

        currencyMask.assertMask("1 111", 4)
    }

    @Test
    fun `remove cluster separator`() {
        val currencyMask = createCurrencyMask().withValue("12345")

        currencyMask.applyChangesFromWithNbsp("12345", 2)

        currencyMask.assertMask("12 345", 2)
    }

    private fun createCurrencyMask(locale: Locale = russianLocale): CurrencyInputMask {
        return CurrencyInputMask(locale) { }
    }

    private fun CurrencyInputMask.applyChangesFromWithNbsp(value: String, position: Int?) {
        applyChangeFrom(value.withNbsp(), position)
    }

    private fun CurrencyInputMask.withValue(value: String): CurrencyInputMask {
        applyChangeFrom(value.withNbsp())

        return this
    }

    private fun CurrencyInputMask.assertMask(
        expectedMaskedValue: String,
        expectedCursorPosition: Int
    ) {
        Assert.assertEquals(expectedMaskedValue.withNbsp(), value)
        Assert.assertEquals(expectedCursorPosition, cursorPosition)
    }

    private fun String.withNbsp(): String = replace(' ', ' ')
}
