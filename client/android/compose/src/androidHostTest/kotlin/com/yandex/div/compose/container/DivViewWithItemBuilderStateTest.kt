package com.yandex.div.compose.container

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivState
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivViewWithItemBuilderStateTest {
    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `changing the resolved item id resets local state`() {
        val id = Variable.StringVariable("id", "before")
        variableController.declare(
            id,
            Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "item")))
        )
        setContent(data(editableItems(id = expression("@{id}"))))
        rule.onNodeWithTag("before").performClick()
        rule.onNodeWithTag("before").assertTextEquals("changed item 0")

        id.set("after")

        rule.onNodeWithTag("after").assertTextEquals("initial item 0")
    }

    @Test
    fun `new prototype does not evaluate old prototype expressions on changed item data`() {
        val elements = Variable.ArrayVariable(
            "elements", jsonArray(jsonObject("kind" to "a", "a" to "First"))
        )
        variableController.declare(elements)
        setContent(data(container(itemBuilder = itemBuilder(
            data = arrayExpression("@{elements}"),
            prototypes = listOf(
                prototype(
                    id = constant("item"),
                    selector = booleanExpression("@{it.getString('kind') == 'a'}"),
                    div = text(text = expression("@{it.getString('a')}"))
                ),
                prototype(
                    id = constant("item"),
                    div = text(text = expression("@{it.getString('b')}"))
                )
            )
        ))))
        rule.onNodeWithTag("item").assertTextEquals("First")

        elements.set(jsonArray(jsonObject("kind" to "b", "b" to "Second")))

        rule.onNodeWithTag("item").assertTextEquals("Second")
    }

    @Test
    fun `nested selector observes updated outer item data with stable id`() {
        val elements = Variable.ArrayVariable(
            "elements", jsonArray(jsonObject("choice" to "first"))
        )
        variableController.declare(elements)
        val nested = container(itemBuilder = itemBuilder(
            data = jsonArray(jsonObject()),
            prototypes = listOf(
                prototype(
                    selector = booleanExpression("@{outer.getString('choice') == 'first'}"),
                    div = text(text = "First")
                ),
                prototype(div = text(text = "Second"))
            )
        ))
        setContent(data(container(itemBuilder = itemBuilder(
            data = arrayExpression("@{elements}"),
            dataElementName = "outer",
            prototypes = listOf(prototype(id = constant("outer"), div = nested))
        ))))
        rule.onNodeWithText("First").assertExists()

        elements.set(jsonArray(jsonObject("choice" to "second")))

        rule.onNodeWithText("Second").assertExists()
    }

    @Test
    fun `item local variable survives leaving and re-entering composition`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "kept")))
        val active = Variable.StringVariable("active", "list")
        variableController.declare(elements, active)
        setContent(data(switchable(editableItems())))
        rule.onNodeWithTag("kept").performClick()
        active.set("empty")
        rule.onNodeWithText("empty").assertExists()

        active.set("list")

        rule.onNodeWithTag("kept").assertTextEquals("changed kept 0")
    }

    private fun setContent(card: DivData) {
        rule.setContent(configuration = configuration, data = card)
    }
}

private fun switchable(content: Div): Div = state(
    id = "switch",
    stateIdVariable = "active",
    states = listOf(
        DivState.State(div = content, stateId = "list"),
        DivState.State(div = text(text = "empty"), stateId = "empty")
    )
)
