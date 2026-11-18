package com.yandex.div.compose.views.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivExtension
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivStateChangeNestedStateTest : StateChangeTransitionTestBase() {
    private val ancestorVisibility = Variable.StringVariable(VISIBILITY_VARIABLE, "visible")
    private val nestedStateVariable = Variable.StringVariable(NESTED_STATE_VARIABLE, OUTGOING)
    private val boundaryComposedIds = mutableSetOf<String>()
    private val boundaryTracker = TrackingExtensionHandler(boundaryComposedIds)

    @Test
    fun `initially invisible wrapped state has no rendered transition ids`() {
        ancestorVisibility.set("invisible")
        renderWrappedNestedState()

        composeRule.runOnIdle {
            assertEquals(
                emptySet(),
                boundaryTracker.transitionStateOf(OUTGOING_ID).snapshotRenderedIds(),
            )
        }
    }

    @Test
    fun `hiding wrapped state in same update does not retain outgoing snapshot`() {
        renderWrappedNestedState()

        composeRule.runOnIdle {
            ancestorVisibility.set("invisible")
            nestedStateVariable.set(INCOMING)
        }
        settleNestedStateChange()

        composeRule.runOnIdle {
            assertTrue(boundaryTracker.transitionStateOf(OUTGOING_ID).snapshots.exiting.isEmpty())
            assertEquals(
                emptySet(),
                boundaryTracker.transitionStateOf(INCOMING_ID).snapshotRenderedIds(),
            )
        }
    }

    @Test
    fun `showing wrapped state in same update retains only visible incoming ids`() {
        ancestorVisibility.set("invisible")
        renderWrappedNestedState()

        composeRule.runOnIdle {
            ancestorVisibility.set("visible")
            nestedStateVariable.set(INCOMING)
        }
        settleNestedStateChange()

        composeRule.runOnIdle {
            assertTrue(boundaryTracker.transitionStateOf(OUTGOING_ID).snapshots.exiting.isEmpty())
            assertEquals(
                setOf(INCOMING_ID),
                boundaryTracker.transitionStateOf(INCOMING_ID).snapshotRenderedIds(),
            )
        }
    }

    @Test
    fun `hidden wrapped state drops previously rendered transition ids`() {
        renderWrappedNestedState()
        composeRule.runOnIdle { ancestorVisibility.set("invisible") }
        composeRule.waitForIdle()

        nestedStateVariable.set(INCOMING)
        settleNestedStateChange()

        composeRule.runOnIdle {
            assertTrue(boundaryTracker.transitionStateOf(OUTGOING_ID).snapshots.exiting.isEmpty())
        }
    }

    @Test
    fun `regular nested state isolates outer scope and starts its own incoming transition`() {
        var nestedScope: StateChangeTransitionScope? = null
        val handler = object : DivExtensionHandler {
            @Composable
            override fun Content(
                modifier: Modifier,
                environment: DivExtensionEnvironment,
                content: @Composable (Modifier) -> Unit,
            ) {
                val scope = LocalStateChangeTransitionContext.current.scope
                SideEffect { nestedScope = scope }
                content(modifier)
            }
        }
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(items = listOf(
                text(text = "Sibling", transitionOut = fade(TRANSITION_DURATION_MS)),
                state(
                    id = "nested",
                    stateIdVariable = NESTED_STATE_VARIABLE,
                    states = listOf(
                        state(
                            stateId = OUTGOING,
                            div = text(text = OUTGOING, extensions = listOf(extension)),
                        ),
                        state(stateId = INCOMING, div = text(
                            text = INCOMING,
                            id = INCOMING_ID,
                            extensions = listOf(extension),
                            transitionIn = fade(TRANSITION_DURATION_MS),
                        )),
                    ),
                ),
            )),
            extraVariables = listOf(nestedStateVariable),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )
        assertNull(nestedScope)

        nestedStateVariable.set(INCOMING)
        settleNestedStateChange()

        assertEquals(setOf(INCOMING_ID), assertNotNull(nestedScope).enteringIds)
    }

    private fun renderWrappedNestedState() {
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val nested = state(
            id = "nested",
            stateIdVariable = NESTED_STATE_VARIABLE,
            states = listOf(
                state(stateId = OUTGOING, div = text(
                    text = OUTGOING,
                    id = OUTGOING_ID,
                    extensions = listOf(extension),
                    transitionOut = fade(TRANSITION_DURATION_MS),
                )),
                state(stateId = INCOMING, div = text(
                    text = INCOMING,
                    id = INCOMING_ID,
                    extensions = listOf(extension),
                    transitionIn = fade(TRANSITION_DURATION_MS),
                )),
            ),
        )
        renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(
                visibility = visibilityExpression("@{$VISIBILITY_VARIABLE}"),
                items = listOf(container(items = listOf(container(items = listOf(nested))))),
            ),
            extraVariables = listOf(ancestorVisibility, nestedStateVariable),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to boundaryTracker),
        )
    }

    private fun settleNestedStateChange() {
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
    }

    @Test
    fun `new nested content in retained state does not start variable triggers`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val nestedState = Variable.StringVariable(NESTED_STATE_VARIABLE, OUTGOING)
        val condition = Variable.BooleanVariable(CONDITION_VARIABLE, true)
        val counter = Variable.IntegerVariable(COUNTER_VARIABLE, 1L)
        val triggerBearingChild = text(
            text = INCOMING,
            id = NESTED_TRANSITION_ID,
            extensions = listOf(extension),
            triggers = listOf(
                trigger(
                    action = action(
                        typed = setVariableAction(
                            name = COUNTER_VARIABLE,
                            value = typedValue(intExpression("@{$COUNTER_VARIABLE + 1}")),
                        )
                    ),
                    condition = "@{$CONDITION_VARIABLE}",
                )
            ),
        )
        val nested = state(
            id = "nested",
            stateIdVariable = NESTED_STATE_VARIABLE,
            states = listOf(
                state(stateId = OUTGOING, div = text(text = OUTGOING)),
                state(stateId = INCOMING, div = triggerBearingChild),
            ),
        )
        val outgoing = container(
            items = listOf(
                text(
                    text = OUTGOING,
                    id = OUTGOING_ID,
                    transitionOut = fade(duration = TRANSITION_DURATION_MS),
                ),
                nested,
            ),
        )
        val outerState = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = outgoing,
            extraVariables = listOf(nestedState, condition, counter),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to TrackingExtensionHandler(composedIds)
            ),
        )

        outerState.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        nestedState.set(INCOMING)
        composeRule.waitForIdle()

        assertEquals(1L, counter.getValue())
        assertEquals(emptySet(), composedIds)
    }

    @Test
    fun `nested state isolates outer transition scope`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val nestedState = state(
            id = "nested",
            states = listOf(
                state(
                    stateId = "content",
                    div = text(
                        text = OUTGOING,
                        transitionOut = DivAppearanceTransition.Scale(DivScaleTransition()),
                        id = NESTED_TRANSITION_ID,
                        extensions = listOf(extension),
                    ),
                )
            ),
        )
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = nestedState,
            incoming = text(text = INCOMING, id = INCOMING_ID),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to TrackingExtensionHandler(composedIds)
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.waitForIdle()
        assertEquals(emptySet(), composedIds)
        composeRule.onNodeWithText(INCOMING).assertExists()
    }

    @Test
    fun `nested state boundary runs its own transition out`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val handler = TrackingExtensionHandler(composedIds)
        val nestedState = state(
            extensions = listOf(extension),
            id = NESTED_TRANSITION_ID,
            states = listOf(
                state(
                    stateId = OUTGOING,
                    div = text(
                        text = OUTGOING,
                        id = OUTGOING_ID,
                        extensions = listOf(extension),
                    ),
                )
            ),
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
        )
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = nestedState,
            incoming = text(text = INCOMING, id = INCOMING_ID),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to handler
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 2)
        val layer = composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            val snapshot = handler.transitionStateOf(NESTED_TRANSITION_ID).snapshots.exiting.single()
            assertEquals(NESTED_TRANSITION_ID, snapshot.id)
            assertFalse(snapshot.layer.isReleased)
            assertTrue(snapshot.layer.alpha in 0.01f..0.99f)
            snapshot.layer
        }
        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            assertTrue(layer.isReleased)
        }
    }

    @Test
    fun `nested state under invisible ancestor does not retain outgoing content`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val nestedState = Variable.StringVariable(NESTED_STATE_VARIABLE, OUTGOING)
        val nested = state(
            id = "nested",
            stateIdVariable = NESTED_STATE_VARIABLE,
            states = listOf(
                state(
                    stateId = OUTGOING,
                    div = text(
                        text = OUTGOING,
                        id = OUTGOING_ID,
                        extensions = listOf(extension),
                        transitionOut = fade(duration = TRANSITION_DURATION_MS),
                    ),
                ),
                state(
                    stateId = INCOMING,
                    div = text(text = INCOMING, id = INCOMING_ID),
                ),
            ),
        )
        renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(
                visibility = constant(DivVisibility.INVISIBLE),
                items = listOf(nested),
            ),
            extraVariables = listOf(nestedState),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to TrackingExtensionHandler(composedIds)
            ),
        )

        assertEquals(setOf(OUTGOING_ID), composedIds)
        nestedState.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.waitForIdle()
        assertEquals(emptySet(), composedIds)
    }
}
