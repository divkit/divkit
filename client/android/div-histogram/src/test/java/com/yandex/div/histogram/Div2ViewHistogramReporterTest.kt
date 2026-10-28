package com.yandex.div.histogram

import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.internal.util.Clock
import com.yandex.div.internal.util.UiThreadHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class Div2ViewHistogramReporterTest {
    private val renderConfig = RenderConfiguration(
        measureFilter = { true },
        layoutFilter = { true },
        drawFilter = { true },
        totalFilter = { true },
        prepareFilter = { true },
    )
    private val histogramReporter = mock<HistogramReporter>()
    private val clock = mock<Clock>()

    private val underTest = Div2ViewHistogramReporter(
        histogramReporter = { histogramReporter },
        renderConfig = { renderConfig },
    ).also {
        Clock.setForTests(clock)
        UiThreadHandler.setInTestMode(true)
        it.component = TEST_COMPONENT
    }

    @AfterTest
    fun tearDown() {
        Clock.setForTests(null)
        UiThreadHandler.setInTestMode(false)
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

    @Test
    fun `report prepare once per render`() {
        whenever(clock.uptimeMicros).doReturn(100L)
        underTest.onRenderStarted()
        whenever(clock.uptimeMicros).doReturn(150L)
        underTest.onPrepareFinished()
        whenever(clock.uptimeMicros).doReturn(200L)
        underTest.onPrepareFinished()

        verify(histogramReporter).reportDuration(
            DIV_RENDER_PREPARE,
            50L,
            TEST_COMPONENT,
            filter = renderConfig.prepareFilter
        )
        verify(histogramReporter, times(1))
            .reportDuration(eq(DIV_RENDER_PREPARE), any(), anyOrNull(), anyOrNull(), any())
    }

    @Test
    fun `prepare is not reported without render start`() {
        underTest.onPrepareFinished()

        verifyNotReported(DIV_RENDER_PREPARE)
    }

    @Test
    fun `report binding parts after binding`() {
        underTest.onRenderStarted()

        whenever(clock.uptimeMicros).doReturn(100L)
        underTest.onBindingStarted()
        whenever(clock.uptimeMicros).doReturn(200L)
        underTest.onBindViewsStarted()
        whenever(clock.uptimeMicros).doReturn(500L)
        underTest.onBindViewsFinished()
        whenever(clock.uptimeMicros).doReturn(600L, 900L)
        underTest.onBindingFinished()

        verify(histogramReporter).reportDuration(DIV_BINDING_HISTOGRAM, 500L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_BINDING_SETUP, 100L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_BINDING_VIEWS, 300L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_BINDING_FINISH, 100L, TEST_COMPONENT)
    }

    @Test
    fun `binding parts are not reported without views timing`() {
        underTest.onRenderStarted()
        underTest.onBindViewsStarted()
        underTest.onBindViewsFinished()

        underTest.onBindingStarted()
        underTest.onBindingFinished()

        verify(histogramReporter).reportDuration(DIV_BINDING_HISTOGRAM, 0L, TEST_COMPONENT)
        verifyNotReported(*BINDING_PARTS)
    }

    @Test
    fun `binding parts are not reported on rebinding`() {
        underTest.onRenderStarted()

        underTest.onRebindingStarted()
        underTest.onBindViewsStarted()
        underTest.onBindViewsFinished()
        underTest.onRebindingFinished()

        verify(histogramReporter).reportDuration(DIV_REBINDING_HISTOGRAM, 0L, TEST_COMPONENT)
        verifyNotReported(*BINDING_PARTS)
    }

    @Test
    fun `report async binding phases`() {
        whenever(clock.uptimeMicros).doReturn(100L)
        val timer = underTest.onAsyncBindingQueued()
        whenever(clock.uptimeMicros).doReturn(150L)
        timer.onBackgroundStarted()
        underTest.onRenderStarted()
        whenever(clock.uptimeMicros).doReturn(400L)
        timer.onBackgroundFinished()
        whenever(clock.uptimeMicros).doReturn(450L)
        timer.onMainStarted()
        whenever(clock.uptimeMicros).doReturn(500L)
        timer.onMainFinished()
        whenever(clock.uptimeMicros).doReturn(900L)
        timer.report()

        verify(histogramReporter).reportDuration(DIV_ASYNC_BINDING_BACKGROUND_WAIT, 50L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_ASYNC_BINDING_BACKGROUND, 250L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_ASYNC_BINDING_MAIN_WAIT, 50L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_ASYNC_BINDING_MAIN, 50L, TEST_COMPONENT)
        verify(histogramReporter).reportDuration(DIV_ASYNC_BINDING_TOTAL, 400L, TEST_COMPONENT)
    }

    @Test
    fun `async binding without new data is not reported`() {
        val timer = underTest.onAsyncBindingQueued()
        timer.onBackgroundStarted()
        timer.onBackgroundFinished()
        timer.onMainStarted()
        timer.onMainFinished()
        timer.report()

        verifyNotReported(*ASYNC_BINDING_PHASES)
    }

    @Test
    fun `async binding not applied on main thread is not reported`() {
        val timer = underTest.onAsyncBindingQueued()
        timer.onBackgroundStarted()
        underTest.onRenderStarted()
        timer.onBackgroundFinished()
        timer.report()

        verifyNotReported(*ASYNC_BINDING_PHASES)
    }

    @Test
    fun `render started outside of background phase is not attributed to async binding`() {
        val timer = underTest.onAsyncBindingQueued()
        underTest.onRenderStarted()
        timer.onBackgroundStarted()
        timer.onBackgroundFinished()
        underTest.onRenderStarted()
        timer.onMainStarted()
        timer.onMainFinished()
        timer.report()

        verifyNotReported(*ASYNC_BINDING_PHASES)
    }

    @Test
    fun `async binding is not reported when binding of views is deferred`() {
        val timer = underTest.onAsyncBindingQueued()
        timer.onBackgroundStarted()
        underTest.onRenderStarted()
        underTest.onBindingDeferred()
        timer.onBackgroundFinished()
        timer.onMainStarted()
        timer.onMainFinished()
        timer.report()

        verifyNotReported(*ASYNC_BINDING_PHASES)
    }

    @Test
    fun `binding deferred outside of background phase does not cancel async binding`() {
        val timer = underTest.onAsyncBindingQueued()
        timer.onBackgroundStarted()
        underTest.onRenderStarted()
        timer.onBackgroundFinished()
        underTest.onBindingDeferred()
        timer.onMainStarted()
        timer.onMainFinished()
        timer.report()

        verify(histogramReporter).reportDuration(DIV_ASYNC_BINDING_TOTAL, 0L, TEST_COMPONENT)
    }

    @Test
    fun `cancelled async binding is not reported`() {
        val timer = underTest.onAsyncBindingQueued()
        timer.onBackgroundStarted()
        underTest.onRenderStarted()
        timer.onBackgroundFinished()
        timer.onMainStarted()
        timer.onMainFinished()
        timer.cancel()
        timer.report()

        verifyNotReported(*ASYNC_BINDING_PHASES)
    }

    private fun verifyNotReported(vararg histogramNames: String) {
        histogramNames.forEach { histogramName ->
            verify(histogramReporter, never()).reportDuration(eq(histogramName), any(), any(), any(), any())
        }
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
        private val BINDING_PARTS = arrayOf(DIV_BINDING_SETUP, DIV_BINDING_VIEWS, DIV_BINDING_FINISH)
        private val ASYNC_BINDING_PHASES = arrayOf(
            DIV_ASYNC_BINDING_BACKGROUND_WAIT,
            DIV_ASYNC_BINDING_BACKGROUND,
            DIV_ASYNC_BINDING_MAIN_WAIT,
            DIV_ASYNC_BINDING_MAIN,
            DIV_ASYNC_BINDING_TOTAL,
        )
    }
}
