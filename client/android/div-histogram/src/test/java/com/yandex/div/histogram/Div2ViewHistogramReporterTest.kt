package com.yandex.div.histogram

import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.internal.util.Clock
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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
    private val clock = mock<Clock>()

    private val underTest = Div2ViewHistogramReporter(
        histogramReporter = { histogramReporter },
        renderConfig = { renderConfig },
    ).also {
        Clock.setForTests(clock)
        it.component = TEST_COMPONENT
    }

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

        whenever(clock.uptimeMicros).doReturn(100L)
        underTest.onMeasureStarted()
        whenever(clock.uptimeMicros).doReturn(200L)
        underTest.onMeasureFinished()

        underTest.onMeasureStarted()
        whenever(clock.uptimeMicros).doReturn(300L)
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

        whenever(clock.uptimeMicros).doReturn(100L)
        underTest.onLayoutStarted()
        whenever(clock.uptimeMicros).doReturn(200L)
        underTest.onLayoutFinished()

        underTest.onLayoutStarted()
        whenever(clock.uptimeMicros).doReturn(300L)
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

        whenever(clock.uptimeMicros).doReturn(2000L)
        underTest.onMeasureStarted()
        whenever(clock.uptimeMicros).doReturn(2100L)
        underTest.onMeasureFinished()

        underTest.onLayoutStarted()
        whenever(clock.uptimeMicros).doReturn(2300L)
        underTest.onLayoutFinished()

        underTest.onDrawStarted()
        whenever(clock.uptimeMicros).doReturn(2600L)
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

        whenever(clock.uptimeMicros).doReturn(100L)
        underTest.run { if (isRebinding) onRebindingStarted() else onBindingStarted() }
        whenever(clock.uptimeMicros).doReturn(200L)
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
        whenever(clock.uptimeMicros).doReturn(400L)
        underTest.onMeasureFinished()

        underTest.onLayoutStarted()
        whenever(clock.uptimeMicros).doReturn(700L)
        underTest.onLayoutFinished()

        underTest.onDrawStarted()
        whenever(clock.uptimeMicros).doReturn(1100L)
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
