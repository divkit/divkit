package com.yandex.div.compose.expressions

import com.yandex.div.compose.DivReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.json.expressions.Expression
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivComposeExpressionResolverTest {

    private val variableController = DivVariableController()

    private val reporter = object : DivReporter() {
        override fun reportError(message: String) {
            fail(message)
        }

        override fun reportError(message: String, e: Throwable) {
            fail(message)
        }
    }

    private val expressionResolver = DivComposeExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    @Test
    fun `expression with string function`() {
        assertEquals(
            "length = 9",
            evaluate("length = @{len('123456789')}")
        )
    }

    @Test
    fun `expression with string variables`() {
        variableController.declare(Variable.StringVariable("host", "test.abc"))
        variableController.declare(Variable.StringVariable("path", "path"))

        assertEquals(
            "https://test.abc/path",
            evaluate("https://@{host}/@{path}")
        )
    }

    @Test
    fun `expression with integer variable`() {
        variableController.declare(Variable.IntegerVariable("value", 100))

        assertEquals(
            "value + 10 = 110",
            evaluate("value + 10 = @{value + 10}")
        )
    }

    @Test
    fun `expression with double variable`() {
        variableController.declare(Variable.DoubleVariable("value", 123.45))

        assertEquals(
            "value + 10 = 133.45",
            evaluate("value + 10 = @{value + 10}")
        )
    }

    @Test
    fun `observed value changes when variable changes`() {
        variableController.declare(Variable.IntegerVariable("value", 10))

        var value: String? = null
        expression("value = @{value}").observeAndGet(expressionResolver) { value = it }

        assertEquals("value = 10", value)

        variableController.get("value")?.set("20")

        assertEquals("value = 20", value)
    }

    @Test
    fun `observed value does not change when subscription is closed`() {
        variableController.declare(Variable.IntegerVariable("value", 10))

        var value: String? = null
        val subscription = expression("value = @{value}")
            .observeAndGet(expressionResolver) { value = it }

        assertEquals("value = 10", value)

        subscription.close()
        variableController.get("value")?.set("20")

        assertEquals("value = 10", value)
    }

    private fun evaluate(expression: String): String {
        return expression(expression).evaluate(expressionResolver)
    }

    private fun expression(expression: String): Expression<String> {
        return Expression.MutableExpression<String, String>(
            expressionKey = "test",
            rawExpression = expression,
            validator = { true },
            converter = { it },
            logger = { fail(it.message) },
            typeHelper = TYPE_HELPER_STRING,
        )
    }
}
