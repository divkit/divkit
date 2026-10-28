package com.yandex.div.compose.container

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import org.json.JSONArray
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.Test

/**
 * A built item keeps its local state while its key stays the same: a resolved id within one
 * prototype, told apart by position when the id repeats, or the data and the index of an item
 * without an id.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class DivViewWithItemBuilderItemKeyTest(private val case: Case) {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `local state follows the item key`() {
        val elements = Variable.ArrayVariable("elements", case.elements)
        val configuration = DivConfiguration(
            reporter = TestReporter(),
            variableController = DivVariableController().apply { declare(elements) }
        )
        rule.setContent(configuration = configuration, data = data(editableItems(case.id)))
        rule.onNodeWithText(case.clicked).performClick()

        case.updatedElements?.let(elements::set)

        case.expected.forEach { rule.onNodeWithText(it).assertExists() }
    }

    class Case(
        private val name: String,
        val elements: JSONArray,
        val clicked: String,
        val expected: List<String>,
        val updatedElements: JSONArray? = null,
        val id: Expression<String>? = expression("@{it.getString('id')}"),
    ) {
        override fun toString() = name
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<Case> = cases

        // The runner calls the provider once per case, so the list is built once.
        private val cases: List<Case> = listOf(
            Case(
                name = "same id after an insertion before the item",
                elements = jsonArray(jsonObject("id" to "kept")),
                clicked = "initial kept 0",
                updatedElements = jsonArray(jsonObject("id" to "new"), jsonObject("id" to "kept")),
                expected = listOf("changed kept 1", "initial new 0")
            ),
            Case(
                name = "same id with changed data",
                elements = jsonArray(jsonObject("id" to "kept", "version" to 1)),
                clicked = "initial kept 0",
                updatedElements = jsonArray(jsonObject("id" to "kept", "version" to 2)),
                expected = listOf("changed kept 0")
            ),
            Case(
                name = "repeated id",
                elements = jsonArray(jsonObject("id" to "first"), jsonObject("id" to "second")),
                id = constant("repeated"),
                clicked = "initial first 0",
                expected = listOf("changed first 0", "initial second 1")
            ),
            Case(
                name = "repeated id next to a literal id with a position suffix",
                elements = jsonArray(
                    jsonObject("id" to "a"),
                    jsonObject("id" to "a"),
                    jsonObject("id" to "a#1")
                ),
                clicked = "initial a 1",
                expected = listOf("changed a 1", "initial a#1 2")
            ),
            Case(
                name = "no id, same data at another index",
                elements = jsonArray(jsonObject("id" to "kept")),
                id = null,
                clicked = "initial kept 0",
                updatedElements = jsonArray(jsonObject("id" to "new"), jsonObject("id" to "kept")),
                expected = listOf("initial kept 1")
            ),
            Case(
                name = "no id, other data at the same index",
                elements = jsonArray(jsonObject("id" to "kept")),
                id = null,
                clicked = "initial kept 0",
                updatedElements = jsonArray(jsonObject("id" to "kept", "version" to 2)),
                expected = listOf("initial kept 0")
            ),
        )
    }
}

/** Items showing `<local> <id> <index>`; a click sets the item's local variable to `changed`. */
internal fun editableItems(
    id: Expression<String>? = expression("@{it.getString('id')}"),
): Div = container(
    itemBuilder = itemBuilder(
        data = arrayExpression("@{elements}"),
        prototypes = listOf(
            prototype(
                id = id,
                div = text(
                    text = expression("@{local} @{it.getString('id')} @{index}"),
                    variables = listOf(variable("local", "initial")),
                    action = action(url = "div-action://set_variable?name=local&value=changed")
                )
            )
        )
    )
)
