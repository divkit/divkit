package com.yandex.div.compose.views.input

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTextReplacement
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
import com.yandex.div2.DivInputFilter
import com.yandex.div2.DivInputFilterRegex
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivInputMaxLengthTest {

    @get:Rule
    val rule = createComposeRule()

    private val source = Variable.StringVariable("text", "")
    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = variableController,
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `text is unrestricted when max length is absent`() {
        setContent()

        field.performTextInput("abcdef")

        field.assertTextEquals("abcdef")
    }

    @Test
    fun `paste is clipped to max length`() {
        setContent(maxLength = constant(3L))

        field.performTextInput("abcdef")

        field.assertTextEquals("abc")
        assertEquals("abc", source.getValue())
    }

    @Test
    fun `middle insertion preserves text after cursor`() {
        source.set("abcd")
        setContent(maxLength = constant(6L))
        field.performTextInputSelection(TextRange(2))

        field.performTextInput("WXYZ")

        field.assertTextEquals("abWXcd")
    }

    @Test
    fun `selection replacement uses available length`() {
        source.set("abcdef")
        setContent(maxLength = constant(6L))
        field.performTextInputSelection(TextRange(2, 4))

        field.performTextInput("WXYZ")

        field.assertTextEquals("abWXef")
    }

    @Test
    fun `reversed selection replacement uses available length`() {
        source.set("abcdef")
        setContent(maxLength = constant(6L))
        field.performTextInputSelection(TextRange(4, 2))

        field.performTextInput("WXYZ")

        field.assertTextEquals("abWXef")
    }

    @Test
    fun `whole text replacement is clipped`() {
        source.set("abc")
        setContent(maxLength = constant(3L))

        field.performTextReplacement("WXYZ")

        field.assertTextEquals("WXY")
    }

    @Test
    fun `whole text replacement does not preserve a matching suffix`() {
        source.set("abc")
        setContent(maxLength = constant(3L))
        field.performTextInputSelection(TextRange(1))

        field.performTextReplacement("abXYc")

        field.assertTextEquals("abX")
    }

    @Test
    fun `whole text replacement at start does not preserve old text`() {
        source.set("abc")
        setContent(maxLength = constant(4L))
        field.performTextInputSelection(TextRange(0))

        field.performTextReplacement("WXYZabc")

        field.assertTextEquals("WXYZ")
        assertEquals("WXYZ", source.getValue())
    }

    @Test
    fun `cutoff does not split a surrogate pair`() {
        setContent(maxLength = constant(2L))

        field.performTextInput("a🙂b")

        field.assertTextEquals("a")
    }

    @Test
    fun `initial text is clipped without changing source`() {
        source.set("abcdef")

        setContent(maxLength = constant(3L))

        field.assertTextEquals("abc")
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `external text is clipped without changing source`() {
        setContent(maxLength = constant(3L))

        source.set("abcdef")

        field.assertTextEquals("abc")
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `external text with unchanged display preserves cursor`() {
        source.set("abc")
        setContent(maxLength = constant(3L))
        field.performTextInputSelection(TextRange(1))

        source.set("abcdef")

        field.assertTextEquals("abc")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `external text with unchanged display preserves selection`() {
        source.set("abcdef")
        setContent(maxLength = constant(3L))
        field.performTextInputSelection(TextRange(1, 2))

        source.set("abcxyz")

        field.assertTextEquals("abc")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1, 2)))
        assertEquals("abcxyz", source.getValue())
    }

    @Test
    fun `moving cursor keeps oversized source unchanged`() {
        source.set("abcdef")
        setContent(maxLength = constant(3L))

        field.performTextInputSelection(TextRange(1))

        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `replacing displayed text with equal content keeps oversized source`() {
        source.set("abcdef")
        setContent(maxLength = constant(3L))

        field.performTextReplacement("abc")

        field.assertTextEquals("abc")
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `rejected insertion keeps oversized source unchanged`() {
        source.set("abcdef")
        setContent(maxLength = constant(3L))

        field.performTextInput("X")

        field.assertTextEquals("abc")
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `rejected repeated character insertion keeps cursor`() {
        source.set("aaa")
        setContent(maxLength = constant(3L))
        field.performTextInputSelection(TextRange(1))

        field.performTextInput("a")

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
    }

    @Test
    fun `decreasing max length preserves current text and cursor`() {
        val limit = Variable.IntegerVariable("limit", 6)
        variableController.declare(limit)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"))
        field.performTextInputSelection(TextRange(2))

        limit.set(3)

        field.assertTextEquals("abcdef")
        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(2)))
    }

    @Test
    fun `next replacement uses decreased max length`() {
        val limit = Variable.IntegerVariable("limit", 6)
        variableController.declare(limit)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"))
        limit.set(4)
        field.performTextInputSelection(TextRange(2, 5))

        field.performTextInput("XYZ")

        field.assertTextEquals("abXf")
    }

    @Test
    fun `whole text replacement uses decreased max length at end cursor`() {
        val limit = Variable.IntegerVariable("limit", 6)
        variableController.declare(limit)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"))
        limit.set(3)
        field.performTextInputSelection(TextRange(6))

        field.performTextReplacement("abcdefX")

        field.assertTextEquals("abc")
    }

    @Test
    fun `append preserves existing text after decreasing max length`() {
        val limit = Variable.IntegerVariable("limit", 6)
        variableController.declare(limit)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"))
        limit.set(3)
        field.performTextInputSelection(TextRange(6))

        field.performTextInput("X")

        field.assertTextEquals("abcdef")
    }

    @Test
    fun `deletion is allowed while text exceeds decreased limit`() {
        val limit = Variable.IntegerVariable("limit", 6)
        variableController.declare(limit)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"))
        limit.set(3)
        field.performTextInputSelection(TextRange(1, 2))

        field.performTextInput("")

        field.assertTextEquals("acdef")
    }

    @Test
    fun `increased max length allows more input without integer overflow`() {
        val limit = Variable.IntegerVariable("limit", 2)
        variableController.declare(limit)
        source.set("ab")
        setContent(maxLength = intExpression("@{limit}"))
        limit.set(Long.MAX_VALUE)

        field.performTextInput("cdef")

        field.assertTextEquals("abcdef")
    }

    @Test
    fun `user input is clipped before regex validation`() {
        setContent(maxLength = constant(3L), pattern = constant("[a-z]{3}"))

        field.performTextInput("abcdef")

        field.assertTextEquals("abc")
    }

    @Test
    fun `external text is validated before clipping`() {
        setContent(maxLength = constant(3L), pattern = constant("[a-z]{4,}"))

        source.set("abcdef")

        field.assertTextEquals("abc")
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `selection change does not validate clipped display`() {
        source.set("abcdef")
        setContent(maxLength = constant(3L), pattern = constant("[a-z]{4,}"))

        field.performTextInputSelection(TextRange(1))

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
    }

    @Test
    fun `source observer normalization is reflected in display`() {
        source.set("AB")
        source.addObserver { variable ->
            val value = variable.getValue().toString().uppercase()
            if (variable.getValue() != value) variable.set(value)
        }
        setContent(maxLength = constant(4L))

        field.performTextInput("c")

        field.assertTextEquals("ABC")
        assertEquals("ABC", source.getValue())
    }

    @Test
    fun `filter change applies a previously rejected external value`() {
        val pattern = Variable.StringVariable("pattern", "[0-9]+")
        variableController.declare(pattern)
        source.set("123")
        setContent(maxLength = constant(3L), pattern = expression("@{pattern}"))
        source.set("abcdef")
        rule.waitForIdle()

        pattern.set("[a-z]+")

        field.assertTextEquals("abc")
        assertEquals("abcdef", source.getValue())
    }

    @Test
    fun `external text uses a limit changed in the same update`() {
        val limit = Variable.IntegerVariable("limit", 3)
        variableController.declare(limit)
        setContent(maxLength = intExpression("@{limit}"))

        rule.runOnIdle {
            limit.set(5)
            source.set("abcdef")
        }

        field.assertTextEquals("abcde")
    }

    @Test
    fun `external text uses a filter changed in the same update`() {
        val pattern = Variable.StringVariable("pattern", "[0-9]+")
        variableController.declare(pattern)
        source.set("123")
        setContent(maxLength = constant(3L), pattern = expression("@{pattern}"))

        rule.runOnIdle {
            pattern.set("[a-z]+")
            source.set("abcdef")
        }

        field.assertTextEquals("abc")
    }

    @Test
    fun `external text uses a limit decreased after it in the same update`() {
        val limit = Variable.IntegerVariable("limit", 5)
        variableController.declare(limit)
        setContent(maxLength = intExpression("@{limit}"))

        rule.runOnIdle {
            source.set("abcdef")
            limit.set(3)
        }

        field.assertTextEquals("abc")
    }

    @Test
    fun `source observer can restore the preceding value`() {
        source.set("AB")
        source.addObserver { variable ->
            if (variable.getValue() != "AB") variable.set("AB")
        }
        setContent(maxLength = constant(4L))

        field.performTextInput("c")

        field.assertTextEquals("AB")
        assertEquals("AB", source.getValue())
    }

    @Test
    fun `filter update does not reclip text after decreasing limit`() {
        val limit = Variable.IntegerVariable("limit", 6)
        val pattern = Variable.StringVariable("pattern", "[a-z]+")
        variableController.declare(limit, pattern)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"), pattern = expression("@{pattern}"))
        limit.set(3)
        rule.waitForIdle()

        pattern.set(".*")

        field.assertTextEquals("abcdef")
    }

    @Test
    fun `filter update does not expand text after increasing limit`() {
        val limit = Variable.IntegerVariable("limit", 3)
        val pattern = Variable.StringVariable("pattern", "[a-z]+")
        variableController.declare(limit, pattern)
        source.set("abcdef")
        setContent(maxLength = intExpression("@{limit}"), pattern = expression("@{pattern}"))
        limit.set(6)
        rule.waitForIdle()

        pattern.set(".*")

        field.assertTextEquals("abc")
    }

    private fun setContent(maxLength: Expression<Long>? = null, pattern: Expression<String>? = null) {
        variableController.declare(source)
        rule.setContent(
            configuration = configuration,
            data = data(
                input(
                    filters = pattern?.let {
                        listOf(DivInputFilter.Regex(DivInputFilterRegex(pattern = it)))
                    },
                    maxLength = maxLength,
                    textVariable = "text",
                )
            ),
        )
    }
}
