package com.yandex.div.compose.actions

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.updateStructureAction
import com.yandex.div2.DivAction
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateStructureActionHandlerTest {
    private val actionHandlerEnvironment = ActionHandlerEnvironment()

    private val reporter: TestReporter
        get() = actionHandlerEnvironment.reporter

    private val variableController: DivVariableController
        get() = actionHandlerEnvironment.variableController

    @Before
    fun setUp() {
        actionHandlerEnvironment.init(
            updateStructureActionHandler = UpdateStructureActionHandler(
                reporter = reporter
            )
        )
    }

    @Test
    fun `update dict top-level value`() {
        val variable = Variable.DictVariable("var", dict("key1" to "value1", "key2" to "value2"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "key1",
                    value = typedValue("new value")
                )
            )
        )

        assertDictsEqual(dict("key1" to "new value", "key2" to "value2"), variable)
    }

    @Test
    fun `update nested dict value`() {
        val variable = Variable.DictVariable("var", dict("key" to dict("inner_key" to "value")))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "key/inner_key",
                    value = typedValue("new value")
                )
            )
        )

        assertDictsEqual(dict("key" to dict("inner_key" to "new value")), variable)
    }

    @Test
    fun `update array element`() {
        val variable = Variable.ArrayVariable("var", array("a", "b", "c"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "1",
                    value = typedValue("new value")
                )
            )
        )

        assertArraysEqual(array("a", "new value", "c"), variable)
    }

    @Test
    fun `update nested array element`() {
        val variable = Variable.ArrayVariable("var", array(array("value 1", 2), array(3, 4)))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "0/1",
                    value = typedValue("new value")
                )
            )
        )

        assertArraysEqual(array(array("value 1", "new value"), array(3, 4)), variable)
    }

    @Test
    fun `update dict inside array`() {
        val variable = Variable.ArrayVariable("var", array(dict("key" to "value")))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "0/key",
                    value = typedValue("new value")
                )
            )
        )

        assertArraysEqual(array(dict("key" to "new value")), variable)
    }

    @Test
    fun `update array inside dict`() {
        val variable = Variable.DictVariable("var", dict("items" to array("value 1", 2)))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "items/1",
                    value = typedValue("new value")
                )
            )
        )

        assertDictsEqual(dict("items" to array("value 1", "new value")), variable)
    }

    @Test
    fun `error on unknown variable`() {
        reporter.failOnError = false

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "key",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Unknown variable: var", reporter.lastError)
    }

    @Test
    fun `error on non-structure variable`() {
        reporter.failOnError = false

        val variable = Variable.StringVariable("var", "string value")
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "key",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Action requires array or dictionary variable", reporter.lastError)
    }

    @Test
    fun `error on malformed path`() {
        reporter.failOnError = false

        val variable = Variable.DictVariable("var", dict("key" to "value"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "///",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Malformed path '///': all path segments are empty", reporter.lastError)
    }

    @Test
    fun `error on path element not found`() {
        reporter.failOnError = false

        val variable = Variable.DictVariable("var", dict("key" to "value"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "missing/nested",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Element with path 'missing' is not found", reporter.lastError)
    }

    @Test
    fun `error on non-structure path element`() {
        reporter.failOnError = false

        val variable = Variable.DictVariable("var", dict("key" to "value"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "key/nested",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Element with path 'key' is not a structure", reporter.lastError)
    }

    @Test
    fun `error on invalid array index`() {
        reporter.failOnError = false

        val variable = Variable.ArrayVariable("var", array("a", "b"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "abc/0",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Unable to use 'abc' as array index", reporter.lastError)
    }

    @Test
    fun `error on out of bounds array index`() {
        reporter.failOnError = false

        val variable = Variable.ArrayVariable("var", array("a", "b"))
        variableController.declare(variable)

        handle(
            action(
                typed = updateStructureAction(
                    name = "var",
                    path = "99",
                    value = typedValue("value")
                )
            )
        )

        assertEquals("Position '99' is out of array bounds", reporter.lastError)
    }

    private fun handle(action: DivAction) = actionHandlerEnvironment.handle(action)
}

private fun assertArraysEqual(expected: JSONArray, actual: Variable.ArrayVariable) {
    assertEquals(expected.toString(), actual.getValue().toString())
}

private fun assertDictsEqual(expected: JSONObject, actual: Variable.DictVariable) {
    assertEquals(expected.toString(), actual.getValue().toString())
}

private fun array(vararg items: Any) = JSONArray(items)

private fun dict(vararg pairs: Pair<String, Any>): JSONObject {
    return JSONObject().apply {
        pairs.forEach { (key, value) -> put(key, value) }
    }
}
