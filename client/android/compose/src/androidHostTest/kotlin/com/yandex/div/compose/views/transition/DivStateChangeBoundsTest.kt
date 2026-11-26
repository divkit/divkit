package com.yandex.div.compose.views.transition

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.state
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivChangeBoundsTransition
import com.yandex.div2.DivChangeSetTransition
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivExtension
import com.yandex.div2.DivPager
import com.yandex.div2.DivTransitionSelector
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivStateChangeBoundsTest : StateChangeTransitionTestBase() {
    @Test
    fun `zero duration timings with the same delay are equivalent`() {
        assertEquals(boundsTiming(0, 256, LinearEasing), boundsTiming(0, 256, EaseIn))
    }

    @Test
    fun `nested bounds follow the state transition without moving the child twice`() {
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(
            duration = constant(1024L),
            startDelay = constant(128L),
            interpolator = constant(DivAnimationInterpolator.LINEAR),
        ))
        val nestedSet = DivChangeTransition.Set(DivChangeSetTransition(items = listOf(bounds)))
        val transition = DivChangeTransition.Set(DivChangeSetTransition(items = listOf(bounds, nestedSet)))
        val state = renderStateChange(
            selector = null,
            outgoing = nestedBounds(0, 100, 10, 20, null),
            incoming = nestedBounds(40, 160, 30, 60, transition),
        )
        val parent = composeRule.onNodeWithTag("parent")
        val child = composeRule.onNodeWithTag("child")
        val start = child.getUnclippedBoundsInRoot()
        assertEquals(DpRect(10.dp, 10.dp, 30.dp, 30.dp), start)

        state.set(INCOMING)
        composeRule.waitForIdle()
        repeat(3) { composeRule.mainClock.advanceTimeByFrame() }
        composeRule.mainClock.advanceTimeBy(64)
        assertEquals(start, child.getUnclippedBoundsInRoot())
        composeRule.mainClock.advanceTimeBy(576)
        val parentMiddle = parent.getUnclippedBoundsInRoot()
        val childMiddle = child.getUnclippedBoundsInRoot()
        assertTrue(parentMiddle.left.value in 18f..22f, "parent at midpoint: $parentMiddle")
        assertTrue((parentMiddle.right - parentMiddle.left).value in 127f..133f, "parent at midpoint: $parentMiddle")
        assertTrue(childMiddle.left.value in 37f..43f, "child at midpoint: $childMiddle")
        assertTrue((childMiddle.bottom - childMiddle.top).value in 38f..42f, "child at midpoint: $childMiddle")

        composeRule.mainClock.advanceTimeBy(640)
        assertEquals(DpRect(40.dp, 40.dp, 200.dp, 200.dp), parent.getUnclippedBoundsInRoot())
        assertEquals(DpRect(70.dp, 70.dp, 130.dp, 130.dp), child.getUnclippedBoundsInRoot())
    }

    @Test
    fun `reversing a state change starts from the displayed bounds`() {
        val transition = DivChangeTransition.Bounds(DivChangeBoundsTransition(
            duration = constant(1024L),
            startDelay = constant(128L),
            interpolator = constant(DivAnimationInterpolator.LINEAR),
        ))
        val state = renderStateChange(
            selector = null,
            outgoing = nestedBounds(40, 160, 30, 60, transition),
            incoming = nestedBounds(0, 100, 10, 20, transition),
        )
        val child = composeRule.onNodeWithTag("child")
        val initial = child.getUnclippedBoundsInRoot()
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(640)
        val interrupted = child.getUnclippedBoundsInRoot()
        assertTrue(interrupted.left.value in 37f..48f, "child before reversal: $interrupted")

        state.set(OUTGOING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(96)
        val reversed = child.getUnclippedBoundsInRoot()
        assertEquals(interrupted.left.value, reversed.left.value, 2f)
        assertEquals(interrupted.right.value, reversed.right.value, 3f)
        composeRule.mainClock.advanceTimeBy(1280)
        assertEquals(initial, child.getUnclippedBoundsInRoot())
    }

    @Test
    fun `a selector excluding state changes applies the new bounds immediately`() {
        val state = renderStateChange(
            selector = DivTransitionSelector.DATA_CHANGE,
            outgoing = container(id = "box", width = fixed(40), height = fixed(40)),
            incoming = container(id = "box", width = fixed(120), height = fixed(120),
                transitionChange = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1024L)))),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(64)
        assertEquals(120.dp, composeRule.onNodeWithTag("box").getUnclippedBoundsInRoot().right)
    }

    @Test
    fun `moving between a gallery item and a regular layout starts at the displayed position`() {
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(
            duration = constant(512L), startDelay = constant(256L),
        ))
        val state = renderStateChange(
            selector = null,
            outgoing = gallery(
                width = fixed(180), height = fixed(40), itemSpacing = constant(0L),
                items = listOf(
                    container(width = fixed(64), height = fixed(24)),
                    container(id = "shared", width = fixed(72), height = fixed(24)),
                ),
            ),
            incoming = container(id = "shared", width = fixed(40), height = fixed(24), transitionChange = bounds),
        )
        val node = composeRule.onNodeWithTag("shared")
        val galleryBounds = node.getUnclippedBoundsInRoot()
        assertEquals(64.dp, galleryBounds.left)

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(128)
        assertEquals(galleryBounds, node.getUnclippedBoundsInRoot())
        composeRule.mainClock.advanceTimeBy(896)
        val outside = node.getUnclippedBoundsInRoot()
        assertEquals(0.dp, outside.left)

        state.set(OUTGOING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(128)
        assertEquals(outside, node.getUnclippedBoundsInRoot())
        composeRule.mainClock.advanceTimeBy(896)
        assertEquals(galleryBounds, node.getUnclippedBoundsInRoot())
    }

    @Test
    fun `a set with conflicting timings reports the conflict`() {
        val transition = DivChangeTransition.Set(DivChangeSetTransition(items = listOf(
            DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(500L))),
            DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(200L))),
        )))
        val state = renderStateChange(
            selector = null,
            outgoing = container(id = "box", width = fixed(40), height = fixed(40)),
            incoming = container(id = "box", width = fixed(120), height = fixed(120), transitionChange = transition),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(96)
        assertEquals(120.dp, composeRule.onNodeWithTag("box").getUnclippedBoundsInRoot().right)
        assertEquals(listOf("Conflicting change_bounds timings for element 'box'"), reporter.warnings)
    }

    @Test
    fun `outgoing timing moves the parent and an undeclared child follows its layout`() {
        val state = renderStateChange(
            selector = null,
            outgoing = nestedBounds(0, 100, 10, 20, DivChangeTransition.Bounds(DivChangeBoundsTransition(
                duration = constant(1024L),
                interpolator = constant(DivAnimationInterpolator.LINEAR),
            )), childTransition = null),
            incoming = nestedBounds(40, 160, 30, 60, null),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(544)
        val parent = composeRule.onNodeWithTag("parent").getUnclippedBoundsInRoot()
        val child = composeRule.onNodeWithTag("child").getUnclippedBoundsInRoot()
        assertTrue(parent.left.value in 18f..22f, "parent at midpoint: $parent")
        assertEquals(30.dp, child.left - parent.left)
        assertEquals(60.dp, child.right - child.left)
    }

    @Test
    fun `a child stays at its destination while its parent finishes moving`() {
        val slow = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1024L)))
        val fast = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(256L)))
        val state = renderStateChange(
            selector = null,
            outgoing = nestedBounds(0, 100, 10, 20, slow),
            incoming = nestedBounds(40, 160, 30, 60, slow, childTransition = fast),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(544)
        val child = composeRule.onNodeWithTag("child").getUnclippedBoundsInRoot()
        assertEquals(DpRect(70.dp, 70.dp, 130.dp, 130.dp), child)
    }

    @Test
    fun `destination timing takes precedence and a delayed zero duration snaps only after the delay`() {
        val state = renderStateChange(
            selector = null,
            outgoing = container(id = "box", width = fixed(40), height = fixed(40),
                transitionChange = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1000L)))),
            incoming = container(id = "box", width = fixed(120), height = fixed(120),
                transitionChange = DivChangeTransition.Bounds(DivChangeBoundsTransition(
                    duration = constant(0L), startDelay = constant(256L),
                ))),
        )
        val box = composeRule.onNodeWithTag("box")
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(128)
        assertEquals(40.dp, box.getUnclippedBoundsInRoot().right)
        composeRule.mainClock.advanceTimeBy(224)
        assertEquals(120.dp, box.getUnclippedBoundsInRoot().right)
    }

    @Test
    fun `a child retained by an item builder has no outgoing overlay`() {
        val tracker = TrackingExtensionHandler(mutableSetOf())
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1024L)))
        val state = renderStateChange(
            selector = null,
            outgoing = container(items = listOf(
                container(id = "box", width = fixed(40), height = fixed(40), transitionOut = fade(1024L)),
                container(
                    id = "leaving", width = fixed(20), height = fixed(20),
                    extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                    transitionOut = fade(1024L),
                ),
            )),
            incoming = container(itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()),
                prototypes = listOf(prototype(
                    id = constant("box"),
                    div = container(width = fixed(120), height = fixed(120), transitionChange = bounds),
                )),
            )),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to tracker),
        )
        val outgoingState = tracker.transitionStateOf("leaving")
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(544)
        val right = composeRule.onNodeWithTag("box").getUnclippedBoundsInRoot().right
        assertTrue(right > 40.dp && right < 120.dp)
        val exit = outgoingState.snapshots.exiting.single()
        assertEquals("leaving", exit.id)
        assertEquals(1, exit.captureVersion)
        assertEquals(1, outgoingState.overlayLayers().size)
    }

    @Test
    fun `a builder child ends an earlier exit across a state without ids`() {
        val tracker = TrackingExtensionHandler(mutableSetOf())
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(512L)))
        val states = renderStates(
            states = listOf(
                state(stateId = OUTGOING, div = container(
                    id = "box", width = fixed(40), height = fixed(40), transitionOut = fade(1024L),
                    extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                )),
                state(stateId = MIDDLE, div = container(width = fixed(40), height = fixed(40))),
                state(stateId = INCOMING, div = container(itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject()),
                    prototypes = listOf(prototype(
                        id = constant("box"),
                        div = container(width = fixed(80), height = fixed(40), transitionChange = bounds),
                    )),
                ))),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to tracker),
        )
        val source = tracker.transitionStateOf("box")
        states.set(MIDDLE)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(160)
        val layer = source.overlayLayers().single().layer
        assertTrue(layer.alpha in 0.01f..0.99f)

        states.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(128)
        composeRule.onNodeWithTag("box").assertExists()
        assertTrue(source.overlayLayers().isEmpty())
        assertTrue(layer.isReleased)
    }

    @Test
    fun `a centered wrap content scene follows the visible bounds through delay and easing`() {
        val transition = DivChangeTransition.Bounds(DivChangeBoundsTransition(
            duration = constant(1024L),
            startDelay = constant(256L),
            interpolator = constant(DivAnimationInterpolator.EASE_IN),
        ))
        val state = renderStateChange(
            selector = null,
            outgoing = container(id = "box", width = fixed(180), height = fixed(180)),
            incoming = container(id = "box", width = fixed(40), height = fixed(40), transitionChange = transition),
            stateWidth = wrapContent(),
            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
        )
        val box = composeRule.onNodeWithTag("box")
        val initial = box.fetchSemanticsNode().boundsInRoot
        state.set(INCOMING)
        composeRule.waitForIdle()
        repeat(3) { composeRule.mainClock.advanceTimeByFrame() }
        composeRule.mainClock.advanceTimeBy(128)
        assertEquals(initial, box.fetchSemanticsNode().boundsInRoot)

        composeRule.mainClock.advanceTimeBy(384)
        val visibleHeight = with(composeRule.density) { box.fetchSemanticsNode().boundsInRoot.height.toDp() }
        assertTrue(visibleHeight in 159.dp..173.dp, "visible height after a quarter of ease_in: $visibleHeight")
        composeRule.mainClock.advanceTimeBy(1024)
        val end = box.fetchSemanticsNode().boundsInRoot
        assertEquals(initial.center.x, end.center.x)
        assertEquals(40.dp, with(composeRule.density) { end.height.toDp() })
    }

    @Test
    fun `two destination elements cannot both animate from the same source`() {
        val timing = DivChangeTransition.Bounds(DivChangeBoundsTransition(
            duration = constant(512L), startDelay = constant(256L),
        ))
        val tracker = TrackingExtensionHandler(mutableSetOf())
        val state = renderStateChange(
            selector = null,
            outgoing = container(
                id = "parent", transitionOut = fade(1024L),
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
                items = listOf(container(id = "box", width = fixed(20), height = fixed(20), transitionChange = timing)),
            ),
            incoming = container(items = listOf(
                container(id = "box", width = fixed(60), height = fixed(20)),
                container(id = "box", width = fixed(100), height = fixed(20)),
            )),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to tracker),
        )
        val source = tracker.transitionStateOf("parent")
        state.set(INCOMING)
        composeRule.waitForIdle()
        repeat(2) { composeRule.mainClock.advanceTimeByFrame() }
        composeRule.mainClock.advanceTimeBy(128)
        val boxes = composeRule.onAllNodesWithTag("box").fetchSemanticsNodes()
        val widths = boxes.map { with(composeRule.density) { it.size.width.toDp() } }
        assertEquals(listOf(60.dp, 100.dp), widths)
        assertTrue(source.overlayLayers().single().layer.alpha in 0.01f..0.99f)
        assertEquals(1f, requireNotNull(source.snapshots.snapshot("box")).layer.alpha)
    }

    @Test
    fun `a matching id outside the incoming gallery viewport keeps its exit`() {
        val tracker = TrackingExtensionHandler(mutableSetOf())
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1024L)))
        val state = renderStateChange(
            selector = null,
            outgoing = container(
                id = "shared", width = fixed(60), height = fixed(22), transitionOut = fade(1024L),
                extensions = listOf(DivExtension(id = TRACKING_EXTENSION_ID)),
            ),
            incoming = gallery(
                width = fixed(120), height = fixed(40), itemSpacing = constant(0L),
                items = List(12) { container(width = fixed(60), height = fixed(22)) } +
                    container(id = "shared", width = fixed(60), height = fixed(22), transitionChange = bounds),
            ),
            extensionHandlers = mapOf(TRACKING_EXTENSION_ID to tracker),
        )
        val source = tracker.transitionStateOf("shared")
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(320)
        composeRule.onNodeWithTag("shared").assertDoesNotExist()
        assertTrue(source.overlayLayers().single().layer.alpha in 0.01f..0.99f)
    }

    @Test
    fun `an element moving onto the initial pager page animates its bounds`() {
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(
            duration = constant(1024L),
            interpolator = constant(DivAnimationInterpolator.LINEAR),
        ))
        val state = renderStateChange(
            selector = null,
            outgoing = container(id = "shared", width = fixed(40), height = fixed(24)),
            incoming = pager(
                width = fixed(180), height = fixed(40), scrollAxisAlignment = DivPager.ItemAlignment.START,
                items = listOf(container(id = "shared", width = fixed(120), height = fixed(24), transitionChange = bounds)),
            ),
        )
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(544)
        val shared = composeRule.onNodeWithTag("shared").getUnclippedBoundsInRoot()
        assertTrue((shared.right - shared.left).value in 76f..84f, "Width at midpoint: $shared")
    }

    @Test
    fun `an element first shown after the state switch uses its current geometry`() {
        val visibility = Variable.StringVariable("late", "gone")
        val bounds = DivChangeTransition.Bounds(DivChangeBoundsTransition(duration = constant(1024L)))
        val state = renderStateChange(
            selector = null,
            outgoing = container(items = listOf(
                container(id = "box", width = fixed(20), height = fixed(20)),
                container(id = "moving", width = fixed(20), height = fixed(20)),
            )),
            incoming = container(items = listOf(
                container(
                    id = "box", width = fixed(120), height = fixed(20), transitionChange = bounds,
                    visibility = visibilityExpression("@{late}"),
                ),
                container(id = "moving", width = fixed(140), height = fixed(20), transitionChange = bounds),
            )),
            extraVariables = listOf(visibility),
        )
        val box = composeRule.onNodeWithTag("box")
        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(320)
        box.assertDoesNotExist()
        visibility.set("visible")
        composeRule.mainClock.advanceTimeBy(32)
        val shown = box.getUnclippedBoundsInRoot()
        assertEquals(120.dp, shown.right - shown.left)
    }

    private fun nestedBounds(
        parentOffset: Int,
        parentSize: Int,
        childOffset: Int,
        childSize: Int,
        transition: DivChangeTransition?,
        childTransition: DivChangeTransition? = transition,
    ) = container(
        id = "parent",
        width = fixed(parentSize),
        height = fixed(parentSize),
        margins = insets(start = parentOffset, top = parentOffset),
        transitionChange = transition,
        items = listOf(container(
            id = "child",
            width = fixed(childSize),
            height = fixed(childSize),
            margins = insets(start = childOffset, top = childOffset),
            transitionChange = childTransition,
        )),
    )
}
