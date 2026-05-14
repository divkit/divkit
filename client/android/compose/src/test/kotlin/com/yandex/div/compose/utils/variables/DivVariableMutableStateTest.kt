package com.yandex.div.compose.utils.variables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivVariableMutableStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val variableController = DivVariableController()
    private val reporter = TestReporter()
    private val localComponent = mockLocalComponent(reporter, variableController)

    @Test
    fun `string state has initial variable value`() {
        variableController.declare(Variable.StringVariable("input", "hello"))
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromStringVariable("input")
        }

        assertEquals("hello", state?.value)
    }

    @Test
    fun `string state is null when variable not found`() {
        reporter.failOnError = false
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromStringVariable("nonexistent")
        }

        assertNull(state)
    }

    @Test
    fun `string state reports error when variable not found`() {
        reporter.failOnError = false

        setContent {
            mutableStateFromStringVariable("missing_var")
        }

        assertEquals("variable [missing_var] not found", reporter.lastError)
    }

    @Test
    fun `string state is null for non-string variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.IntegerVariable("count", 42))
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromStringVariable("count")
        }

        assertNull(state)
    }

    @Test
    fun `string state reports error for non-string variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.BooleanVariable("flag", true))

        setContent {
            mutableStateFromStringVariable("flag")
        }

        assertEquals("variable [flag] is not a string variable", reporter.lastError)
    }

    @Test
    fun `string state does not report error when variable exists`() {
        variableController.declare(Variable.StringVariable("input", "value"))

        setContent {
            mutableStateFromStringVariable("input")
        }

        assertNull(reporter.lastError)
    }

    @Test
    fun `string state assignment updates state and variable`() {
        val variable = Variable.StringVariable("input", "initial")
        variableController.declare(variable)
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromStringVariable("input")
        }

        state?.let { it.value = "updated" }
        composeRule.waitForIdle()

        assertEquals("updated", state?.value)
        assertEquals("updated", variable.getValue())
    }

    @Test
    fun `string state updates on variable change`() {
        val variable = Variable.StringVariable("input", "first")
        variableController.declare(variable)
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromStringVariable("input")
        }

        variable.set("second")
        composeRule.waitForIdle()

        assertEquals("second", state?.value)
    }

    @Test
    fun `string state same value does not trigger recomposition`() {
        val variable = Variable.StringVariable("input", "same")
        variableController.declare(variable)
        var recomposeCount = 0

        setContent {
            mutableStateFromStringVariable("input")
            recomposeCount++
        }

        val countAfterInit = recomposeCount

        variable.set("same")
        composeRule.waitForIdle()

        assertEquals(countAfterInit, recomposeCount)
    }

    @Test
    fun `string state stops observing when leaving composition`() {
        val variable = Variable.StringVariable("input", "initial")
        variableController.declare(variable)
        var show by mutableStateOf(true)
        var state: MutableState<String>? = null

        setContent {
            if (show) {
                state = mutableStateFromStringVariable("input")
            }
        }

        assertEquals("initial", state?.value)

        show = false
        composeRule.waitForIdle()

        variable.set("after_dispose")
        composeRule.waitForIdle()

        assertEquals("initial", state?.value)
    }

    @Test
    fun `boolean state has initial variable value`() {
        variableController.declare(Variable.BooleanVariable("flag", true))
        var state: MutableState<Boolean>? = null

        setContent {
            state = mutableStateFromBooleanVariable("flag")
        }

        assertEquals(true, state?.value)
    }

    @Test
    fun `boolean state has false initial value`() {
        variableController.declare(Variable.BooleanVariable("flag", false))
        var state: MutableState<Boolean>? = null

        setContent {
            state = mutableStateFromBooleanVariable("flag")
        }

        assertEquals(false, state?.value)
    }

    @Test
    fun `boolean state is null when variable not found`() {
        reporter.failOnError = false
        var state: MutableState<Boolean>? = null

        setContent {
            state = mutableStateFromBooleanVariable("nonexistent")
        }

        assertNull(state)
    }

    @Test
    fun `boolean state assignment updates variable`() {
        val variable = Variable.BooleanVariable("flag", false)
        variableController.declare(variable)
        var state: MutableState<Boolean>? = null

        setContent {
            state = mutableStateFromBooleanVariable("flag")
        }

        state?.let { it.value = true }
        composeRule.waitForIdle()

        assertEquals(true, variable.getValue())
        assertEquals(true, state?.value)
    }

    @Test
    fun `boolean state updates on external change`() {
        val variable = Variable.BooleanVariable("flag", false)
        variableController.declare(variable)
        var state: MutableState<Boolean>? = null

        setContent {
            state = mutableStateFromBooleanVariable("flag")
        }

        variable.set(true)
        composeRule.waitForIdle()

        assertEquals(true, state?.value)
    }

    @Test
    fun `boolean state reports error for non-boolean variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.StringVariable("flag", "flag"))

        setContent {
            mutableStateFromBooleanVariable("flag")
        }

        assertEquals("variable [flag] is not a boolean variable", reporter.lastError)
    }

    @Test
    fun `string state is null for boolean variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.BooleanVariable("flag", true))
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromStringVariable("flag")
        }

        assertNull(state)
        assertEquals("variable [flag] is not a string variable", reporter.lastError)
    }

    @Test
    fun `string state default`() {
        reporter.failOnError = false
        var state: MutableState<String>? = null

        setContent {
            state = mutableStateFromVariable("flag", "default")
        }

        assertEquals(state?.value, "default")
        assertEquals("variable [flag] not found", reporter.lastError)
    }

    @Test
    fun `boolean state default`() {
        reporter.failOnError = false
        var state: MutableState<Boolean>? = null

        setContent {
            state = mutableStateFromVariable("flag", true)
        }

        assertEquals(state?.value, true)
        assertEquals("variable [flag] not found", reporter.lastError)
    }

    @Test
    fun `integer state has initial variable value`() {
        variableController.declare(Variable.IntegerVariable("count", 42))
        var state: MutableState<Long>? = null

        setContent {
            state = mutableStateFromIntegerVariable("count")
        }

        assertEquals(42L, state?.value)
    }

    @Test
    fun `integer state is null when variable not found`() {
        reporter.failOnError = false
        var state: MutableState<Long>? = null

        setContent {
            state = mutableStateFromIntegerVariable("nonexistent")
        }

        assertNull(state)
    }

    @Test
    fun `integer state assignment updates variable`() {
        val variable = Variable.IntegerVariable("count", 0)
        variableController.declare(variable)
        var state: MutableState<Long>? = null

        setContent {
            state = mutableStateFromIntegerVariable("count")
        }

        state?.let { it.value = 100 }
        composeRule.waitForIdle()

        assertEquals(100L, variable.getValue())
        assertEquals(100L, state?.value)
    }

    @Test
    fun `integer state updates on external change`() {
        val variable = Variable.IntegerVariable("count", 0)
        variableController.declare(variable)
        var state: MutableState<Long>? = null

        setContent {
            state = mutableStateFromIntegerVariable("count")
        }

        variable.set(7)
        composeRule.waitForIdle()

        assertEquals(7L, state?.value)
    }

    @Test
    fun `integer state reports error for non-integer variable`() {
        reporter.failOnError = false
        variableController.declare(Variable.StringVariable("count", "value"))

        setContent {
            mutableStateFromIntegerVariable("count")
        }

        assertEquals("variable [count] is not an integer variable", reporter.lastError)
    }

    @Test
    fun `integer state default`() {
        reporter.failOnError = false
        var state: MutableState<Long>? = null

        setContent {
            state = mutableStateFromVariable("count", 5L)
        }

        assertEquals(5L, state?.value)
        assertEquals("variable [count] not found", reporter.lastError)
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }
}
