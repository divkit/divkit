package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivText
import com.yandex.div2.DivVariable
import org.junit.Rule
import org.junit.Test

class DivViewTest {

    @get:Rule
    val rule = createComposeRule()

    private val variableController = DivVariableController()

    private val configuration = DivComposeConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun simpleTextIsDisplayed() {
        setContent(
            text(text = constant("Hello!"))
        )

        rule.onNodeWithText("Hello!").assertIsDisplayed()
    }

    @Test
    fun textWithExpressionIsDisplayed() {
        setContent(
            text(text = expression("value = @{counter}")),
            variables = listOf(integerVariable("counter", 10))
        )

        rule.onNodeWithText("value = 10").assertIsDisplayed()
    }

    @Test
    fun textWithHostVariableIsDisplayed() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        setContent(
            text(text = expression("value = @{counter}"))
        )

        rule.onNodeWithText("value = 10").assertIsDisplayed()
    }

    @Test
    fun cardVariableShadowsHostVariable() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        setContent(
            text(text = expression("value = @{counter}")),
            variables = listOf(integerVariable("counter", 20))
        )

        rule.onNodeWithText("value = 20").assertIsDisplayed()
    }

    @Test
    fun textChangesWhenVariableChanges() {
        variableController.declare(Variable.IntegerVariable("counter", 10))

        setContent(
            text(text = expression("value = @{counter}"))
        )

        variableController.get("counter")?.set("20")

        rule.onNodeWithText("value = 20").assertIsDisplayed()
    }

    @Test
    fun textChangesWhenSetVariableActionIsTriggered() {
        setContent(
            text(
                action = action(url = "div-action://set_variable?name=counter&value=20"),
                id = "title",
                text = expression("value = @{counter}")
            ),
            variables = listOf(integerVariable("counter", 10))
        )

        rule.onNodeWithTag("title").apply {
            assertTextEquals("value = 10")
            performClick()
            assertTextEquals("value = 20")
        }
    }

    private fun setContent(content: Div, variables: List<DivVariable>? = null) {
        setContent(data(content, variables))
    }

    private fun setContent(data: DivData) {
        rule.setContent {
            val divContext = configuration.createContext(baseContext = LocalContext.current)
            CompositionLocalProvider(LocalContext provides divContext) {
                DivView(data = data)
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
    text: Expression<String>
): Div {
    return Div.Text(
        value = DivText(
            action = action,
            id = id,
            text = text
        )
    )
}
