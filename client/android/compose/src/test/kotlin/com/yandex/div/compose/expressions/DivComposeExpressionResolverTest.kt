package com.yandex.div.compose.expressions

import com.yandex.div.compose.DivReporter
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TypeHelper
import com.yandex.div.json.expressions.Expression
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivComposeExpressionResolverTest {

    private val variables = mutableMapOf<String, Variable>()

    private val evaluationContext = EvaluationContext(
        variableProvider = { variables[it]?.getValue() },
        storedValueProvider = mock(),
        functionProvider = GeneratedBuiltinFunctionProvider,
        warningSender = { _, _ -> }
    )

    private val reporter = object : DivReporter() {
        override fun reportError(message: String) {
            fail(message)
        }

        override fun reportError(message: String, e: Throwable) {
            fail(message)
        }
    }

    private val expressionResolver = DivComposeExpressionResolver(
        Evaluator(evaluationContext),
        reporter
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
        variables["host"] = Variable.StringVariable("host", "test.abc")
        variables["path"] = Variable.StringVariable("path", "path")

        assertEquals(
            "https://test.abc/path",
            evaluate("https://@{host}/@{path}")
        )
    }

    @Test
    fun `expression with integer variable`() {
        variables["value"] = Variable.IntegerVariable("value", 100)

        assertEquals(
            "value + 10 = 110",
            evaluate("value + 10 = @{value + 10}")
        )
    }

    @Test
    fun `expression with double variable`() {
        variables["value"] = Variable.DoubleVariable("value", 123.45)

        assertEquals(
            "value + 10 = 133.45",
            evaluate("value + 10 = @{value + 10}")
        )
    }

    private fun evaluate(expression: String): String {
        return expression(expression, TYPE_HELPER_STRING).evaluate(expressionResolver)
    }

    private fun <T : Any> expression(
        rawExpression: String,
        typeHelper: TypeHelper<T>,
        validator: (T) -> Boolean = { true },
    ) = Expression.MutableExpression<T, T>(
        expressionKey = "test",
        rawExpression = rawExpression,
        validator = validator,
        converter = { it },
        logger = { fail(it.message) },
        typeHelper = typeHelper,
    )
}
