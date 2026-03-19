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
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivText
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
    fun localVariableShadowsCardVariable() {
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
    fun `text changes when set_variable action is triggered`() {
        setContent(
            container(
                items = listOf(
                    text(
                        action = action(url = "div-action://set_variable?name=card_var&value=new card value"),
                        id = "card_text",
                        text = expression("card_var = @{card_var}")
                    ),
                    text(
                        action = action(url = "div-action://set_variable?name=local_var&value=new local value"),
                        id = "local_text",
                        text = expression("local_var = @{local_var}")
                    )
                ),
                variables = listOf(variable("local_var", "local value"))
            ),
            variables = listOf(variable("card_var", "card value"))
        )

        rule.onNodeWithTag("card_text").apply {
            assertTextEquals("card_var = card value")
            performClick()
            assertTextEquals("card_var = new card value")
        }

        rule.onNodeWithTag("local_text").apply {
            assertTextEquals("local_var = local value")
            performClick()
            assertTextEquals("local_var = new local value")
        }
    }

    private fun setContent(content: Div, variables: List<DivVariable>? = null) {
        rule.setContent {
            val divContext = configuration.createContext(baseContext = LocalContext.current)
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = data(content, variables))
            }
        }
    }
}

private fun data(
    content: Div,
    variables: List<DivVariable>? = null
): DivData {
    return DivData(
        logId = "test",
        variables = variables,
        states = listOf(
            DivData.State(
                stateId = 0,
                div = content
            )
        )
    )
}

private fun text(
    action: DivAction? = null,
    id: String? = null,
    text: Expression<String>,
    variables: List<DivVariable>? = null
): Div {
    return Div.Text(
        value = DivText(
            action = action,
            id = id,
            text = text,
            variables = variables
        )
    )
}

private fun container(
    items: List<Div>,
    variables: List<DivVariable>? = null
): Div {
    return Div.Container(
        value = DivContainer(
            items = items,
            variables = variables
        )
    )
}
