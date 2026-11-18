package com.yandex.div.compose.layoutprovider

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivLayoutProvider
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithLayoutProviderTest {
    private val reporter = TestReporter()
    private val widthVariable = Variable.IntegerVariable("width", 0)
    private val heightVariable = Variable.IntegerVariable("height", 0)

    private val variableController = DivVariableController().apply {
        declare(widthVariable, heightVariable)
    }

    private val configuration = DivConfiguration(
        reporter = reporter,
        variableController = variableController,
    )

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `variables are updated after the initial layout`() {
        setContent(
            text(
                text = constant("Test"),
                width = fixed(80),
                height = fixed(50),
                margins = insets(10),
                paddings = insets(5),
                layoutProvider = DivLayoutProvider(
                    widthVariableName = "width",
                    heightVariableName = "height",
                ),
            )
        )

        assertEquals(80L, widthVariable.getValue())
        assertEquals(50L, heightVariable.getValue())
    }

    @Test
    fun `width variable is updated when the value of the size expression changes`() {
        val desiredWidth = Variable.IntegerVariable("desired_width", 40)
        variableController.declare(desiredWidth)

        setContent(
            text(
                text = constant("Test"),
                width = fixed(intExpression("@{desired_width}")),
                layoutProvider = DivLayoutProvider(widthVariableName = "width"),
            )
        )

        desiredWidth.set(80)
        rule.waitForIdle()

        assertEquals(80L, widthVariable.getValue())
    }

    @Test
    fun `height variable is updated when the text content changes`() {
        val content = Variable.StringVariable("content", "One")
        variableController.declare(content)

        setContent(
            text(
                text = expression("@{content}"),
                lineHeight = 20,
                layoutProvider = DivLayoutProvider(heightVariableName = "height"),
            )
        )

        content.set("One\nTwo\nThree")
        rule.waitForIdle()

        assertEquals(60L, heightVariable.getValue())
    }

    @Test
    fun `local variable is updated`() {
        setContent(
            container(
                items = listOf(
                    text(
                        id = "text",
                        text = expression("width = @{width}"),
                        width = fixed(200),
                        layoutProvider = DivLayoutProvider(widthVariableName = "width"),
                    )
                ),
                variables = listOf(variable("width", 0))
            ),
        )

        rule.onNodeWithTag("text").assertTextEquals("width = 200")

        assertEquals(0L, widthVariable.getValue())
    }

    @Test
    fun `variable is not updated when the maximum update count is reached`() {
        setContent(
            text(
                text = constant(""),
                width = fixed(intExpression("@{width + 1}")),
                layoutProvider = DivLayoutProvider(widthVariableName = "width"),
            )
        )

        assertEquals(3L, widthVariable.getValue())
        assertEquals(listOf("Recursive layout for variable: width"), reporter.warnings)
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content),
        )
        rule.waitForIdle()
    }
}
