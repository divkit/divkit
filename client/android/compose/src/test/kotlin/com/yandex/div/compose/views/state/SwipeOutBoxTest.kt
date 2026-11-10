package com.yandex.div.compose.views.state

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.change
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SwipeOutBoxTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val currentState = Variable.StringVariable("current_state", "visible")

    private val variables = DivVariableController().apply {
        declare(currentState)
    }

    private val actions = listOf(
        action(url = "test://first"),
        action(url = "test://second"),
    )

    private val expectedActions = listOf(
        actionData(url = "test://first", source = DivActionSource.GESTURE),
        actionData(url = "test://second", source = DivActionSource.GESTURE),
    )

    private val mounted = mutableStateOf(true)

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = divConfiguration {
            actionHandler = this@SwipeOutBoxTest.actionHandler
            reporter = TestReporter()
            variableController = variables
        }
    )

    @Test
    fun `long right swipe dispatches every action once in order`() {
        showState()

        swipe(from = 0.1f, to = 0.9f, duration = 1000)

        assertEquals(expectedActions, actionHandler.handledActions)
    }

    @Test
    fun `long left swipe dispatches actions`() {
        showState()

        swipe(from = 0.9f, to = 0.1f, duration = 1000)

        assertEquals(expectedActions, actionHandler.handledActions)
    }

    @Test
    fun `short fast swipe dispatches actions`() {
        showState()

        swipe(from = 0.2f, to = 0.3f, duration = 50)

        assertEquals(expectedActions, actionHandler.handledActions)
    }

    @Test
    fun `short slow swipe restores position without actions`() {
        showState()

        swipe(from = 0.2f, to = 0.3f, duration = 4000)

        assertEquals(emptyList(), actionHandler.handledActions)
        composeRule.onNodeWithTag("content").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun `cancelled drag past threshold completes dismissal`() {
        showState()
        composeRule.onNodeWithTag("state").performTouchInput {
            down(Offset(width * 0.1f, centerY))
            moveBy(Offset(width * 0.75f, 0f))
        }

        composeRule.onNodeWithTag("state").performTouchInput { cancel() }
        composeRule.waitForIdle()

        assertEquals(expectedActions, actionHandler.handledActions)
        composeRule.onNodeWithTag("content").assertDoesNotExist()
    }

    @Test
    fun `swipeable state shares parent height with its sibling`() {
        showCard(
            data(
                container(
                    height = fixed(100),
                    items = listOf(
                        state(
                            id = "state",
                            height = matchParent(),
                            states = listOf(
                                state(
                                    stateId = "visible",
                                    div = text(text = "Swipe me"),
                                    swipeOutActions = actions,
                                ),
                            ),
                        ),
                        text(id = "footer", text = "After state", height = fixed(20)),
                    ),
                ),
            ),
        )

        composeRule.onNodeWithTag("footer").assertPositionInRootIsEqualTo(0.dp, 80.dp)
    }

    @Test
    fun `vertical swipe does not dispatch actions`() {
        showState()

        composeRule.onNodeWithTag("state").performTouchInput {
            swipe(
                start = Offset(centerX, height * 0.1f),
                end = Offset(centerX, height * 0.9f),
                durationMillis = 50,
            )
        }

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `state without swipe actions does not move`() {
        showState(swipeActions = null)

        composeRule.onNodeWithTag("state").performTouchInput {
            down(center)
            moveBy(Offset(width * 0.3f, 0f))
        }

        composeRule.onNodeWithTag("content").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun `state switch cancels pending swipe actions`() {
        showState()
        composeRule.mainClock.autoAdvance = false
        swipe(from = 0.1f, to = 0.9f, duration = 1000)

        currentState.set("other")
        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()

        assertEquals(emptyList(), actionHandler.handledActions)
        composeRule.onNodeWithTag("other").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun `completed swipe hides content when action leaves state unchanged`() {
        showState()

        swipe(from = 0.1f, to = 0.9f, duration = 1000)

        composeRule.onNodeWithTag("state").assertDoesNotExist()
        composeRule.onNodeWithTag("content").assertDoesNotExist()
    }

    @Test
    fun `switching away and back restores dismissed state`() {
        showState()
        swipe(from = 0.1f, to = 0.9f, duration = 1000)
        currentState.set("other")
        composeRule.waitForIdle()

        currentState.set("visible")
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("content").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun `dismissal collapses the space before the following element`() {
        showState()
        composeRule.onNodeWithTag("footer").assertPositionInRootIsEqualTo(0.dp, 100.dp)

        swipe(from = 0.1f, to = 0.9f, duration = 1000)

        composeRule.onNodeWithTag("footer").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun `dismissal survives leaving and returning to composition`() {
        showState()
        swipe(from = 0.1f, to = 0.9f, duration = 1000)
        composeRule.runOnIdle { mounted.value = false }
        composeRule.waitForIdle()

        composeRule.runOnIdle { mounted.value = true }

        composeRule.onNodeWithTag("content").assertDoesNotExist()
        composeRule.onNodeWithTag("footer").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun `swipe action can switch to another state`() {
        showState(
            swipeActions = listOf(
                action(url = "div-action://set_state?state_id=0/state/other")
            )
        )

        swipe(from = 0.1f, to = 0.9f, duration = 1000)

        composeRule.onNodeWithTag("other").assertPositionInRootIsEqualTo(0.dp, 0.dp)
        composeRule.onNodeWithTag("footer").assertPositionInRootIsEqualTo(0.dp, 100.dp)
    }

    @Test
    fun `replacing a dismissed element creates visible content`() {
        val card = showState()
        swipe(from = 0.1f, to = 0.9f, duration = 1000)

        composeRule.runOnIdle {
            runBlocking {
                assertTrue(
                    divContext.patcher.applyPatch(
                        data = card,
                        patch = DivPatch(
                            listOf(
                                change(id = "state", items = listOf(swipeState(actions))),
                            )
                        )
                    )
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("content").assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    private fun showState(swipeActions: List<DivAction>? = actions): DivData {
        val card = data(
            container(
                items = listOf(
                    swipeState(swipeActions),
                    text(id = "footer", text = "After state", height = fixed(20)),
                )
            )
        )
        showCard(card)
        return card
    }

    @Test
    fun `swiping one of two states with equal ids leaves the other visible`() {
        showCard(
            data(
                container(
                    itemBuilder = itemBuilder(
                        data = jsonArray(jsonObject(), jsonObject()),
                        prototypes = listOf(
                            prototype(
                                id = expression("row_@{index}"),
                                div = container(items = listOf(swipeState(actions))),
                            )
                        ),
                    )
                )
            )
        )

        composeRule.onAllNodesWithTag("state")[0].performTouchInput {
            swipe(
                start = Offset(width * 0.1f, centerY),
                end = Offset(width * 0.9f, centerY),
                durationMillis = 1000
            )
        }
        composeRule.waitForIdle()

        composeRule.onAllNodesWithTag("state").assertCountEquals(1)
        assertEquals(expectedActions, actionHandler.handledActions)
    }

    private fun showCard(card: DivData) {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext.component.baseContext,
                LocalDivContext provides divContext,
            ) {
                if (mounted.value) {
                    DivView(card)
                }
            }
        }
    }

    private fun swipeState(swipeActions: List<DivAction>?): Div.State = state(
        id = "state",
        width = fixed(300),
        height = fixed(100),
        stateIdVariable = "current_state",
        states = listOf(
            state(
                stateId = "visible",
                div = text(id = "content", text = "Swipe me"),
                swipeOutActions = swipeActions,
            ),
            state(stateId = "other", div = text(id = "other", text = "Other")),
        ),
    )

    private fun swipe(from: Float, to: Float, duration: Long) {
        composeRule.onNodeWithTag("state").performTouchInput {
            swipe(
                start = Offset(width * from, centerY),
                end = Offset(width * to, centerY),
                durationMillis = duration,
            )
        }
        composeRule.waitForIdle()
    }
}
