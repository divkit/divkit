package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.intExpression
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpressionUtilsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val variableController = DivVariableController()
    private val localComponent = mockLocalComponent(variableController = variableController)

    @Test
    fun `observed value changes when variable changes`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val expression = intExpression("@{counter}")
        var observedValue by mutableLongStateOf(0L)

        setContent {
            observedValue = expression.observedValue()
        }

        assertEquals(1L, observedValue)

        variable.set(2)
        composeRule.waitForIdle()

        assertEquals(2L, observedValue)
    }

    @Test
    fun `observed value has default value if expression is null`() {
        val expression: Expression<Long>? = null
        var observedValue by mutableLongStateOf(0L)

        setContent {
            observedValue = expression.observedValue(10)
        }

        assertEquals(10L, observedValue)
    }

    @Test
    fun `observed value does not change when it leaves composition`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

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
        variable.set(2)
        composeRule.waitForIdle()

        assertEquals(1L, observedValue)
    }

    @Test
    fun `observed value resolves constant`() {
        val expression = Expression.constant("Hello, world!")
        var observedValue by mutableStateOf("")

        setContent {
            observedValue = expression.observedValue()
        }

        assertEquals("Hello, world!", observedValue)
    }

    @Test
    fun `observed value resolves constant with slashes`() {
        val expression = Expression.constant("Hello, world! \\\\")
        var observedValue by mutableStateOf("")

        setContent {
            observedValue = expression.observedValue()
        }

        assertEquals("Hello, world! \\", observedValue)
    }

    @Test
    fun `observed value changes when expression instance changes`() {
        val counterA = Variable.IntegerVariable("counter_a", 1)
        variableController.declare(counterA)

        val counterB = Variable.IntegerVariable("counter_b", 10)
        variableController.declare(counterB)

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

        counterA.set(2)
        counterB.set(20)
        composeRule.waitForIdle()

        assertEquals(20L, observedValue)
    }

    @Test
    fun `recompose once when variable changes`() {
        val variable = Variable.IntegerVariable("counter", 1)
        variableController.declare(variable)

        val expression = intExpression("@{counter}")
        var recompositionCount = 0
        var observedValue1 by mutableLongStateOf(0L)
        var observedValue2 by mutableLongStateOf(0L)

        setContent {
            observedValue1 = expression.observedValue()
            observedValue2 = expression.observedValue()
            SideEffect { recompositionCount++ }
        }

        assertEquals(1, recompositionCount)
        assertEquals(1L, observedValue1)
        assertEquals(1L, observedValue2)

        variable.set(20)
        composeRule.waitForIdle()

        assertEquals(2, recompositionCount)
        assertEquals(20L, observedValue1)
        assertEquals(20L, observedValue2)
    }

    @Test
    fun `recompose once when multiple variables change`() {
        val variable1 = Variable.IntegerVariable("counter1", 1)
        val variable2 = Variable.IntegerVariable("counter2", 1)
        variableController.declare(variable1, variable2)

        val expression1 = intExpression("@{counter1}")
        val expression2 = intExpression("@{counter2}")
        var recompositionCount = 0
        var observedValue1 by mutableLongStateOf(0L)
        var observedValue2 by mutableLongStateOf(0L)

        setContent {
            observedValue1 = expression1.observedValue()
            observedValue2 = expression2.observedValue()
            SideEffect { recompositionCount++ }
        }

        assertEquals(1, recompositionCount)
        assertEquals(1L, observedValue1)
        assertEquals(1L, observedValue2)

        variable1.set(20)
        variable2.set(30)
        composeRule.waitForIdle()

        assertEquals(2, recompositionCount)
        assertEquals(20L, observedValue1)
        assertEquals(30L, observedValue2)
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
