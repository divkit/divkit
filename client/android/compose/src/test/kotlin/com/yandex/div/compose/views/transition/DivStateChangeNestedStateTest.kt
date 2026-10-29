package com.yandex.div.compose.views.transition

import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivExtension
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivStateChangeNestedStateTest : StateChangeTransitionTestBase() {
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
