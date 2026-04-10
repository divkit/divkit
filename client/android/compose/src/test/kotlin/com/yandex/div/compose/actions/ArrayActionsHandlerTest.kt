package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayInsertValueAction
import com.yandex.div.test.data.arrayRemoveValueAction
import com.yandex.div.test.data.arraySetValueAction
import com.yandex.div.test.data.typedValue
import com.yandex.div2.DivAction
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArrayActionsHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    private val variableController: DivVariableController
        get() = actionHandlerEnvironment.variableController

    @Before
    fun setUp() {
        actionHandlerEnvironment.init(
            arrayActionsHandler = ArrayActionsHandler(
                reporter = reporter
            )
        )
    }

    @Test
    fun `insert value without index`() {
        val variable = Variable.ArrayVariable("var", array("string value", 123))
        variableController.declare(variable)

        handle(
            action(
                typed = arrayInsertValueAction(
                    name = "var",
                    value = typedValue("new value")
                )
            )
        )

        assertArraysEqual(array("string value", 123, "new value"), variable)
    }

    @Test
    fun `insert value with index`() {
        val variable = Variable.ArrayVariable("var", array("string value", 123))
        variableController.declare(variable)

        handle(
            action(
                typed = arrayInsertValueAction(
                    name = "var",
                    index = 1,
                    value = typedValue(321)
                )
            )
        )

        assertArraysEqual(array("string value", 321, 123), variable)
    }

    @Test
    fun `insert array value`() {
        val variable = Variable.ArrayVariable("var", array("string value", 123))
        variableController.declare(variable)

        handle(
            action(
                typed = arrayInsertValueAction(
                    name = "var",
                    value = typedValue(array("new value", 321))
                )
            )
        )

        assertArraysEqual(
            array("string value", 123, array("new value", 321)),
            variable
        )
    }

    @Test
    fun `insert value into unknown variable`() {
        reporter.failOnError = false

        handle(
            action(typed = arrayInsertValueAction(name = "var", value = typedValue(321)))
        )

        assertEquals("Unknown variable: var", reporter.lastError)
    }

    @Test
    fun `insert value into invalid variable`() {
        reporter.failOnError = false

        val variable = Variable.StringVariable("var", "string value")
        variableController.declare(variable)

        handle(
            action(typed = arrayInsertValueAction(name = "var", value = typedValue(321)))
        )

        assertEquals("Variable is not an array variable: var", reporter.lastError)
    }

    @Test
    fun `insert value at invalid index`() {
        reporter.failOnError = false

        val variable = Variable.ArrayVariable("var", array("string value", 123))
        variableController.declare(variable)

        handle(
            action(typed = arrayInsertValueAction(
                name = "var",
                index = 10,
                value = typedValue(321))
            )
        )

        assertEquals("Unable to insert value into var: index (10) out of bounds", reporter.lastError)
    }

    @Test
    fun `remove value`() {
        val variable = Variable.ArrayVariable("var", array("string value", 123, true))
        variableController.declare(variable)

        handle(
            action(typed = arrayRemoveValueAction(name = "var", index = 1))
        )

        assertArraysEqual(array("string value", true), variable)
    }

    @Test
    fun `remove value at invalid index`() {
        reporter.failOnError = false

        val variable = Variable.ArrayVariable("var", array("string value", 123, true))
        variableController.declare(variable)

        handle(
            action(typed = arrayRemoveValueAction(name = "var", index = 10))
        )

        assertEquals("Unable to remove value from var: index (10) out of bounds", reporter.lastError)
    }

    @Test
    fun `set value`() {
        val variable = Variable.ArrayVariable("var", array("string value", 123, true))
        variableController.declare(variable)

        handle(
            action(
                typed = arraySetValueAction(
                    name = "var",
                    index = 1,
                    value = typedValue(array("new value", 321))
                )
            )
        )

        assertArraysEqual(
            array("string value", array("new value", 321), true),
            variable
        )
    }

    @Test
    fun `set value at invalid index`() {
        reporter.failOnError = false

        val variable = Variable.ArrayVariable("var", array("string value", 123, true))
        variableController.declare(variable)

        handle(
            action(
                typed = arraySetValueAction(
                    name = "var",
                    index = 10,
                    value = typedValue("new value")
                )
            )
        )

        assertEquals("Unable to set value in var: index (10) out of bounds", reporter.lastError)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}

private fun assertArraysEqual(expected: JSONArray, actual: Variable.ArrayVariable) {
    assertEquals(expected.toString(), actual.getValue().toString())
}

private fun array(vararg item: Any) = JSONArray(item)
