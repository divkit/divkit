package com.yandex.div.compose.views.input

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.input
import com.yandex.div2.DivInputFilter
import com.yandex.div2.DivInputFilterExpression
import com.yandex.div2.DivInputFilterRegex
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivInputFilterTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private val source = Variable.StringVariable("text", "")
    private val variableController = DivVariableController().apply { declare(source) }
    private val configuration = DivConfiguration(
        reporter = reporter,
        variableController = variableController,
    )
    private val field get() = rule.onNode(hasSetTextAction())

    @Test
    fun `regex accepts matching value`() {
        setContent(regexFilter("[0-9]+"))

        field.performTextInput("12345")

        field.assertTextEquals("12345")
        assertEquals("12345", source.getValue())
    }

    @Test
    fun `regex rejects non-matching value and keeps previous`() {
        setContent(regexFilter("[0-9]+"))
        field.performTextInput("123")

        field.performTextInput("abc")

        field.assertTextEquals("123")
        assertEquals("123", source.getValue())
    }

    @Test
    fun `multiple regexes - all must pass`() {
        setContent(regexFilter(".*[0-9].*"), regexFilter(".{3,}"))
        field.performTextInput("ab1")
        field.assertTextEquals("ab1")

        field.performTextReplacement("a1")

        field.assertTextEquals("ab1")
    }

    @Test
    fun `expression true accepts`() {
        setContent(expressionFilter(true))

        field.performTextInput("anything")

        field.assertTextEquals("anything")
    }

    @Test
    fun `expression false rejects`() {
        setContent(expressionFilter(false))

        field.performTextInput("anything")

        field.assertTextEquals("")
    }

    @Test
    fun `initial value rejected by filter is not shown`() {
        source.set("initial")

        setContent(expressionFilter(false))

        field.assertTextEquals("")
        assertEquals("initial", source.getValue())
    }

    @Test
    fun `combined regex and expression - both must pass`() {
        setContent(regexFilter("[a-z]+"), expressionFilter(true))
        field.performTextInput("hello")
        field.assertTextEquals("hello")

        field.performTextInput("123")

        field.assertTextEquals("hello")
    }

    @Test
    fun `combined - expression false rejects even if regex matches`() {
        setContent(regexFilter("[a-z]+"), expressionFilter(false))

        field.performTextInput("hello")

        field.assertTextEquals("")
    }

    @Test
    fun `external source update applied when matches filter`() {
        setContent(regexFilter("[0-9]+"))

        source.set("999")

        field.assertTextEquals("999")
    }

    @Test
    fun `external source update ignored when fails filter`() {
        setContent(regexFilter("[0-9]+"))
        field.performTextInput("111")

        source.set("abc")

        field.assertTextEquals("111")
    }

    @Test
    fun `invalid regex pattern reports error and is skipped`() {
        reporter.failOnError = false
        setContent(regexFilter("[invalid"))

        field.performTextInput("anything")

        field.assertTextEquals("anything")
        assertEquals("Invalid regex pattern '[invalid'", reporter.lastError)
    }

    @Test
    fun `regex filter rebuilds when pattern variable changes`() {
        val pattern = Variable.StringVariable("pattern", "[0-9]+")
        variableController.declare(pattern)
        setContent(DivInputFilter.Regex(DivInputFilterRegex(pattern = expression("@{pattern}"))))
        source.set("12345")
        field.assertTextEquals("12345")

        pattern.set("[a-z]+")
        rule.waitForIdle()
        field.assertTextEquals("12345")

        source.set("67890")
        field.assertTextEquals("12345")

        source.set("hello")
        field.assertTextEquals("hello")
    }

    @Test
    fun `expression filter reflects condition variable change`() {
        val allow = Variable.BooleanVariable("allow", true)
        variableController.declare(allow)
        setContent(DivInputFilter.Expression(DivInputFilterExpression(condition = booleanExpression("@{allow}"))))
        source.set("first")
        field.assertTextEquals("first")

        allow.set(false)
        source.set("second")
        field.assertTextEquals("first")

        allow.set(true)
        source.set("third")
        field.assertTextEquals("third")
    }

    private fun regexFilter(pattern: String): DivInputFilter =
        DivInputFilter.Regex(DivInputFilterRegex(pattern = constant(pattern)))

    private fun expressionFilter(condition: Boolean): DivInputFilter =
        DivInputFilter.Expression(DivInputFilterExpression(condition = constant(condition)))

    private fun setContent(vararg filters: DivInputFilter) {
        rule.setContent(
            configuration = configuration,
            data = data(input(filters = filters.toList(), textVariable = "text")),
        )
    }
}
