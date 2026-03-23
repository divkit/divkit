package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
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
class DivViewTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `simple text is displayed`() {
        setContent(
            text(text = constant("Hello!"))
        )

        rule.onNodeWithText("Hello!").assertIsDisplayed()
    }

    @Test
    fun `text with expression is displayed`() {
        setContent(
            text(text = expression("value = @{counter}")),
            variables = listOf(variable("counter", 10))
        )

        rule.onNodeWithText("value = 10").assertIsDisplayed()
    }

    @Test
    fun `text with host variable is displayed`() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        setContent(
            text(text = expression("value = @{counter}"))
        )

        rule.onNodeWithText("value = 10").assertIsDisplayed()
    }

    @Test
    fun `card variable shadows host variable`() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        setContent(
            text(text = expression("value = @{counter}")),
            variables = listOf(variable("counter", 20))
        )

        rule.onNodeWithText("value = 20").assertIsDisplayed()
    }

    @Test
    fun `local variable shadows card variable`() {
        setContent(
            text(
                text = expression("value = @{counter}"),
                variables = listOf(variable("counter", 20))
            ),
            variables = listOf(variable("counter", 10))
        )

        rule.onNodeWithText("value = 20").assertIsDisplayed()
    }

    @Test
    fun `text changes when host variable changes`() {
        val variable = Variable.IntegerVariable("counter", 10)
        variableController.declare(variable)

        setContent(
            text(text = expression("value = @{counter}"))
        )

        variable.set(20)

        rule.onNodeWithText("value = 20").assertIsDisplayed()
    }

    @Test
    fun `text changes when set_variable action is clicked`() {
        setContent(
            text(
                action = action(url = "div-action://set_variable?name=var&value=new value"),
                id = "title",
                text = expression("var = @{var}"),
                variables = listOf(variable("var", "initial value"))
            )
        )

        rule.onNodeWithTag("title").apply {
            assertTextEquals("var = initial value")
            performClick()
            assertTextEquals("var = new value")
        }
    }

    @Test
    fun `text changes when set_variable action with local variable is clicked`() {
        setContent(
            container(
                items = listOf(
                    text(
                        action = action(url = "div-action://set_variable?name=var&value=new value"),
                        id = "title",
                        text = expression("var = @{var}")
                    )
                )
            ),
            variables = listOf(variable("var", "initial value"))
        )

        rule.onNodeWithTag("title").apply {
            assertTextEquals("var = initial value")
            performClick()
            assertTextEquals("var = new value")
        }
    }

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
        rule.setContent {
            val divContext = configuration.createContext(baseContext = LocalContext.current)
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = data(content, triggers, variables))
            }
        }
    }
}
