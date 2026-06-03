package com.yandex.div.compose

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivVariable
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()

    private val configuration = DivComposeConfiguration(
        actionHandler = actionHandler,
        reporter = reporter
    )

    @Test
    fun `text changes when element with set_variable action is clicked`() {
        setContent(
            text(
                action = action(url = "div-action://set_variable?name=var&value=new value"),
                id = "button",
                text = expression("var = @{var}"),
                variables = listOf(variable("var", "initial value"))
            )
        )

        rule.onNodeWithTag("button").apply {
            assertTextEquals("var = initial value")
            performClick()
            assertTextEquals("var = new value")
        }
    }

    @Test
    fun `text changes when element with set_variable action with local variable is clicked`() {
        setContent(
            container(
                items = listOf(
                    text(
                        action = action(url = "div-action://set_variable?name=var&value=new value"),
                        id = "button",
                        text = expression("var = @{var}")
                    )
                )
            ),
            variables = listOf(variable("var", "initial value"))
        )

        rule.onNodeWithTag("button").apply {
            assertTextEquals("var = initial value")
            performClick()
            assertTextEquals("var = new value")
        }
    }

    @Test
    fun `disabled actions are ignored`() {
        setContent(
            text(
                actions = listOf(
                    action(url = "test://action_1"),
                    action(isEnabled = false, url = "test://disabled_action"),
                    action(url = "test://action_2"),
                ),
                text = constant("button")
            )
        )

        rule.onNodeWithText("button").performClick()

        assertEquals(
            listOf(
                actionData(url = "test://action_1"),
                actionData(url = "test://action_2")
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `tap on element with actions does not trigger container actions`() {
        setContent(
            container(
                action = action(url = "test://container_tap"),
                items = listOf(
                    text(
                        action = action(url = "test://tap"),
                        text = constant("button")
                    )
                )
            )
        )

        rule.onNodeWithText("button").performClick()

        assertEquals(
            listOf(
                actionData(
                    source = DivActionSource.TAP,
                    url = "test://tap"
                )
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `double tap triggers doubleTapActions`() {
        setContent(
            text(
                doubleTapActions = listOf(action(url = "test://double_tap")),
                text = constant("button")
            )
        )

        rule.onNodeWithText("button").performClick()

        assertEquals(emptyList(), actionHandler.handledActions)

        rule.onNodeWithText("button").performTouchInput { doubleClick() }

        assertEquals(
            listOf(
                actionData(
                    source = DivActionSource.DOUBLE_TAP,
                    url = "test://double_tap"
                )
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `double tap does not trigger tap actions`() {
        setContent(
            text(
                action = action(url = "test://tap"),
                doubleTapActions = listOf(action(url = "test://double_tap")),
                text = constant("button")
            )
        )

        rule.onNodeWithText("button").performTouchInput { doubleClick() }

        assertEquals(
            listOf(
                actionData(
                    source = DivActionSource.DOUBLE_TAP,
                    url = "test://double_tap"
                )
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `tap triggers tap actions on element with actions and doubleTapActions`() {
        setContent(
            text(
                action = action(url = "test://tap"),
                doubleTapActions = listOf(action(url = "test://double_tap")),
                text = constant("button")
            )
        )

        rule.onNodeWithText("button").performClick()

        // action is triggered with a delay necessary to distinguish a single tap from a double tap
        rule.waitUntil {
            actionHandler.handledActions.isNotEmpty()
        }

        assertEquals(
            listOf(
                actionData(
                    source = DivActionSource.TAP,
                    url = "test://tap"
                )
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `long tap triggers longTapActions`() {
        setContent(
            text(
                longTapActions = listOf(action(url = "test://long_tap")),
                text = constant("button")
            )
        )

        rule.onNodeWithText("button").performClick()

        assertEquals(emptyList(), actionHandler.handledActions)

        rule.onNodeWithText("button").performTouchInput { longClick() }

        assertEquals(
            listOf(
                actionData(
                    source = DivActionSource.LONG_TAP,
                    url = "test://long_tap"
                )
            ),
            actionHandler.handledActions
        )
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

private fun actionData(
    source: DivActionSource = DivActionSource.TAP,
    url: String
): DivActionData {
    return DivActionData(
        id = "test",
        payload = null,
        source = source,
        url = url.toUri()
    )
}

private class TestExternalActionHandler : DivExternalActionHandler {
    val handledActions = mutableListOf<DivActionData>()

    override fun handle(context: DivActionHandlingContext, action: DivActionData) {
        handledActions.add(action)
    }
}
