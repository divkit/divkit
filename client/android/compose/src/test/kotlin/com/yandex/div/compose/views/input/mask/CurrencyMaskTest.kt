package com.yandex.div.compose.views.input.mask

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.input
import com.yandex.div2.DivCurrencyInputMask
import com.yandex.div2.DivInputMask
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class CurrencyMaskTest {

    @get:Rule
    val rule = createComposeRule()

    private val rawVar = Variable.StringVariable("currency_raw", "")
    private val displayVar = Variable.StringVariable("currency_display", "")
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(rawVar, displayVar) },
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `en-US typing digits groups by thousands`() {
        setContent("en-US")

        field.performTextInput("1234567")

        field.assertTextEquals("1,234,567")
        assertEquals("1234567", rawVar.getValue())
        assertEquals("1,234,567", displayVar.getValue())
    }

    @Test
    fun `en-US decimal separator preserved`() {
        setContent("en-US")

        field.performTextInput("1234.56")

        field.assertTextEquals("1,234.56")
        assertEquals("1,234.56", displayVar.getValue())
    }

    @Test
    fun `en-US extra fraction digits beyond maxFractionDigits are dropped`() {
        setContent("en-US")

        field.performTextInput("12.3456")

        field.assertTextEquals("12.34")
        assertEquals("12.34", displayVar.getValue())
    }

    @Test
    fun `en-US second decimal separator is dropped`() {
        setContent("en-US")

        field.performTextInput("1.2.3")

        field.assertTextEquals("1.23")
        assertEquals("1.23", rawVar.getValue())
    }

    @Test
    fun `en-US non-digit and non-separator chars are dropped`() {
        setContent("en-US")

        field.performTextInput("$1a2b3.4c5")

        field.assertTextEquals("123.45")
        assertEquals("123.45", rawVar.getValue())
        assertEquals("123.45", displayVar.getValue())
    }

    @Test
    fun `en-US external raw variable update reformats display`() {
        setContent("en-US")

        rawVar.set("9876543")

        field.assertTextEquals("9,876,543")
        assertEquals("9,876,543", displayVar.getValue())
    }

    @Test
    fun `ru-RU decimal separator is comma and grouping separator is nbsp`() {
        setContent("ru-RU")

        field.performTextInput("1234567,89")

        field.assertTextEquals("1\u00A0234\u00A0567,89")
        assertEquals("1234567.89", rawVar.getValue())
        assertEquals("1\u00A0234\u00A0567,89", displayVar.getValue())
    }

    @Test
    fun `ru-RU typed dot is the decimal separator`() {
        setContent("ru-RU")

        "123.45".forEach { field.performTextInput(it.toString()) }

        field.assertTextEquals("123,45")
        assertEquals("123.45", rawVar.getValue())
    }

    @Test
    fun `ru-RU external raw variable with a decimal point is shown with the locale separator`() {
        setContent("ru-RU")

        rawVar.set("1234.5")

        field.assertTextEquals("1\u00A0234,5")
        assertEquals("1234.5", rawVar.getValue())
    }

    @Test
    fun `de-DE typed point is the decimal separator despite the point grouping`() {
        setContent("de-DE")

        "1234.5".forEach { field.performTextInput(it.toString()) }

        field.assertTextEquals("1.234,5")
        assertEquals("1234.5", rawVar.getValue())
    }

    @Test
    fun `en-US typed comma is the decimal separator`() {
        setContent("en-US")

        "1,5".forEach { field.performTextInput(it.toString()) }

        field.assertTextEquals("1.5")
        assertEquals("1.5", rawVar.getValue())
    }

    @Test
    fun `de-DE pasted integer with a thousands group keeps its digits`() {
        setContent("de-DE")

        field.performTextInput("1.234")

        field.assertTextEquals("1.234")
        assertEquals("1234", rawVar.getValue())
    }

    @Test
    fun `de-DE pasted integer with several thousands groups keeps its digits`() {
        setContent("de-DE")

        field.performTextInput("1.234.567")

        field.assertTextEquals("1.234.567")
        assertEquals("1234567", rawVar.getValue())
    }

    @Test
    fun `en-US pasted integer with a thousands group keeps its digits`() {
        setContent("en-US")

        field.performTextInput("1,234")

        field.assertTextEquals("1,234")
        assertEquals("1234", rawVar.getValue())
    }

    @Test
    fun `en-US pasted integer with several thousands groups keeps its digits`() {
        setContent("en-US")

        field.performTextInput("1,234,567")

        field.assertTextEquals("1,234,567")
        assertEquals("1234567", rawVar.getValue())
    }

    @Test
    fun `de-DE external raw variable with a decimal point is shown with the locale separator`() {
        setContent("de-DE")

        rawVar.set("1234.5")

        field.assertTextEquals("1.234,5")
        assertEquals("1234.5", rawVar.getValue())
    }

    @Test
    fun `en-US external raw variable formatted with grouping keeps its decimal separator`() {
        setContent("en-US")

        rawVar.set("1,234.56")

        field.assertTextEquals("1,234.56")
        assertEquals("1,234.56", rawVar.getValue())
    }

    @Test
    fun `de-DE external raw variable formatted with grouping keeps its decimal separator`() {
        setContent("de-DE")

        rawVar.set("1.234,56")

        field.assertTextEquals("1.234,56")
        assertEquals("1.234,56", rawVar.getValue())
    }

    @Test
    fun `JPY no fractional part, decimal separator is rejected`() {
        setContent("ja-JP")

        field.performTextInput("1234.56")

        field.assertTextEquals("123,456")
        assertEquals("123456", rawVar.getValue())
        assertEquals("123,456", displayVar.getValue())
    }

    @Test
    fun `en-US leading decimal separator is kept`() {
        setContent("en-US")

        field.performTextInput(".5")

        field.assertTextEquals(".5")
        assertEquals(".5", rawVar.getValue())
    }

    private fun setContent(localeTag: String) {
        rule.setContent(
            configuration = configuration,
            data = data(
                input(
                    mask = DivInputMask.Currency(
                        DivCurrencyInputMask(locale = constant(localeTag), rawTextVariable = "currency_raw")
                    ),
                    textVariable = "currency_display",
                )
            ),
        )
    }
}
