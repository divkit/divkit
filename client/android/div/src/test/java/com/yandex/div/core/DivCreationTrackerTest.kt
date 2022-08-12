package com.yandex.div.core

import android.os.SystemClock
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.reporter.HistogramReporter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivCreationTrackerTest {

    private val histogramReporter = mock<HistogramReporter>()

    @Before
    @After
    fun resetCreatedTimes() {
        DivCreationTracker.resetColdCreation()
    }

    @Test
    fun `send cold view create histogram`() {
        SystemClock.setCurrentTimeMillis(100)
        val underTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(200)
        val callType = underTest.viewCreateCallType

        underTest.sendHistograms(100, 200, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_COLD
        )
    }

    @Test
    fun `send cold view create once if views created at the same time`() {
        SystemClock.setCurrentTimeMillis(100)
        val underTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(200)
        val firstViewCreateCallType = underTest.viewCreateCallType
        val secondViewCreateCallType = underTest.viewCreateCallType

        underTest.sendHistograms(100, 200, histogramReporter, firstViewCreateCallType)
        underTest.sendHistograms(100, 200, histogramReporter, secondViewCreateCallType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_COLD
        )
        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_WARM
        )
    }

    @Test
    fun `send cool view create histogram`() {
        SystemClock.setCurrentTimeMillis(100)
        val underTest = DivCreationTracker(SystemClock.uptimeMillis())
        underTest.viewCreateCallType

        SystemClock.setCurrentTimeMillis(200)
        val newUnderTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(300)
        val callType = newUnderTest.viewCreateCallType

        newUnderTest.sendHistograms(200, 300, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_COOL
        )
    }

    @Test
    fun `send warm view create histogram`() {
        SystemClock.setCurrentTimeMillis(100)
        val underTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(200)
        underTest.viewCreateCallType

        SystemClock.setCurrentTimeMillis(300)
        val callType = underTest.viewCreateCallType

        underTest.sendHistograms(300, 400, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.View.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_WARM
        )
    }

    @Test
    fun `send cold context create histogram`() {
        SystemClock.setCurrentTimeMillis(100)
        val underTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(200)
        underTest.onContextCreationFinished()
        val callType = underTest.viewCreateCallType

        underTest.sendHistograms(100, 200, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.Context.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_COLD
        )
    }

    @Test
    fun `send cool context create histogram`() {
        SystemClock.setCurrentTimeMillis(100)
        val underTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(200)
        underTest.onContextCreationFinished()

        SystemClock.setCurrentTimeMillis(300)
        val newUnderTest = DivCreationTracker(SystemClock.uptimeMillis())

        SystemClock.setCurrentTimeMillis(400)
        newUnderTest.onContextCreationFinished()
        val callType = underTest.viewCreateCallType

        newUnderTest.sendHistograms(300, 400, histogramReporter, callType)

        verify(histogramReporter).reportDuration(
            "Div.Context.Create",
            100,
            forceCallType = HistogramCallType.CALL_TYPE_COOL
        )
    }
}
