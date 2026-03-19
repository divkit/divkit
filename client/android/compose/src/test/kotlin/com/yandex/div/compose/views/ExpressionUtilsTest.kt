package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.intExpression
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class ExpressionUtilsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val resolver = DivComposeExpressionResolver(
        reporter = reporter,
        variableController = variableController
    )

    private val localContext = DivLocalContext(
        actionHandlingContext = mock(),
        expressionResolver = resolver,
        variableController = variableController
    )

    @Test
    fun observedValueChangesWhenVariableChanges() {
        variableController.declare(Variable.IntegerVariable("counter", 1))

        val expression = intExpression("@{counter}")
        var observedValue by mutableLongStateOf(0L)

        setContent {
            observedValue = expression.observedValue()
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
            observedValue = expression.observedValue(10)
        }

        assertEquals(10L, observedValue)
    }

    @Test
    fun observedValueDoesNotChangeWhenItLeavesComposition() {
        variableController.declare(Variable.IntegerVariable("counter", 1))

        val expression = intExpression("@{counter}")
        var showExpression by mutableStateOf(true)
        var observedValue by mutableLongStateOf(0L)

        setContent {
            if (showExpression) {
                observedValue = expression.observedValue()
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

        val expressionA = intExpression("@{counter_a}")
        val expressionB = intExpression("@{counter_b}")
        var useExpressionB by mutableStateOf(false)
        var observedValue by mutableLongStateOf(0L)

        setContent {
            val expression = if (useExpressionB) expressionB else expressionA
            observedValue = expression.observedValue()
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

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalDivContext provides localContext) {
                content()
            }
        }
    }
}
