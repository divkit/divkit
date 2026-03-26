package com.yandex.div.compose

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithTriggersTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `text changes when trigger is triggered`() {
        val condition = Variable.BooleanVariable("condition", false)
        variableController.declare(condition)

        setContent(
            text(
                id = "title",
                text = expression("@{text}")
            ),
            triggers = listOf(
                trigger(
                    action = action(url = "div-action://set_variable?name=text&value=new text"),
                    condition = "@{condition}"
                )
            ),
            variables = listOf(variable("text", "initial text"))
        )

        rule.onNodeWithTag("title").assertTextEquals("initial text")

        condition.set(true)

        rule.onNodeWithTag("title").assertTextEquals("new text")
    }

    @Test
    fun `text changes when local trigger is triggered`() {
        val condition = Variable.BooleanVariable("condition", false)
        variableController.declare(condition)

        setContent(
            text(
                id = "title",
                text = expression("@{text}"),
                triggers = listOf(
                    trigger(
                        action = action(url = "div-action://set_variable?name=text&value=new text"),
                        condition = "@{condition}"
                    )
                ),
                variables = listOf(variable("text", "initial text"))
            )
        )

        rule.onNodeWithTag("title").assertTextEquals("initial text")

        condition.set(true)

        rule.onNodeWithTag("title").assertTextEquals("new text")
    }

    private fun setContent(
        content: Div,
        triggers: List<DivTrigger>? = null,
        variables: List<DivVariable>? = null
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(content, triggers = triggers, variables = variables)
        )
    }
}
