package com.yandex.div.core

import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.internal.util.Clock
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivCreationTrackerTest {

    private val histogramReporter = mock<HistogramReporter>()
    private var currentUptimeMicros = 0L

    private val mockClock = mock<Clock> {
        on { uptimeMicros } doAnswer { currentUptimeMicros }
    }

    @Before
    fun setUp() {
        Clock.setForTests(mockClock)
    }

    @Before
    @After
    fun resetCreatedTimes() {
        DivCreationTracker.resetColdCreation()
        currentUptimeMicros = 0L
    }

    @After
    fun tearDown() {
        Clock.setForTests(null)
    }

    @Test
    fun `send cold view create histogram`() {
        currentUptimeMicros = 100_000L
        val underTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 200_000L
        val callType = underTest.viewCreateCallType

        underTest.sendHistograms(100_000L, 200_000L, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_COLD
        )
    }

    @Test
    fun `send cold view create once if views created at the same time`() {
        currentUptimeMicros = 100_000L
        val underTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 200_000L
        val firstViewCreateCallType = underTest.viewCreateCallType
        val secondViewCreateCallType = underTest.viewCreateCallType

        underTest.sendHistograms(100_000L, 200_000L, histogramReporter, firstViewCreateCallType)
        underTest.sendHistograms(100_000L, 200_000L, histogramReporter, secondViewCreateCallType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_COLD
        )
        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_WARM
        )
    }

    @Test
    fun `send cool view create histogram`() {
        currentUptimeMicros = 100_000L
        val underTest = DivCreationTracker(currentUptimeMicros)
        underTest.viewCreateCallType

        currentUptimeMicros = 200_000L
        val newUnderTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 300_000L
        val callType = newUnderTest.viewCreateCallType

        newUnderTest.sendHistograms(200_000L, 300_000L, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_COOL
        )
    }

    @Test
    fun `send warm view create histogram`() {
        currentUptimeMicros = 100_000L
        val underTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 200_000L
        underTest.viewCreateCallType

        currentUptimeMicros = 300_000L
        val callType = underTest.viewCreateCallType

        underTest.sendHistograms(300_000L, 400_000L, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_WARM
        )
    }

    @Test
    fun `send cold context create histogram`() {
        currentUptimeMicros = 100_000L
        val underTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 200_000L
        underTest.onContextCreationFinished()
        val callType = underTest.viewCreateCallType

        underTest.sendHistograms(100_000L, 200_000L, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.Context.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_COLD
        )
    }

    @Test
    fun `send cool context create histogram`() {
        currentUptimeMicros = 100_000L
        val underTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 200_000L
        underTest.onContextCreationFinished()

        currentUptimeMicros = 300_000L
        val newUnderTest = DivCreationTracker(currentUptimeMicros)

        currentUptimeMicros = 400_000L
        newUnderTest.onContextCreationFinished()
        val callType = underTest.viewCreateCallType

        newUnderTest.sendHistograms(300_000L, 400_000L, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.Context.Create",
            100_000L,
            forceCallType = HistogramCallType.CALL_TYPE_COOL
        )
    }
}
