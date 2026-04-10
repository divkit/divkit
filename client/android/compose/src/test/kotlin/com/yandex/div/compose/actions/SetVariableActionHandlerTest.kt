package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.color
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.typedColorValue
import com.yandex.div.test.data.typedValue
import com.yandex.div2.DivAction
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetVariableActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    private val variableController: DivVariableController
        get() = actionHandlerEnvironment.variableController

    @Before
    fun setUp() {
        actionHandlerEnvironment.init(
            setVariableActionHandler = SetVariableActionHandler(
                reporter = reporter
            )
        )
    }

    @Test
    fun `set string variable`() {
        val variable = Variable.StringVariable("var", "value")
        variableController.declare(variable)

        handle(
            action(typed = setVariableAction("var", typedValue("new value")))
        )

        assertEquals("new value", variable.getValue())
    }

    @Test
    fun `set integer variable`() {
        val variable = Variable.IntegerVariable("counter", 10)
        variableController.declare(variable)

        handle(
            action(typed = setVariableAction("counter", typedValue(20)))
        )

        assertEquals(20L, variable.getValue())
    }

    @Test
    fun `set color variable`() {
        val variable = Variable.ColorVariable("color", 0xAABBCC)
        variableController.declare(variable)

        handle(
            action(typed = setVariableAction("color", typedColorValue(0xBBCCDDEE)))
        )

        assertEquals(color(0xBBCCDDEE), variable.getValue())
    }

    @Test
    fun `set array variable`() {
        val variable = Variable.ArrayVariable("var", JSONArray())
        variableController.declare(variable)

        val newValue = JSONArray().apply {
            put("value")
            put(123)
        }
        handle(
            action(typed = setVariableAction("var", typedValue(newValue)))
        )

        assertEquals(newValue, variable.getValue())
    }

    @Test
    fun `invalid integer value`() {
        reporter.failOnError = false

        val variable = Variable.IntegerVariable("var", 123)
        variableController.declare(variable)

        handle(
            action(typed = setVariableAction("var", typedValue("invalid value")))
        )

        assertEquals(123L, variable.getValue())
        assertEquals(
            "Trying to set value with invalid type (string) to variable var",
            reporter.lastError
        )
    }

    @Test
    fun `div-action with string value`() {
        val variable = Variable.StringVariable("var", "value")
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=var&value=new value"))

        assertEquals("new value", variable.getValue())
    }

    @Test
    fun `div-action with boolean value (true)`() {
        val variable = Variable.BooleanVariable("var", false)
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=var&value=true"))

        assertEquals(true, variable.getValue())
    }

    @Test
    fun `div-action with boolean value (1)`() {
        val variable = Variable.BooleanVariable("var", false)
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=var&value=1"))

        assertEquals(true, variable.getValue())
    }

    @Test
    fun `div-action with invalid boolean value`() {
        reporter.failOnError = false

        val variable = Variable.BooleanVariable("var", false)
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=var&value=3"))

        assertEquals(false, variable.getValue())
        assertEquals("Failed to convert value to Boolean: 3", reporter.lastError)
    }

    @Test
    fun `div-action with double value`() {
        val variable = Variable.DoubleVariable("counter", 12.3)
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=counter&value=123.45"))

        assertEquals(123.45, variable.getValue())
    }

    @Test
    fun `div-action with color value`() {
        val variable = Variable.ColorVariable("color", 0xAABBCC)
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=color&value=%23BBCCDDEE"))

        assertEquals(color(0xBBCCDDEE), variable.getValue())
    }

    @Test
    fun `div-action with invalid integer value`() {
        reporter.failOnError = false

        val variable = Variable.IntegerVariable("var", 123)
        variableController.declare(variable)

        handle(action(url = "div-action://set_variable?name=var&value=invalid"))

        assertEquals(123L, variable.getValue())
        assertEquals("Failed to convert value to Long: invalid", reporter.lastError)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}
