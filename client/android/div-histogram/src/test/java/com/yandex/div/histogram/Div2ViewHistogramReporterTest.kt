package com.yandex.div.histogram

import android.os.SystemClock
import com.yandex.div.histogram.reporter.HistogramReporter
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Div2ViewHistogramReporterTest {
    private val renderConfig = RenderConfiguration(
        measureFilter = { true },
        layoutFilter = { true },
        drawFilter = { true },
        totalFilter = { true },
    )
    private val histogramReporter = mock<HistogramReporter>()

    private val underTest = Div2ViewHistogramReporter({ histogramReporter }) { renderConfig }
        .also { it.component = TEST_COMPONENT }

    @Test
    fun `report rendering on binding`() {
        verifyReportRendering(isRebinding = false)
    }

    @Test
    fun `report rendering on rebinding`() {
        verifyReportRendering(isRebinding = true)
    }

    @Test
    fun `accumulate measure durations on report rendering`() {
        underTest.onRenderStarted()

        SystemClock.setCurrentTimeMillis(100L)
        underTest.onMeasureStarted()
        SystemClock.setCurrentTimeMillis(200L)
        underTest.onMeasureFinished()

        underTest.onMeasureStarted()
        SystemClock.setCurrentTimeMillis(300L)
        underTest.onMeasureFinished()

        underTest.onDrawFinished()

        verify(histogramReporter).reportDuration(
            DIV_RENDER_MEASURE,
            200L,
            TEST_COMPONENT,
            filter = renderConfig.measureFilter
        )
    }

    @Test
    fun `accumulate layout durations on report rendering`() {
        underTest.onRenderStarted()

        SystemClock.setCurrentTimeMillis(100L)
        underTest.onLayoutStarted()
        SystemClock.setCurrentTimeMillis(200L)
        underTest.onLayoutFinished()

        underTest.onLayoutStarted()
        SystemClock.setCurrentTimeMillis(300L)
        underTest.onLayoutFinished()

        underTest.onDrawFinished()

        verify(histogramReporter).reportDuration(
            DIV_RENDER_LAYOUT,
            200L,
            TEST_COMPONENT,
            filter = renderConfig.layoutFilter
        )
    }

    @Test
    fun `redraw does not cause metrics reporting`() {
        verifyReportRendering(isRebinding = false)
        clearInvocations(histogramReporter)

        SystemClock.setCurrentTimeMillis(2000L)
        underTest.onMeasureStarted()
        SystemClock.setCurrentTimeMillis(2100L)
        underTest.onMeasureFinished()

        underTest.onLayoutStarted()
        SystemClock.setCurrentTimeMillis(2300L)
        underTest.onLayoutFinished()

        underTest.onDrawStarted()
        SystemClock.setCurrentTimeMillis(2600L)
        underTest.onDrawFinished()

        verify(histogramReporter, never()).reportDuration(
            any(),
            any(),
            eq(TEST_COMPONENT),
            any(),
            any()
        )
    }


    private fun verifyReportRendering(isRebinding: Boolean) {
        underTest.onRenderStarted()

        SystemClock.setCurrentTimeMillis(100L)
        underTest.run { if (isRebinding) onRebindingStarted() else onBindingStarted() }
        SystemClock.setCurrentTimeMillis(200L)
        underTest.run {
            if (isRebinding) {
                onRebindingFinished()
                verify(histogramReporter).reportDuration(
                    DIV_REBINDING_HISTOGRAM,
                    100L,
                    TEST_COMPONENT
                )
            } else {
                onBindingFinished()
                verify(histogramReporter).reportDuration(
                    DIV_BINDING_HISTOGRAM,
                    100L,
                    TEST_COMPONENT
                )
            }
        }

        underTest.onMeasureStarted()
        SystemClock.setCurrentTimeMillis(400L)
        underTest.onMeasureFinished()

        underTest.onLayoutStarted()
        SystemClock.setCurrentTimeMillis(700L)
        underTest.onLayoutFinished()

        underTest.onDrawStarted()
        SystemClock.setCurrentTimeMillis(1100L)
        underTest.onDrawFinished()

        verify(histogramReporter).reportDuration(
            DIV_RENDER_TOTAL,
            1000L,
            TEST_COMPONENT,
            filter = renderConfig.totalFilter
        )
        verify(histogramReporter).reportDuration(
            DIV_RENDER_MEASURE,
            200L,
            TEST_COMPONENT,
            filter = renderConfig.measureFilter
        )
        verify(histogramReporter).reportDuration(
            DIV_RENDER_LAYOUT,
            300L,
            TEST_COMPONENT,
            filter = renderConfig.layoutFilter
        )
        verify(histogramReporter).reportDuration(
            DIV_RENDER_DRAW,
            400L,
            TEST_COMPONENT,
            filter = renderConfig.drawFilter
        )
    }

    private companion object {
        private const val TEST_COMPONENT = "TestComponent"
    }
}
