package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.views.DivViewContext
import com.yandex.div.compose.views.LocalDivViewContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.json.expressions.Expression
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class StateExtensionsComposeRuleTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val resolver = DivComposeExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    private val viewContext = DivViewContext(
        expressionResolver = resolver,
        reporter = reporter,
        variableAdapter = mock(),
        variableController = variableController
    )

    @Test
    fun observedValueChangesWhenVariableChanges() {
        variableController.declare(Variable.IntegerVariable("counter", 1))

        val expression = expression("@{counter}")
        var observedValue by mutableLongStateOf(0L)

        setContent {
            observedValue = expression.observeAsValue()
        }

        assertEquals(1L, observedValue)

        variableController.get("counter")?.set("2")
        composeRule.waitForIdle()

        assertEquals(2L, observedValue)
    }

    @Test
    fun observedValueHasDefaultValueIfExpressionIsNull() {
        val expression: Expression<Long>? = null
        var observedValue by mutableLongStateOf(0L)

        setContent {
            observedValue = expression.observeAsValue(10)
        }

        assertEquals(10L, observedValue)
    }

    @Test
    fun observedValueDoesNotChangeWhenItLeavesComposition() {
        variableController.declare(Variable.IntegerVariable("counter", 1))

        val expression = expression("@{counter}")
        var showExpression by mutableStateOf(true)
        var observedValue by mutableLongStateOf(0L)

        setContent {
            if (showExpression) {
                observedValue = expression.observeAsValue()
            }
        }

        assertEquals(1L, observedValue)

        showExpression = false
        variableController.get("counter")?.set("2")
        composeRule.waitForIdle()

        assertEquals(1L, observedValue)
    }

    @Test
    fun observedValueChangesWhenExpressionInstanceChanges() {
        variableController.declare(Variable.IntegerVariable("counter_a", 1))
        variableController.declare(Variable.IntegerVariable("counter_b", 10))

        val expressionA = expression("@{counter_a}")
        val expressionB = expression("@{counter_b}")
        var useExpressionB by mutableStateOf(false)
        var observedValue by mutableLongStateOf(0L)

        setContent {
            val expression = if (useExpressionB) expressionB else expressionA
            observedValue = expression.observeAsValue()
        }

        assertEquals(1L, observedValue)

        useExpressionB = true
        composeRule.waitForIdle()

        assertEquals(10L, observedValue)

        variableController.get("counter_a")?.set("2")
        variableController.get("counter_b")?.set("20")
        composeRule.waitForIdle()

        assertEquals(20L, observedValue)
    }

    private fun expression(rawExpression: String) = Expression.MutableExpression<Long, Long>(
        expressionKey = "test",
        rawExpression = rawExpression,
        validator = { true },
        converter = { it },
        logger = { fail(it.message) },
        typeHelper = TYPE_HELPER_INT,
    )

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalDivViewContext provides viewContext) {
                content()
            }
        }
    }
}
