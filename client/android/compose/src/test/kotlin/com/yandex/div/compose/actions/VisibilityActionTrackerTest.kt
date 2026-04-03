package com.yandex.div.compose.actions

import com.yandex.div.test.data.disappearAction
import com.yandex.div.test.data.visibilityAction
import com.yandex.div2.DivSightAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

class VisibilityActionTrackerTest {
    private val actionHandlingContext = ActionHandlerEnvironment().context
    private val handledActions = mutableListOf<DivSightAction>()
    private val testScope = TestScope()

    private val actionHandler = mock<DivActionHandler> {
        on { handle(eq(actionHandlingContext), any<DivSightAction>()) } doAnswer {
            handledActions.add(it.getArgument(1))
            Unit
        }
    }

    private val tracker = VisibilityActionTracker(
        actionHandler = actionHandler,
        coroutineScope = testScope
    )

    @Test
    fun `onVisibilityChanged() triggers visibility action when view becomes visible`() {
        val action = visibilityAction()

        tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = true)

        verifyHandled(action)
    }

    @Test
    fun `onVisibilityChanged() does not trigger visibility action when limit is reached`() {
        val action = visibilityAction(limit = 3)

        repeat(5) {
            tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = true)
        }

        verifyHandled(action, times = 3)
    }

    @Test
    fun `onVisibilityChanged() triggers visibility action every time if limit is zero`() {
        val action = visibilityAction(limit = 0)

        repeat(5) {
            tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = true)
        }

        verifyHandled(action, times = 5)
    }

    @Test
    fun `isLimitReached() is false for new visibility action`() {
        val action = visibilityAction(limit = 1)

        assertFalse(tracker.isLimitReached(action, limit = 1))
    }

    @Test
    fun `isLimitReached() is false for infinite visibility action`() {
        val action = visibilityAction(limit = 0)

        assertFalse(tracker.isLimitReached(action, limit = 0))
    }

    @Test
    fun `isLimitReached() is false for infinite disappear action`() {
        val action = disappearAction(limit = 0)

        assertFalse(tracker.isLimitReached(action, limit = 0))
    }

    @Test
    fun `isLimitReached() is true if limit reached`() {
        val action = visibilityAction(limit = 3)

        repeat(3) {
            tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = true)
        }

        assertTrue(tracker.isLimitReached(action, limit = 3))
    }

    @Test
    fun `isLimitReached() is false if limit not reached`() {
        val action = visibilityAction(limit = 5)

        repeat(3) {
            tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = true)
        }

        assertFalse(tracker.isLimitReached(action, limit = 5))
    }

    @Test
    fun `onVisibilityChanged() triggers disappear action after delay`() = testScope.runTest {
        val action = disappearAction(delayMs = 500)

        tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = false)
        wait(300)

        verifyNoActionsHandled()

        wait(200)

        verifyHandled(action)
    }

    @Test
    fun `onVisibilityChanged() respects disappear action limit`() = testScope.runTest {
        val action = disappearAction(delayMs = 100, limit = 3)

        repeat(5) {
            tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = false)
            wait(100)
        }

        verifyHandled(action, times = 3)
    }

    @Test
    fun `onVisibilityChanged() schedules multiple disappear actions`() = testScope.runTest {
        val action1 = disappearAction(delayMs = 250, id = "action1")
        val action2 = disappearAction(delayMs = 500, id = "action2")

        tracker.onVisibilityChanged(actionHandlingContext, action1, isVisible = false)
        tracker.onVisibilityChanged(actionHandlingContext, action2, isVisible = false)
        wait(300)

        verifyHandled(action1)

        wait(300)

        verifyHandled(action2)
    }

    @Test
    fun `onVisibilityChanged() reschedules disappear action`() = testScope.runTest {
        val action = disappearAction(delayMs = 500)

        tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = false)
        wait(250)
        tracker.onVisibilityChanged(actionHandlingContext, action, isVisible = false)
        wait(250)

        verifyNoActionsHandled()

        wait(250)

        verifyHandled(action)
    }

    @Test
    fun `onVisibilityChanged() does not affect other actions when reschedules disappear action`() = testScope.runTest {
        val action1 = disappearAction(delayMs = 500, id = "action1")
        val action2 = disappearAction(delayMs = 500, id = "action2")

        tracker.onVisibilityChanged(actionHandlingContext, action1, isVisible = false)
        tracker.onVisibilityChanged(actionHandlingContext, action2, isVisible = false)
        wait(250)
        tracker.onVisibilityChanged(actionHandlingContext, action1, isVisible = false)
        wait(250)

        verifyHandled(action2)

        wait(250)

        verifyHandled(action1)
    }

    private fun verifyHandled(action: DivSightAction, times: Int = 1) {
        assertEquals(List(times) { action }, handledActions)
        handledActions.clear()
    }

    private fun verifyNoActionsHandled() {
        assertEquals(emptyList<DivSightAction>(), handledActions)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.wait(delayMs: Long) {
    advanceTimeBy(delayMs)
    runCurrent()
}
