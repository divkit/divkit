package com.yandex.div.compose

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewWithTriggersTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val variableController = DivVariableController()

    private val activity: ComponentActivity
        get() = rule.activity

    private lateinit var divContext: DivContext

    @Before
    fun setUp() {
        divContext = DivContext(
            baseContext = activity,
            configuration = DivComposeConfiguration(
                reporter = TestReporter(),
                variableController = variableController
            )
        )
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

    @Test
    fun `does not trigger after leaving composition`() {
        val condition = Variable.BooleanVariable("condition", false)
        val counter = Variable.IntegerVariable("counter", 1)
        variableController.declare(condition, counter)

        setContent(
            text(
                id = "title",
                text = expression("counter = @{counter}")
            ),
            triggers = listOf(
                trigger(
                    action = action(
                        typed = setVariableAction(
                            name = "counter",
                            value = typedValue(intExpression("@{counter + 1}"))
                        )
                    ),
                    condition = "@{condition}"
                )
            )
        )

        activity.setContentView(View(activity))
        condition.set(true)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `does not trigger (local) after leaving composition`() {
        val condition = Variable.BooleanVariable("condition", false)
        val counter = Variable.IntegerVariable("counter", 1)
        variableController.declare(condition, counter)

        setContent(
            text(
                id = "title",
                text = expression("counter = @{counter}"),
                triggers = listOf(
                    trigger(
                        action = action(
                            typed = setVariableAction(
                                name = "counter",
                                value = typedValue(intExpression("@{counter + 1}"))
                            )
                        ),
                        condition = "@{condition}"
                    )
                )
            )
        )

        activity.setContentView(View(activity))
        condition.set(true)

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `does not trigger when reentering composition and condition is not changed`() {
        val condition = Variable.BooleanVariable("condition", false)
        val counter = Variable.IntegerVariable("counter", 1)
        variableController.declare(condition, counter)

        val data = data(
            text(
                id = "title",
                text = expression("counter = @{counter}")
            ),
            triggers = listOf(
                trigger(
                    action = action(
                        typed = setVariableAction(
                            name = "counter",
                            value = typedValue(intExpression("@{counter + 1}"))
                        )
                    ),
                    condition = "@{condition}"
                )
            )
        )
        setContent(data)

        condition.set(true)
        assertEquals(2L, counter.getValue())

        activity.setContentView(View(activity))
        setContent(data)

        assertEquals(2L, counter.getValue())
    }

    private fun setContent(
        content: Div,
        triggers: List<DivTrigger>? = null,
        variables: List<DivVariable>? = null
    ) {
        setContent(data(content, triggers = triggers, variables = variables))
    }

    private fun setContent(data: DivData) {
        activity.setContentView(
            ComposeView(divContext).apply {
                setContent {
                    DivView(data)
                }
            }
        )
    }
}
