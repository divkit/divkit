package com.yandex.div.compose.views.input.mask

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.input
import com.yandex.div.test.data.intExpression
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask
import com.yandex.div2.DivInputValidator
import com.yandex.div2.DivInputValidatorRegex
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class FixedLengthMaskTest {

    @get:Rule
    val rule = createComposeRule()

    private val rawVar = Variable.StringVariable("fixed_raw", "")
    private val displayVar = Variable.StringVariable("fixed_display", "")
    private val variableController = DivVariableController().apply { declare(rawVar, displayVar) }
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = variableController,
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `typing digits fills dynamic slots and inserts static characters`() {
        setContent(digitsMask())

        field.performTextInput("1234567890")

        field.assertTextEquals("(123) 456-7890")
        assertEquals("1234567890", rawVar.getValue())
        assertEquals("(123) 456-7890", displayVar.getValue())
    }

    @Test
    fun `characters that do not match the regex are dropped`() {
        setContent(digitsMask())

        field.performTextInput("12ab34cd56")

        field.assertTextEquals("(123) 456-")
        assertEquals("123456", rawVar.getValue())
    }

    @Test
    fun `raw value is capped to the number of dynamic slots`() {
        setContent(digitsMask())

        field.performTextInput("123456789012345")

        field.assertTextEquals("(123) 456-7890")
        assertEquals("1234567890", rawVar.getValue())
        assertEquals("(123) 456-7890", displayVar.getValue())
    }

    @Test
    fun `digit typed after the last slot is dropped`() {
        setContent(digitsMask())
        field.performTextInput("1111111111")

        field.performTextInput("1")

        field.assertTextEquals("(111) 111-1111")
        assertEquals("1111111111", rawVar.getValue())
    }

    @Test
    fun `partial input display variable holds only static + filled chars`() {
        setContent(digitsMask())

        field.performTextInput("123")

        field.assertTextEquals("(123) ")
        assertEquals("(123) ", displayVar.getValue())
    }

    @Test
    fun `alwaysVisible placeholders are included in display variable`() {
        setContent(digitsMask(alwaysVisible = true))

        field.performTextInput("12")

        field.assertTextEquals("(12_) ___-____")
        assertEquals("(12_) ___-____", displayVar.getValue())
    }

    @Test
    fun `external raw variable update reformats display and renders placeholders`() {
        setContent(digitsMask(alwaysVisible = true))

        rawVar.set("555")

        field.assertTextEquals("(555) ___-____")
        assertEquals("(555) ___-____", displayVar.getValue())
    }

    @Test
    fun `external raw variable with invalid characters is sanitized`() {
        setContent(digitsMask())

        rawVar.set("12ab34")

        field.assertTextEquals("(123) 4")
        assertEquals("12ab34", rawVar.getValue())
        assertEquals("(123) 4", displayVar.getValue())
    }

    @Test
    fun `multi-key pattern different regexes per slot`() {
        setContent(mixedMask())

        field.performTextInput("Aabcde1")

        field.assertTextEquals("A-abcde-1")
        assertEquals("Aabcde1", rawVar.getValue())
        assertEquals("A-abcde-1", displayVar.getValue())
    }

    @Test
    fun `multi-key pattern char that does not match the expected slot is dropped`() {
        setContent(mixedMask())

        field.performTextInput("aAbc")

        field.assertTextEquals("A-bc")
        assertEquals("Abc", rawVar.getValue())
        assertEquals("A-bc", displayVar.getValue())
    }

    @Test
    fun `max length limits the formatted text`() {
        setContent(digitsMask(), maxLength = constant(6L))

        field.performTextInput("1234567890")

        field.assertTextEquals("(123) ")
        assertEquals("123", rawVar.getValue())
    }

    @Test
    fun `max length does not count always visible placeholders`() {
        setContent(digitsMask(alwaysVisible = true), maxLength = constant(6L))

        field.performTextInput("1234")

        field.assertTextEquals("(123) ___-____")
        assertEquals("123", rawVar.getValue())
    }

    @Test
    fun `external raw variable is clipped to max length without changing it`() {
        setContent(digitsMask(), maxLength = constant(6L))

        rawVar.set("1234567890")

        field.assertTextEquals("(123) ")
        assertEquals("1234567890", rawVar.getValue())
    }

    @Test
    fun `decreasing max length preserves masked text and cursor`() {
        val limit = Variable.IntegerVariable("limit", 14)
        variableController.declare(limit)
        setContent(digitsMask(), maxLength = intExpression("@{limit}"))
        field.performTextInput("1234567890")
        limit.set(6)

        field.performTextInputSelection(TextRange(3), relativeToOriginalText = false)

        field.assertTextEquals("(123) 456-7890")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(3)))
        assertEquals("1234567890", rawVar.getValue())
    }

    @Test
    fun `insertion is rejected while masked text exceeds decreased max length`() {
        val limit = Variable.IntegerVariable("limit", 14)
        variableController.declare(limit)
        setContent(digitsMask(), maxLength = intExpression("@{limit}"))
        field.performTextInput("1234567890")
        limit.set(6)
        field.performTextInputSelection(TextRange(3), relativeToOriginalText = false)

        field.performTextInput("5")

        field.assertTextEquals("(123) 456-7890")
        assertEquals("1234567890", rawVar.getValue())
    }

    @Test
    fun `paste beyond max length is rejected`() {
        setContent(digitsMask(), maxLength = constant(13L))
        field.performTextInput("123456789")
        field.performTextInputSelection(TextRange(9), relativeToOriginalText = false)

        field.performTextInput("55")

        field.assertTextEquals("(123) 456-789")
        assertEquals("123456789", rawVar.getValue())
    }

    @Test
    fun `external raw variable is clipped to max length at a surrogate pair`() {
        setContent(digitsMask(pattern = constant("##"), regex = null), maxLength = constant(1L))

        rawVar.set("\uD83D\uDE00")

        field.assertTextEquals("")
        assertEquals("\uD83D\uDE00", rawVar.getValue())
    }

    @Test
    fun `typed emoji beyond max length is dropped whole`() {
        setContent(digitsMask(pattern = constant("##"), regex = null), maxLength = constant(1L))

        field.performTextInput("\uD83D\uDE00")

        field.assertTextEquals("")
        assertEquals("", rawVar.getValue())
    }

    @Test
    fun `append is rejected while masked text exceeds decreased max length`() {
        val limit = Variable.IntegerVariable("limit", 14)
        variableController.declare(limit)
        setContent(digitsMask(), maxLength = intExpression("@{limit}"))
        field.performTextInput("1234567890")
        limit.set(6)
        rule.waitForIdle()

        field.performTextInput("5")

        field.assertTextEquals("(123) 456-7890")
        assertEquals("1234567890", rawVar.getValue())
    }

    @Test
    fun `pattern change re-applies the mask to the field`() {
        val pattern = Variable.StringVariable("pattern", "####-####")
        variableController.declare(pattern)
        setContent(digitsMask(pattern = expression("@{pattern}")))
        field.performTextInput("12345678")
        field.assertTextEquals("1234-5678")

        pattern.set("###")

        field.assertTextEquals("123")
        assertEquals("12345678", rawVar.getValue())
        assertEquals("123", displayVar.getValue())
    }

    @Test
    fun `regex change re-admits the characters of the raw variable`() {
        val regex = Variable.StringVariable("regex", "[0-9]")
        variableController.declare(regex)
        setContent(digitsMask(regex = expression("@{regex}")))
        rawVar.set("1a2")
        field.assertTextEquals("(12")

        regex.set("[0-9a-z]")

        field.assertTextEquals("(1a2) ")
        assertEquals("1a2", rawVar.getValue())
        assertEquals("(1a2) ", displayVar.getValue())
    }

    @Test
    fun `mask with a static prefix hides the hint`() {
        setContent(digitsMask(pattern = constant("+7 ###")), hintText = constant("Hint"))

        field.assertTextEquals("+7 ")
        rule.onNodeWithText("Hint").assertDoesNotExist()
    }

    @Test
    fun `password field with a mask shows the hint`() {
        setContent(
            digitsMask(pattern = constant("+7 ###")),
            keyboardType = constant(DivInput.KeyboardType.PASSWORD),
            hintText = constant("Hint"),
        )

        rule.onNodeWithText("Hint").assertIsDisplayed()
    }

    @Test
    fun `pattern change with a restoring observer keeps later edits synced`() {
        val pattern = Variable.StringVariable("pattern", "####")
        variableController.declare(pattern)
        setContent(digitsMask(pattern = expression("@{pattern}")))
        field.performTextInput("1234")
        rawVar.addObserver { variable -> if (variable.getValue() != "1234") variable.set("1234") }
        pattern.set("##")
        field.assertTextEquals("12")

        field.performKeyInput { pressKey(Key.Backspace) }

        field.assertTextEquals("12")
        assertEquals("1234", rawVar.getValue())
    }

    @Test
    fun `regex validator checks the formatted text`() {
        val valid = Variable.BooleanVariable("valid", false)
        variableController.declare(valid)
        setContent(
            digitsMask(),
            validators = listOf(
                DivInputValidator.Regex(
                    DivInputValidatorRegex(
                        labelId = constant("label"),
                        pattern = constant("""\(\d{3}\) \d{3}-\d{4}"""),
                        variable = "valid",
                    )
                )
            ),
        )

        field.performTextInput("1234567890")

        field.assertTextEquals("(123) 456-7890")
        assertEquals(true, valid.getValue())
    }

    private fun digitsMask(
        alwaysVisible: Boolean = false,
        pattern: Expression<String> = constant("(###) ###-####"),
        regex: Expression<String>? = constant("[0-9]"),
    ) = DivInputMask.FixedLength(
        DivFixedLengthInputMask(
            alwaysVisible = constant(alwaysVisible),
            pattern = pattern,
            patternElements = listOf(
                DivFixedLengthInputMask.PatternElement(
                    key = constant("#"),
                    placeholder = constant("_"),
                    regex = regex,
                ),
            ),
            rawTextVariable = "fixed_raw",
        ),
    )

    private fun mixedMask() = DivInputMask.FixedLength(
        DivFixedLengthInputMask(
            pattern = constant("\$-#####-0"),
            patternElements = listOf(
                DivFixedLengthInputMask.PatternElement(key = constant("\$"), regex = constant("[A-Z]")),
                DivFixedLengthInputMask.PatternElement(key = constant("#"), regex = constant("[a-z]")),
                DivFixedLengthInputMask.PatternElement(key = constant("0"), regex = constant("[0-9]")),
            ),
            rawTextVariable = "fixed_raw",
        ),
    )

    private fun setContent(
        mask: DivInputMask,
        hintText: Expression<String>? = null,
        keyboardType: Expression<DivInput.KeyboardType> = constant(DivInput.KeyboardType.MULTI_LINE_TEXT),
        maxLength: Expression<Long>? = null,
        validators: List<DivInputValidator>? = null,
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(
                input(
                    hintText = hintText,
                    keyboardType = keyboardType,
                    mask = mask,
                    maxLength = maxLength,
                    textVariable = "fixed_display",
                    validators = validators,
                )
            ),
        )
    }
}
