package com.yandex.div.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithTriggersTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `initially gone child without local variables becomes visible from its trigger`() {
        val condition = Variable.BooleanVariable("condition", false)
        val hidden = Variable.BooleanVariable("hidden", true)
        variableController.declare(condition, hidden)
        setContent(
            container(
                items = listOf(
                    text(
                        text = "item",
                        visibility = visibilityExpression("@{hidden ? 'gone' : 'visible'}"),
                        triggers = listOf(
                            trigger(
                                action = action(
                                    url = "div-action://set_variable?name=hidden&value=false"
                                ),
                                condition = "@{condition}"
                            )
                        )
                    )
                )
            )
        )

        condition.set(true)

        rule.onNodeWithText("item").assertIsDisplayed()
    }

    @Test
    fun `static child trigger resumes once after returning to composition`() {
        val condition = Variable.BooleanVariable("condition", false)
        val counter = Variable.IntegerVariable("counter", 0)
        variableController.declare(condition, counter)
        val child = text(
            text = "item",
            triggers = listOf(
                trigger(
                    condition = "@{condition}",
                    action = action(
                        typed = setVariableAction(
                            name = "counter",
                            value = typedValue(intExpression("@{counter + 1}"))
                        )
                    )
                )
            )
        )
        val card = data(container(items = listOf(child)))
        var shown by mutableStateOf(true)
        rule.setContentWithDivContext(configuration) {
            if (shown) DivView(data = card)
        }
        rule.runOnIdle { shown = false }
        rule.runOnIdle { shown = true }

        rule.runOnIdle { condition.set(true) }

        assertEquals(1L, counter.getValue())
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}
