package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
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
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class FixedLengthMaskTest {

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
    fun `typing digits fills dynamic slots and inserts static characters`() {
        var state: DivInputState? = null
        setContent { state = digitsInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("1234567890", TextRange(10)))
        composeRule.waitForIdle()

        assertEquals("1234567890", state!!.text.text)
        assertEquals("1234567890", rawVar.getValue())
        assertEquals("(123) 456-7890", displayVar.getValue())
        assertEquals("(123) 456-7890", state!!.formattedDisplay())
    }

    @Test
    fun `characters that do not match the regex are dropped`() {
        var state: DivInputState? = null
        setContent { state = digitsInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("12ab34cd56", TextRange(10)))
        composeRule.waitForIdle()

        assertEquals("123456", state!!.text.text)
        assertEquals("123456", rawVar.getValue())
    }

    @Test
    fun `raw value is capped to the number of dynamic slots`() {
        var state: DivInputState? = null
        setContent { state = digitsInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("123456789012345", TextRange(15)))
        composeRule.waitForIdle()

        assertEquals("1234567890", state!!.text.text)
        assertEquals("(123) 456-7890", displayVar.getValue())
    }

    @Test
    fun `partial input display variable holds only static + filled chars`() {
        var state: DivInputState? = null
        setContent { state = digitsInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("123", TextRange(3)))
        composeRule.waitForIdle()

        assertEquals("123", state!!.text.text)
        assertEquals("(123) ", displayVar.getValue())
    }

    @Test
    fun `alwaysVisible placeholders rendered in BasicTextField only, not stored in display variable`() {
        var state: DivInputState? = null
        setContent { state = digitsInput(alwaysVisible = true).rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("12", TextRange(2)))
        composeRule.waitForIdle()

        assertEquals("(12_) ___-____", state!!.formattedDisplay())
        assertEquals("(12", displayVar.getValue())
    }

    @Test
    fun `external raw variable update reformats display and renders placeholders`() {
        var state: DivInputState? = null
        setContent { state = digitsInput(alwaysVisible = true).rememberDivInputState() }

        rawVar.set("555")
        composeRule.waitForIdle()

        assertEquals("555", state!!.text.text)
        assertEquals("(555) ", displayVar.getValue())
        assertEquals("(555) ___-____", state!!.formattedDisplay())
    }

    @Test
    fun `external raw variable with invalid characters is sanitized`() {
        var state: DivInputState? = null
        setContent { state = digitsInput().rememberDivInputState() }

        rawVar.set("12ab34")
        composeRule.waitForIdle()

        assertEquals("1234", rawVar.getValue())
        assertEquals("(123) 4", displayVar.getValue())
    }

    @Test
    fun `multi-key pattern different regexes per slot`() {
        var state: DivInputState? = null
        setContent { state = mixedInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("Aabcde1", TextRange(7)))
        composeRule.waitForIdle()

        assertEquals("Aabcde1", state!!.text.text)
        assertEquals("A-abcde-1", displayVar.getValue())
    }

    @Test
    fun `multi-key pattern char that does not match the expected slot is dropped`() {
        var state: DivInputState? = null
        setContent { state = mixedInput().rememberDivInputState() }

        state!!.onValueChange(TextFieldValue("aAbc", TextRange(4)))
        composeRule.waitForIdle()

        assertEquals("Abc", state!!.text.text)
        assertEquals("A-bc", displayVar.getValue())
    }

    private fun DivInputState.formattedDisplay(): String =
        visualTransformation.filter(AnnotatedString(text.text)).text.text

    private fun digitsInput(alwaysVisible: Boolean = false): DivInput = DivInput(
        textVariable = DISPLAY_VAR,
        mask = DivInputMask.FixedLength(
            DivFixedLengthInputMask(
                alwaysVisible = Expression.constant(alwaysVisible),
                pattern = Expression.constant("(###) ###-####"),
                patternElements = listOf(
                    DivFixedLengthInputMask.PatternElement(
                        key = Expression.constant("#"),
                        placeholder = Expression.constant("_"),
                        regex = Expression.constant("[0-9]"),
                    ),
                ),
                rawTextVariable = RAW_VAR,
            ),
        ),
    )

    private fun mixedInput(): DivInput = DivInput(
        textVariable = DISPLAY_VAR,
        mask = DivInputMask.FixedLength(
            DivFixedLengthInputMask(
                alwaysVisible = Expression.constant(false),
                pattern = Expression.constant("\$-#####-0"),
                patternElements = listOf(
                    DivFixedLengthInputMask.PatternElement(
                        key = Expression.constant("\$"),
                        placeholder = Expression.constant("_"),
                        regex = Expression.constant("[A-Z]"),
                    ),
                    DivFixedLengthInputMask.PatternElement(
                        key = Expression.constant("#"),
                        placeholder = Expression.constant("_"),
                        regex = Expression.constant("[a-z]"),
                    ),
                    DivFixedLengthInputMask.PatternElement(
                        key = Expression.constant("0"),
                        placeholder = Expression.constant("_"),
                        regex = Expression.constant("[0-9]"),
                    ),
                ),
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
        private const val RAW_VAR = "fixed_raw"
        private const val DISPLAY_VAR = "fixed_display"
    }
}
