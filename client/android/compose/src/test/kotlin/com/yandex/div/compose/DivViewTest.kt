package com.yandex.div.compose

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
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val configuration = DivComposeConfiguration(
        reporter = reporter,
        variableController = variableController
    )

    @Test
    fun `error is reported if data is empty`() {
        reporter.failOnError = false

        rule.setContent(
            configuration = configuration,
            data = DivData(
                logId = "test",
                states = emptyList()
            )
        )

        assertEquals("Empty data", reporter.lastError)
    }

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
    fun `text with visibility=gone is not displayed`() {
        setContent(
            text(id = "title", text = "Hello!", visibility = constant(DivVisibility.GONE))
        )

        rule.onNodeWithTag("title").assertDoesNotExist()
    }

    @Test
    fun `text disappears when visibility changes to gone`() {
        val visibility = Variable.StringVariable("visibility", "visible")
        variableController.declare(visibility)

        setContent(
            text(
                id = "title",
                text = "Hello!",
                visibility = visibilityExpression("@{visibility}")
            )
        )

        rule.onNodeWithTag("title").assertIsDisplayed()

        visibility.set("gone")

        rule.onNodeWithTag("title").assertDoesNotExist()
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

    private fun setContent(
        content: Div,
        variables: List<DivVariable>? = null
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(content, variables = variables)
        )
    }
}
