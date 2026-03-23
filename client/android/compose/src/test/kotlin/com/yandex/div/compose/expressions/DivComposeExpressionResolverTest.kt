package com.yandex.div.compose.expressions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.expression
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivComposeExpressionResolverTest {

    private val variableController = DivVariableController()

    private val expressionResolver = DivComposeExpressionResolver(
        reporter = TestReporter(),
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
}
