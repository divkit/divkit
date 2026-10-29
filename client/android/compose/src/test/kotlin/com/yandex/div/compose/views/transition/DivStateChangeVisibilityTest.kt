package com.yandex.div.compose.views.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.IntSize
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.setContent
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.DivContainer
import com.yandex.div2.DivExtension
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivStateChangeVisibilityTest : StateChangeTransitionTestBase() {
    @Test
    fun `common id visible to gone runs transition out`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val handler = TrackingExtensionHandler(composedIds)
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = PERSISTENT_ID,
                extensions = listOf(extension),
                transitionOut = fade(duration = TRANSITION_DURATION_MS),
            ),
            incoming = text(
                text = INCOMING,
                id = PERSISTENT_ID,
                visibility = constant(DivVisibility.GONE),
            ),
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
            val snapshot = handler.transitionStateOf(PERSISTENT_ID).snapshots.exiting.single()
            assertEquals(PERSISTENT_ID, snapshot.id)
            assertFalse(snapshot.layer.isReleased)
            assertTrue(snapshot.layer.alpha in 0.01f..0.99f)
            snapshot.layer
        }
        composeRule.onNodeWithText(INCOMING).assertDoesNotExist()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            assertTrue(layer.isReleased)
        }
    }

    @Test
    fun `rendered visible id runs transition out when visibility and state change together`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val handler = TrackingExtensionHandler(composedIds)
        val visibility = Variable.StringVariable(VISIBILITY_VARIABLE, "visible")
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = PERSISTENT_ID,
                visibility = visibilityExpression("@{$VISIBILITY_VARIABLE}"),
                extensions = listOf(extension),
                transitionOut = fade(duration = TRANSITION_DURATION_MS),
            ),
            incoming = text(
                text = INCOMING,
                id = PERSISTENT_ID,
                visibility = constant(DivVisibility.GONE),
            ),
            extraVariables = listOf(visibility),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to handler
            ),
        )

        visibility.set("gone")
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 2)
        val layer = composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            val snapshot = handler.transitionStateOf(PERSISTENT_ID).snapshots.exiting.single()
            assertEquals(PERSISTENT_ID, snapshot.id)
            assertFalse(snapshot.layer.isReleased)
            assertTrue(snapshot.layer.alpha in 0.01f..0.99f)
            snapshot.layer
        }
        composeRule.onNodeWithText(INCOMING).assertDoesNotExist()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            assertTrue(layer.isReleased)
        }
    }

    @Test
    fun `common descendant releases outgoing ancestor capture`() {
        val common = text(
            text = INCOMING,
            id = PERSISTENT_ID,
            transitionTriggers = listOf(DivTransitionTrigger.DATA_CHANGE),
        ).value()
        val descendant = text(
            text = OUTGOING,
            id = DESCENDANT,
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
        ).value()
        val transitionState = StateChangeTransitionState().apply {
            updateCurrentLayout(
                DESCENDANT,
                StateChangeLayoutState(
                    sceneSize = IntSize(100, 100),
                    bounds = ElementBounds(Offset.Zero, IntSize(40, 40)),
                    positionInRoot = Offset.Zero,
                ),
            )
            captureExitValues(setOf(DESCENDANT))
        }
        var commonTransition: StateChangeTransitionModifier? = null
        var descendantTransition: StateChangeTransitionModifier? = null
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                            scope = StateChangeTransitionScope(
                                enterScope = this,
                                exitScope = this,
                                enteringIds = emptySet(),
                                exitingIds = setOf(DESCENDANT),
                                commonIds = setOf(PERSISTENT_ID),
                                transitionState = transitionState,
                            ),
                            contentActive = false,
                            overlayCaptured = true,
                        )
                    ) {
                        val commonResult = Modifier.applyStateChangeTransition(
                            common,
                            DivVisibility.VISIBLE,
                        )
                        Box(commonResult.modifier) {
                            val transitionContext = LocalStateChangeTransitionContext.current
                            CompositionLocalProvider(
                                LocalStateChangeTransitionContext provides transitionContext.copy(
                                    overlayCaptured =
                                        !commonResult.releasesOverlayCapture || commonResult.capturesOverlay,
                                )
                            ) {
                                val descendantResult = Modifier.applyStateChangeTransition(
                                    descendant,
                                    DivVisibility.VISIBLE,
                                )
                                SideEffect { descendantTransition = descendantResult }
                                Box(descendantResult.modifier)
                            }
                        }
                        SideEffect { commonTransition = commonResult }
                    }
                }
            }
        }

        composeRule.waitForIdle()
        val commonResult = requireNotNull(commonTransition)
        assertEquals(0f, commonResult.appearanceAlpha)
        assertTrue(commonResult.releasesOverlayCapture)
        assertFalse(commonResult.capturesOverlay)
        assertTrue(requireNotNull(descendantTransition).capturesOverlay)
    }

    @Test
    fun `rapid retarget keeps each outgoing state until its fade finishes`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val handler = TrackingExtensionHandler(composedIds)
        val state = renderStates(
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
                    stateId = MIDDLE,
                    div = text(
                        text = MIDDLE,
                        id = MIDDLE_ID,
                        extensions = listOf(extension),
                        transitionIn = fade(duration = TRANSITION_DURATION_MS),
                        transitionOut = fade(duration = TRANSITION_DURATION_MS),
                    ),
                ),
                state(
                    stateId = INCOMING,
                    div = text(
                        text = INCOMING,
                        id = INCOMING_ID,
                        extensions = listOf(extension),
                        transitionIn = fade(duration = TRANSITION_DURATION_MS),
                    ),
                ),
            ),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to handler
            ),
        )

        state.set(MIDDLE)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(300L)
        val outgoingLayer = composeRule.runOnIdle {
            handler.transitionStateOf(OUTGOING_ID).snapshots.exiting.single().layer
        }
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        val middleLayer = composeRule.runOnIdle {
            assertEquals(setOf(INCOMING_ID), composedIds)
            val middleSnapshot = handler.transitionStateOf(MIDDLE_ID).snapshots.exiting.single()
            assertEquals(MIDDLE_ID, middleSnapshot.id)
            assertFalse(outgoingLayer.isReleased)
            assertFalse(middleSnapshot.layer.isReleased)
            assertTrue(outgoingLayer !== middleSnapshot.layer)
            assertTrue(outgoingLayer.alpha in 0.01f..0.99f)
            middleSnapshot.layer
        }

        composeRule.mainClock.advanceTimeBy(700L)
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals(setOf(INCOMING_ID), composedIds)
            assertTrue(outgoingLayer.isReleased)
            assertFalse(middleLayer.isReleased)
            assertTrue(middleLayer.alpha in 0.001f..0.99f)
        }

        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertEquals(setOf(INCOMING_ID), composedIds)
            assertTrue(middleLayer.isReleased)
        }
    }

    @Test
    fun `rapid retarget continues fade from current alpha`() {
        val data = text(
            text = MIDDLE,
            id = MIDDLE_ID,
            transitionIn = fade(duration = TRANSITION_DURATION_MS),
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
        ).value()
        val exiting = mutableStateOf(false)
        val transitionState = StateChangeTransitionState()
        lateinit var exitVisibility: MutableTransitionState<Boolean>
        var appearanceAlpha: Float? = null
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                exitVisibility = remember { MutableTransitionState(true) }
                AnimatedVisibility(
                    visibleState = exitVisibility,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None,
                ) exitContent@ {
                    val enterVisibility = remember {
                        MutableTransitionState(false).apply { targetState = true }
                    }
                    AnimatedVisibility(
                        visibleState = enterVisibility,
                        enter = EnterTransition.None,
                        exit = ExitTransition.None,
                    ) enterContent@ {
                        CompositionLocalProvider(
                            LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                                scope = StateChangeTransitionScope(
                                    enterScope = this@enterContent,
                                    exitScope = this@exitContent,
                                    enteringIds = if (exiting.value) emptySet() else setOf(MIDDLE_ID),
                                    exitingIds = if (exiting.value) setOf(MIDDLE_ID) else emptySet(),
                                    transitionState = transitionState,
                                )
                            )
                        ) {
                            val transition = Modifier.applyStateChangeTransition(
                                data,
                                DivVisibility.VISIBLE,
                            )
                            SideEffect { appearanceAlpha = transition.appearanceAlpha }
                            Box(transition.modifier)
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 3)
        composeRule.waitForIdle()
        var alphaBeforeRetarget = 0f
        composeRule.runOnIdle {
            alphaBeforeRetarget = requireNotNull(appearanceAlpha)
            transitionState.captureExitValues(setOf(MIDDLE_ID))
            exiting.value = true
            exitVisibility.targetState = false
        }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()

        val alphaAfterRetarget = requireNotNull(appearanceAlpha)
        assertTrue(alphaBeforeRetarget < 0.8f, "Enter transition did not reach a partial alpha")
        assertTrue(
            abs(alphaBeforeRetarget - alphaAfterRetarget) < 0.1f,
            "Alpha jumped from $alphaBeforeRetarget to $alphaAfterRetarget",
        )
    }

    @Test
    fun `returning to retained state starts a fresh enter lifecycle`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val handler = TrackingExtensionHandler(composedIds)
        val state = renderStates(
            states = listOf(
                state(
                    stateId = OUTGOING,
                    div = text(
                        text = OUTGOING,
                        id = OUTGOING_ID,
                        extensions = listOf(extension),
                        transitionIn = fade(duration = TRANSITION_DURATION_MS),
                        transitionOut = fade(duration = TRANSITION_DURATION_MS),
                    ),
                ),
                state(
                    stateId = INCOMING,
                    div = text(
                        text = INCOMING,
                        id = INCOMING_ID,
                        extensions = listOf(extension),
                        transitionIn = fade(duration = TRANSITION_DURATION_MS),
                        transitionOut = fade(duration = TRANSITION_DURATION_MS),
                    ),
                ),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 3)
        val outgoingLayer = composeRule.runOnIdle {
            handler.transitionStateOf(OUTGOING_ID).snapshots.exiting.single().layer
        }
        state.set(OUTGOING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        val incomingLayer = composeRule.runOnIdle {
            assertEquals(2, handler.compositionsOf(OUTGOING_ID))
            assertEquals(setOf(OUTGOING_ID), composedIds)
            assertTrue(outgoingLayer.isReleased)
            val snapshot = handler.transitionStateOf(INCOMING_ID).snapshots.exiting.single()
            assertEquals(INCOMING_ID, snapshot.id)
            assertFalse(snapshot.layer.isReleased)
            snapshot.layer
        }

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 2)
        composeRule.runOnIdle {
            val current = handler.transitionStateOf(OUTGOING_ID)
            current.captureExitValues(setOf(OUTGOING_ID))
            assertTrue(requireNotNull(current.getExitValues(OUTGOING_ID)).alpha in 0.01f..0.99f)
        }

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.runOnIdle {
            assertTrue(incomingLayer.isReleased)
            assertEquals(setOf(OUTGOING_ID), composedIds)
        }
    }

    @Test
    fun `retained outgoing state does not affect active wrap content size`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = constant(OUTGOING),
                id = OUTGOING_ID,
                width = fixed(OUTGOING_WIDTH_DP),
                transitionOut = fade(duration = TRANSITION_DURATION_MS),
            ),
            incoming = text(
                text = constant(INCOMING),
                id = INCOMING_ID,
                width = fixed(CONTENT_WIDTH_DP),
            ),
            stateWidth = wrapContentUpTo(MAX_STATE_WIDTH_DP),
        )

        assertStateWidth(OUTGOING_WIDTH_DP)
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        assertStateWidth(CONTENT_WIDTH_DP)
    }

    @Test
    fun `retained outgoing state stops observing variable triggers`() {
        val condition = Variable.BooleanVariable(CONDITION_VARIABLE, false)
        val counter = Variable.IntegerVariable(COUNTER_VARIABLE, 1L)
        val outgoing = text(
            text = OUTGOING,
            id = OUTGOING_ID,
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
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
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = outgoing,
            extraVariables = listOf(condition, counter),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        condition.set(true)
        composeRule.waitForIdle()

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `retained match parent overlap child stops observing variable triggers`() {
        val condition = Variable.BooleanVariable(CONDITION_VARIABLE, false)
        val counter = Variable.IntegerVariable(COUNTER_VARIABLE, 1L)
        val outgoing = container(
            width = wrapContent(),
            orientation = DivContainer.Orientation.OVERLAP,
            items = listOf(
                text(
                    text = OUTGOING,
                    id = OUTGOING_ID,
                    transitionOut = fade(duration = TRANSITION_DURATION_MS),
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
            ),
        )
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = outgoing,
            extraVariables = listOf(condition, counter),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        condition.set(true)
        composeRule.waitForIdle()

        assertEquals(1L, counter.getValue())
    }

    @Test
    fun `shared local component stays active after retained owners are disposed`() {
        val condition = Variable.BooleanVariable(CONDITION_VARIABLE, false)
        val counter = Variable.IntegerVariable(COUNTER_VARIABLE, 1L)
        val shared = text(
            text = "shared",
            id = PERSISTENT_ID,
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
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(
                items = listOf(
                    text(
                        text = OUTGOING,
                        id = OUTGOING_ID,
                        transitionOut = fade(duration = TRANSITION_DURATION_MS),
                    ),
                    shared,
                ),
            ),
            incoming = container(
                items = listOf(
                    text(
                        text = INCOMING,
                        id = INCOMING_ID,
                        transitionIn = fade(duration = TRANSITION_DURATION_MS),
                        transitionOut = fade(duration = TRANSITION_DURATION_MS),
                    ),
                    shared,
                ),
            ),
            extraVariables = listOf(condition, counter),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 3)
        state.set(OUTGOING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.waitForIdle()
        condition.set(true)
        composeRule.waitForIdle()

        assertEquals(2L, counter.getValue())
    }
}
