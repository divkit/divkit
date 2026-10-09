package com.yandex.divkit.benchmark

import android.view.FrameMetrics
import android.view.Window
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

@RunWith(AndroidJUnit4::class)
class FrameTotalMetricTest {
    private val window = mock<Window>()
    private val metric = FrameTotalMetric(window)
    private val listener = argumentCaptor<Window.OnFrameMetricsAvailableListener>()

    @BeforeTest
    fun setUp() {
        verify(window).addOnFrameMetricsAvailableListener(listener.capture(), any())
    }

    @AfterTest
    fun tearDown() {
        metric.close()
    }

    @Test
    fun `total duration belongs to the drawn frame`() = runTest {
        val result = metric.start()
        metric.onDraw(100)

        report(frameTimeNanos = 100_500_000, totalNanos = 12_345_678)

        assertEquals(12_345_678.nanoseconds, result.await())
    }

    @Test
    fun `reports before the card draws are ignored`() {
        val result = metric.start()

        report(frameTimeNanos = 100_000_000)

        assertFalse(result.isCompleted)
    }

    @Test
    fun `late reports for earlier frames are ignored`() {
        val result = metric.start()
        metric.onDraw(100)

        report(frameTimeNanos = 90_000_000)

        assertFalse(result.isCompleted)
    }

    @Test
    fun `later draw does not replace the first drawn frame`() = runTest {
        val result = metric.start()
        metric.onDraw(100)
        metric.onDraw(116)

        report(frameTimeNanos = 100_000_000, totalNanos = 12_000_000)

        assertEquals(12.milliseconds, result.await())
    }

    @Test
    fun `next measurement waits for its own frame`() = runTest {
        metric.start()
        metric.onDraw(100)
        report(frameTimeNanos = 100_000_000)
        val result = metric.start()
        metric.onDraw(200)
        report(frameTimeNanos = 116_000_000, totalNanos = 5_000_000)

        report(frameTimeNanos = 200_000_000, totalNanos = 20_000_000)

        assertEquals(20.milliseconds, result.await())
    }

    @Test
    fun `missing target report fails instead of measuring another frame`() = runTest {
        val result = metric.start()
        metric.onDraw(100)

        report(frameTimeNanos = 116_000_000, droppedReports = 1)

        assertFailsWith<IllegalStateException> { result.await() }
    }

    @Test
    fun `unavailable total duration is not reported as a timing`() = runTest {
        val result = metric.start()
        metric.onDraw(100)

        report(frameTimeNanos = 100_000_000, totalNanos = -1)

        assertFailsWith<IllegalStateException> { result.await() }
    }

    @Test
    fun `closing collector cancels pending measurement and removes listener`() {
        val result = metric.start()

        metric.close()

        assertTrue(result.isCancelled)
        verify(window).removeOnFrameMetricsAvailableListener(listener.firstValue)
    }

    private fun report(
        frameTimeNanos: Long,
        totalNanos: Long = 10_000_000,
        droppedReports: Int = 0
    ) {
        val metrics = mock<FrameMetrics> {
            on { getMetric(FrameMetrics.VSYNC_TIMESTAMP) } doReturn frameTimeNanos
            on { getMetric(FrameMetrics.TOTAL_DURATION) } doReturn totalNanos
        }
        listener.firstValue.onFrameMetricsAvailable(window, metrics, droppedReports)
    }
}
