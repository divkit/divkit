package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.dictSetValueAction
import com.yandex.div.test.data.typedValue
import com.yandex.div2.DivAction
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictSetValueActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    private val variableController: DivVariableController
        get() = actionHandlerEnvironment.variableController

    @Before
    fun setUp() {
        actionHandlerEnvironment.init(
            dictSetValueActionHandler = DictSetValueActionHandler(
                reporter = reporter
            )
        )
    }

    @Test
    fun `update key`() {
        val variable = Variable.DictVariable("var", dict("key" to "value"))
        variableController.declare(variable)

        handle(
            action(
                typed = dictSetValueAction(
                    name = "var",
                    key = "key",
                    value = typedValue("new value")
                )
            )
        )

        assertDictsEqual(dict("key" to "new value"), variable)
    }

    @Test
    fun `update key with value of another type`() {
        val variable = Variable.DictVariable("var", dict("key" to "value"))
        variableController.declare(variable)

        handle(
            action(
                typed = dictSetValueAction(
                    name = "var",
                    key = "key",
                    value = typedValue(123)
                )
            )
        )

        assertDictsEqual(dict("key" to 123), variable)
    }

    @Test
    fun `add new key`() {
        val variable = Variable.DictVariable("var", dict("key" to "value"))
        variableController.declare(variable)

        handle(
            action(
                typed = dictSetValueAction(
                    name = "var",
                    key = "new key",
                    value = typedValue("new value")
                )
            )
        )

        assertDictsEqual(dict("key" to "value", "new key" to "new value"), variable)
    }

    @Test
    fun `remove key when value is null`() {
        val variable = Variable.DictVariable("var", dict("key1" to "value1", "key2" to "value2"))
        variableController.declare(variable)

        handle(
            action(typed = dictSetValueAction(name = "var", key = "key1"))
        )

        assertDictsEqual(dict("key2" to "value2"), variable)
    }

    @Test
    fun `set value on unknown variable`() {
        reporter.failOnError = false

        handle(
            action(typed = dictSetValueAction(name = "var", key = "key"))
        )

        assertEquals("Unknown variable: var", reporter.lastError)
    }

    @Test
    fun `set value on non-dict variable`() {
        reporter.failOnError = false

        val variable = Variable.StringVariable("var", "string value")
        variableController.declare(variable)

        handle(
            action(typed = dictSetValueAction(name = "var", key = "key"))
        )

        assertEquals("Variable is not a dict variable: var", reporter.lastError)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}

private fun assertDictsEqual(expected: JSONObject, actual: Variable.DictVariable) {
    assertEquals(expected.toString(), actual.getValue().toString())
}

private fun dict(vararg pairs: Pair<String, Any>): JSONObject {
    return JSONObject().apply {
        pairs.forEach { (key, value) -> put(key, value) }
    }
}
