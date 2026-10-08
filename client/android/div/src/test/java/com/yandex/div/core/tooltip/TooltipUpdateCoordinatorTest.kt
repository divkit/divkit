package com.yandex.div.core.tooltip

import com.yandex.div.core.Disposable
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class TooltipUpdateCoordinatorTest {

    private val updateCallback = argumentCaptor<() -> Unit>()
    private val anchorUpdateCallback = argumentCaptor<() -> Unit>()
    private val updateSubscription = mock<Disposable>()
    private val anchorUpdateSubscription = mock<Disposable>()
    private val events = mock<TooltipUpdateEvents> {
        on { subscribeToUpdates(updateCallback.capture()) } doReturn updateSubscription
        on { subscribeToAnchorUpdates(anchorUpdateCallback.capture()) } doReturn anchorUpdateSubscription
    }
    private val recomputePosition = mock<() -> Unit>()
    private val underTest = TooltipUpdateCoordinator(
        events = events,
        recomputePosition = recomputePosition,
    )

    @Test
    fun `start subscribes to window and anchor updates`() {
        underTest.start()

        verify(events).subscribeToUpdates(any())
        verify(events).subscribeToAnchorUpdates(any())
        verify(recomputePosition, never()).invoke()
    }

    @Test
    fun `window change recomputes position`() {
        underTest.start()

        updateCallback.lastValue()

        verify(recomputePosition).invoke()
    }

    @Test
    fun `anchor change recomputes position`() {
        underTest.start()

        anchorUpdateCallback.lastValue()

        verify(recomputePosition).invoke()
    }

    @Test
    fun `configuration change restarts anchor tracking and recomputes position`() {
        underTest.start()

        underTest.onConfigurationChanged()

        verify(anchorUpdateSubscription).close()
        verify(events, times(2)).subscribeToAnchorUpdates(any())
        verify(recomputePosition).invoke()
    }

    @Test
    fun `stop cancels subscriptions and ignores delivered callbacks`() {
        underTest.start()
        val lastAnchorUpdate = anchorUpdateCallback.lastValue

        underTest.stop()
        lastAnchorUpdate()

        verify(updateSubscription).close()
        verify(anchorUpdateSubscription).close()
        verify(recomputePosition, never()).invoke()
    }

    @Test
    fun `coordinator can start again after stop`() {
        underTest.start()
        underTest.stop()

        underTest.start()
        updateCallback.lastValue()

        verify(events, times(2)).subscribeToUpdates(any())
        verify(recomputePosition).invoke()
    }

    @Test
    fun `start and stop are idempotent`() {
        underTest.start()
        underTest.start()
        underTest.stop()
        underTest.stop()

        verify(events).subscribeToUpdates(any())
        verify(events).subscribeToAnchorUpdates(any())
        verify(updateSubscription).close()
        verify(anchorUpdateSubscription).close()
    }
}
