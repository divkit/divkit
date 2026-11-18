package com.yandex.div.compose.container

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div2.Div
import com.yandex.div2.DivState
import com.yandex.div2.DivTrigger.Mode
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithItemBuilderTriggerTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `item trigger fires once when changed data reenters composition`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("flag" to false)))
        val active = Variable.StringVariable("active", "list")
        val controller = DivVariableController().apply { declare(elements, active) }
        val handler = TestExternalActionHandler()
        val configuration = DivConfiguration(
            reporter = TestReporter(), variableController = controller, actionHandler = handler,
        )
        val content = container(itemBuilder = itemBuilder(
            data = arrayExpression("@{elements}"),
            prototypes = listOf(prototype(id = constant("item"), div = text(
                text = "item",
                triggers = listOf(trigger(
                    condition = "@{it.getBoolean('flag')}",
                    action = action(url = "test://item-updated"),
                    mode = Mode.ON_VARIABLE,
                )),
            ))),
        ))
        rule.setContent(configuration = configuration, data = data(switchable(content)))
        rule.waitForIdle()
        active.set("empty")
        rule.waitForIdle()
        elements.set(jsonArray(jsonObject("flag" to true)))

        active.set("list")
        rule.waitForIdle()

        assertEquals(
            listOf(actionData(source = DivActionSource.TRIGGER, url = "test://item-updated")),
            handler.handledActions,
        )
    }

    @Test
    fun `item data trigger calls host after composition is applied`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("flag" to false)))
        val controller = DivVariableController().apply { declare(elements) }
        var version by mutableIntStateOf(0)
        var appliedVersion = -1
        val actionVersions = mutableListOf<Int>()
        val configuration = DivConfiguration(
            reporter = TestReporter(),
            variableController = controller,
            // Records which composition was applied when the host is called.
            actionHandler = object : DivExternalActionHandler {
                override fun handle(context: DivActionHandlingContext, action: DivActionData) {
                    actionVersions.add(appliedVersion)
                }
            },
        )
        val card = data(container(itemBuilder = itemBuilder(
            data = arrayExpression("@{elements}"),
            prototypes = listOf(prototype(id = constant("item"), div = text(
                text = "item",
                triggers = listOf(trigger(
                    condition = "@{it.getBoolean('flag')}",
                    action = action(url = "test://item-updated"),
                )),
            ))),
        )))
        rule.setContentWithDivContext(configuration) {
            val composedVersion = version
            SideEffect { appliedVersion = composedVersion }
            DivView(card)
        }
        rule.waitForIdle()

        rule.runOnIdle {
            version = 1
            elements.set(jsonArray(jsonObject("flag" to true)))
        }
        rule.waitForIdle()

        assertEquals(listOf(1), actionVersions)
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
