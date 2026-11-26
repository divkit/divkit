package com.yandex.div.compose.views.transition

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivChangeBoundsTransition
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivExtension
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class StateChangeSnapshotTest : StateChangeTransitionTestBase() {
    @Test
    fun `bounds capture requires a unique rendered source id`() {
        val state = StateChangeTransitionState()
        val bounds = ElementBounds(10f, 20f, 40f, 60f)
        val layout = StateChangeLayoutState(IntSize(100, 100), bounds, Offset.Zero)
        val node = mock<StateChangeSnapshotNode> {
            on { id } doReturn "box"
            on { this.layout } doReturn layout
        }
        state.snapshots.update(StateChangeSnapshot(node, transition = null, baseAlpha = 1f))
        state.registerRenderedId("box")
        state.registerRenderedId("box")
        assertTrue(state.snapshots.captureBounds(state.uniqueRenderedIds).isEmpty())

        state.unregisterRenderedId("box")
        assertEquals(bounds, state.snapshots.captureBounds(state.uniqueRenderedIds)["box"]?.bounds)
        state.unregisterRenderedId("box")
        assertTrue(state.snapshots.captureBounds(state.uniqueRenderedIds).isEmpty())
    }

    private val composedIds = mutableSetOf<String>()
    private val tracker = TrackingExtensionHandler(composedIds)
    private lateinit var outgoingState: StateChangeTransitionState
    private val handler = object : DivExtensionHandler {
        @Composable
        override fun Content(
            modifier: Modifier,
            environment: DivExtensionEnvironment,
            content: @Composable (Modifier) -> Unit,
        ) {
            val state = requireNotNull(LocalStateChangeTransitionContext.current.scope).transitionState
            SideEffect { outgoingState = state }
            tracker.Content(modifier, environment, content)
        }
    }

    @Test
    fun `plain incoming composition survives outgoing exit`() {
        val composedIds = mutableSetOf<String>()
        val handler = TrackingExtensionHandler(composedIds)
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = OUTGOING_ID,
                transitionOut = linearFade(TRANSITION_DURATION_MS, 0.0),
            ),
            incoming = text(
                text = INCOMING,
                id = INCOMING_ID,
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()

        assertEquals(1, handler.compositionsOf(INCOMING_ID))
    }

    @Test
    fun `outgoing composition is disposed while its snapshot animates`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = OUTGOING_ID,
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                transitionOut = linearFade(1000, 0.0),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(500)

        composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            assertEquals(1, outgoingState.overlayLayers().size)
            assertFalse(outgoingState.overlayLayers().single().layer.isReleased)
            assertTrue(outgoingState.overlayLayers().single().layer.alpha in 0.1f..0.9f)
        }
    }

    @Test
    fun `snapshot of overflowing container uses scene bounds`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(
                alignmentHorizontal = DivAlignmentHorizontal.CENTER,
                clipToBounds = constant(false),
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                height = fixed(40),
                id = OUTGOING_ID,
                items = listOf(text(text = constant("Overflow"), width = fixed(80))),
                transitionOut = linearFade(TRANSITION_DURATION_MS, 0.0),
                width = fixed(40),
            ),
            incoming = text(text = INCOMING, id = INCOMING_ID),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
            modifier = Modifier.size(100.dp),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        val snapshot = outgoingState.snapshots.exiting.single()
        val layout = requireNotNull(outgoingState.getExitLayout(OUTGOING_ID))
        val captureArea = snapshot.captureArea(snapshot.layer.size, layout)
        assertEquals(layout.sceneSize, captureArea.size)
        assertEquals(
            IntOffset(-layout.bounds.left.roundToInt(), -layout.bounds.top.roundToInt()),
            captureArea.offset,
        )
        assertTrue(captureArea.size.width > snapshot.layer.size.width)
    }

    @Test
    fun `snapshot is released after exit finishes`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = OUTGOING_ID,
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                transitionOut = linearFade(1000, 0.0),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(500)
        val layer = composeRule.runOnIdle { outgoingState.overlayLayers().single().layer }

        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.runOnIdle { assertTrue(layer.isReleased) }
    }

    @Test
    fun `common descendant is excluded from outgoing ancestor snapshot`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(
                id = OUTGOING_ID,
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                items = listOf(text(
                    text = "Common",
                    id = PERSISTENT_ID,
                    transitionTriggers = listOf(DivTransitionTrigger.DATA_CHANGE),
                )),
                transitionOut = linearFade(1000, 0.0),
            ),
            incoming = text(text = "Common", id = PERSISTENT_ID),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(500)

        composeRule.runOnIdle {
            assertEquals(listOf(OUTGOING_ID), outgoingState.snapshots.exiting.map { it.id })
            assertEquals(0f, requireNotNull(outgoingState.snapshots.snapshot(PERSISTENT_ID)).layer.alpha)
        }
    }

    @Test
    fun `common id visible to gone keeps only its exit snapshot`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = PERSISTENT_ID,
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                transitionOut = linearFade(1000, 0.0),
            ),
            incoming = text(text = INCOMING, id = PERSISTENT_ID, visibility = constant(DivVisibility.GONE)),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(500)

        composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            assertEquals(listOf(PERSISTENT_ID), outgoingState.snapshots.exiting.map { it.id })
            assertTrue(outgoingState.overlayLayers().single().layer.alpha in 0.1f..0.9f)
        }
    }

    @Test
    fun `visible siblings with the same id keep separate exits when the destination reuses it`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = container(
                items = listOf(
                    text(text = constant("First"), id = "repeated", width = fixed(40), height = fixed(16),
                        transitionOut = fade(1000L), extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID))),
                    text(text = constant("Second"), id = "repeated", width = fixed(80), height = fixed(24),
                        transitionOut = fade(1000L)),
                    container(
                        visibility = constant(DivVisibility.INVISIBLE),
                        items = listOf(text(text = "Hidden", id = "repeated", transitionOut = fade(1000L))),
                    ),
                ),
            ),
            incoming = container(
                id = "repeated", width = fixed(120), height = fixed(30),
                transitionChange = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1000L))),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        repeat(2) { composeRule.mainClock.advanceTimeByFrame() }
        composeRule.mainClock.advanceTimeBy(320)
        composeRule.runOnIdle {
            val overlays = outgoingState.overlayLayers()
            assertEquals(2, overlays.size)
            assertEquals(
                listOf(0, with(composeRule.density) { 16.dp.roundToPx() }),
                overlays.map { it.layer.topLeft.y },
            )
            assertTrue(overlays.all { it.layer.alpha in 0.01f..0.99f })
        }
    }

    @Test
    fun `returning to outgoing state releases its old snapshot`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = OUTGOING_ID,
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                transitionIn = linearFade(1000, 0.0),
                transitionOut = linearFade(1000, 0.0),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(300)
        val oldLayer = composeRule.runOnIdle { outgoingState.overlayLayers().single().layer }

        state.set(OUTGOING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.runOnIdle {
            assertTrue(oldLayer.isReleased)
            assertEquals(setOf(OUTGOING_ID), composedIds)
            assertEquals(2, tracker.compositionsOf(OUTGOING_ID))
        }
    }

    @Test
    fun `rapid retarget refreezes outgoing parent without common child`() {
        val state = renderStates(
            states = listOf(
                state(
                    stateId = OUTGOING,
                    div = container(
                        extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                        id = OUTGOING_ID,
                        items = listOf(text(text = OUTGOING, id = PERSISTENT_ID)),
                        transitionOut = linearFade(TRANSITION_DURATION_MS, 0.0),
                    ),
                ),
                state(stateId = MIDDLE, div = text(text = MIDDLE, id = MIDDLE_ID)),
                state(stateId = INCOMING, div = text(text = INCOMING, id = PERSISTENT_ID)),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to handler),
        )

        state.set(MIDDLE)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        val snapshot = outgoingState.snapshots.exiting.single()
        val firstCaptureVersion = snapshot.captureVersion

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        val recapturedSnapshot = outgoingState.snapshots.exiting.single()

        assertSame(snapshot, recapturedSnapshot)
        assertEquals(firstCaptureVersion + 1, recapturedSnapshot.captureVersion)
        assertEquals(
            0f,
            requireNotNull(outgoingState.snapshots.snapshot(PERSISTENT_ID)).layer.alpha,
        )
    }
}
