package com.yandex.div.compose.expressions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.createExpressionResolver
import com.yandex.div.compose.variables.DivPropertyVariableExecutor
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.internal.variables.toVariable
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.property
import com.yandex.div.test.data.throwingErrorLogger
import com.yandex.div2.DivEvaluableType
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class DivComposeExpressionResolverTest {
    private val storedValueProvider = mock<ScopedStoredValueProvider>()
    private val variableController = DivVariableController()

    private val expressionResolver = createExpressionResolver(
        storedValueProvider = storedValueProvider,
        variableController = variableController
    )

    private val propertyVariableExecutor = DivPropertyVariableExecutor(
        actionHandler = mock(),
        actionHandlingContext = mock(),
        expressionResolver = expressionResolver
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
    fun `expression with property`() {
        variableController.declare(Variable.IntegerVariable("counter", 21))

        val property = property(
            name = "counterX2",
            valueType = DivEvaluableType.INTEGER,
            get = "@{counter * 2}"
        ).toVariable(
            resolver = expressionResolver,
            propertyVariableExecutor = propertyVariableExecutor,
            logger = throwingErrorLogger
        )
        variableController.declare(assertNotNull(property))

        assertEquals("value = 42", evaluate("value = @{counterX2}"))
    }

    @Test
    fun `expression with getStoredStringValue() function`() {
        whenever(storedValueProvider.get(name = eq("value"))) doReturn "stored value"

        assertEquals(
            "value = stored value",
            evaluate("value = @{getStoredStringValue('value', 'fallback')}")
        )
    }

    @Test
    fun `evaluate() returns new value when variable changes`() {
        val value = Variable.StringVariable("value", "initial value")
        variableController.declare(value)

        assertEquals("initial value", evaluate("@{value}"))

        value.set("new value")

        assertEquals("new value", evaluate("@{value}"))
    }

    @Test
    fun `observed value changes when variable changes`() {
        val variable = Variable.IntegerVariable("value", 10)
        variableController.declare(variable)

        var value: String? = null
        expression("value = @{value}").observeAndGet(expressionResolver) { value = it }

        assertEquals("value = 10", value)

        variable.set(20)

        assertEquals("value = 20", value)
    }

    @Test
    fun `observed value does not change when subscription is closed`() {
        val variable = Variable.IntegerVariable("value", 10)
        variableController.declare(variable)

        var value: String? = null
        val subscription = expression("value = @{value}")
            .observeAndGet(expressionResolver) { value = it }

        assertEquals("value = 10", value)

        subscription.close()
        variable.set(20)

        assertEquals("value = 10", value)
    }

    @Test
    fun `withPropertyNewValueVariable() shadows variable`() {
        variableController.declare(Variable.StringVariable("value", "initial value"))

        val expression = expression("@{value}")
        val expressionResolverWithNewValue = expressionResolver
            .withPropertyNewValueVariable(variableName = "value", value = "new value")

        assertEquals("new value", expression.evaluate(expressionResolverWithNewValue))
        assertEquals("initial value", expression.evaluate(expressionResolver))
    }

    private fun evaluate(expression: String): String {
        return expression(expression).evaluate(expressionResolver)
    }
}
