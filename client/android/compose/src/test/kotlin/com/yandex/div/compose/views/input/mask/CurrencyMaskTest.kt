package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.views.input.DivInputState
import com.yandex.div.compose.views.input.rememberDivInputState
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivCurrencyInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class CurrencyMaskTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()
    private val localComponent = mockLocalComponent(
        reporter = reporter,
        variableController = variableController,
    )

    private val rawVar = Variable.StringVariable(RAW_VAR, "")
    private val displayVar = Variable.StringVariable(DISPLAY_VAR, "")

    init {
        variableController.declare(rawVar)
        variableController.declare(displayVar)
    }

    @Test
    fun `en-US typing digits groups by thousands`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("en-US").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("1234567", TextRange(7)))
        composeRule.waitForIdle()

        assertEquals("1234567", state!!.text.text)
        assertEquals("1234567", rawVar.getValue())
        assertEquals("1,234,567", displayVar.getValue())
        assertEquals("1,234,567", state!!.formattedDisplay())
    }

    @Test
    fun `en-US decimal separator preserved`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("en-US").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("1234.56", TextRange(7)))
        composeRule.waitForIdle()

        assertEquals("1234.56", state!!.text.text)
        assertEquals("1,234.56", displayVar.getValue())
    }

    @Test
    fun `en-US extra fraction digits beyond maxFractionDigits are dropped`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("en-US").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("12.3456", TextRange(7)))
        composeRule.waitForIdle()

        assertEquals("12.34", state!!.text.text)
        assertEquals("12.34", displayVar.getValue())
    }

    @Test
    fun `en-US second decimal separator is dropped`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("en-US").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("1.2.3", TextRange(5)))
        composeRule.waitForIdle()

        assertEquals("1.23", state!!.text.text)
    }

    @Test
    fun `en-US non-digit and non-separator chars are dropped`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("en-US").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("$1a2b3.4c5", TextRange(10)))
        composeRule.waitForIdle()

        assertEquals("123.45", state!!.text.text)
        assertEquals("123.45", displayVar.getValue())
    }

    @Test
    fun `en-US external raw variable update reformats display`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("en-US").rememberDivInputState() }

        rawVar.set("9876543")
        composeRule.waitForIdle()

        assertEquals("9876543", state!!.text.text)
        assertEquals("9,876,543", displayVar.getValue())
    }

    @Test
    fun `ru-RU decimal separator is comma and grouping separator is nbsp`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("ru-RU").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("1234567,89", TextRange(10)))
        composeRule.waitForIdle()

        assertEquals("1234567,89", state!!.text.text)
        assertEquals("1\u00A0234\u00A0567,89", displayVar.getValue())
    }

    @Test
    fun `ru-RU dot as decimal separator is rejected (only comma allowed)`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("ru-RU").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("123.45", TextRange(6)))
        composeRule.waitForIdle()

        assertEquals("12345", state!!.text.text)
    }

    @Test
    fun `JPY no fractional part, decimal separator is rejected`() {
        var state: DivInputState? = null
        setContent { state = currencyInput("ja-JP").rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("1234.56", TextRange(7)))
        composeRule.waitForIdle()

        assertEquals("123456", state!!.text.text)
        assertEquals("123,456", displayVar.getValue())
    }

    private fun DivInputState.formattedDisplay(): String =
        visualTransformation.filter(AnnotatedString(text.text)).text.text

    private fun currencyInput(localeTag: String): DivInput = DivInput(
        textVariable = DISPLAY_VAR,
        mask = DivInputMask.Currency(
            DivCurrencyInputMask(
                locale = Expression.constant(localeTag),
                rawTextVariable = RAW_VAR,
            ),
        ),
    )

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }

    companion object {
        private const val RAW_VAR = "currency_raw"
        private const val DISPLAY_VAR = "currency_display"
    }
}
