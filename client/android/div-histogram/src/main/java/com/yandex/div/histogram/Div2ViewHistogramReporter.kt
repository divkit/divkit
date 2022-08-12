package com.yandex.div.histogram

import android.os.SystemClock
import android.view.View
import androidx.annotation.MainThread
import com.yandex.div.core.util.KAssert
import com.yandex.div.histogram.metrics.RenderMetrics
import com.yandex.div.histogram.reporter.HistogramReporter

@MainThread
class Div2ViewHistogramReporter(
    private val histogramReporter: () -> HistogramReporter,
    private val renderConfig: () -> RenderConfiguration,
) {

    var component: String? = null
    private var renderStarted = false

    private var bindingStartedTime: Long? = null
    private var rebindingStartedTime: Long? = null

    private var measureStartedTime: Long? = null
    private var layoutStartedTime: Long? = null
    private var drawStartedTime: Long? = null

    private val renderMetrics by lazy(LazyThreadSafetyMode.NONE, ::RenderMetrics)

    private val currentUptime: Long
        get() = SystemClock.uptimeMillis()

    fun onRenderStarted() {
        renderStarted = true
    }

    fun onBindingStarted() {
        bindingStartedTime = currentUptime
    }

    fun onBindingFinished() {
        reportHistogram(bindingStartedTime, DIV_BINDING_HISTOGRAM, renderMetrics::binding)
        bindingStartedTime = null
    }

    fun onRebindingStarted() {
        rebindingStartedTime = currentUptime
    }

    fun onRebindingFinished() {
        reportHistogram(rebindingStartedTime, DIV_REBINDING_HISTOGRAM, renderMetrics::rebinding)
        rebindingStartedTime = null
    }

    fun onMeasureStarted() {
        measureStartedTime = currentUptime
    }

    fun onMeasureFinished() {
        measureStartedTime?.toTimePassed()?.let(renderMetrics::addMeasure)
    }

    fun onLayoutStarted() {
        layoutStartedTime = currentUptime
    }

    fun onLayoutFinished() {
        layoutStartedTime?.toTimePassed()?.let(renderMetrics::addLayout)
    }

    fun onDrawStarted() {
        drawStartedTime = currentUptime
    }

    fun onDrawFinished() {
        drawStartedTime?.toTimePassed()?.let(renderMetrics::addDraw)
        if (renderStarted) {
            reportRenderMetrics(renderMetrics)
        }
        resetRenderMetrics()
    }

    private inline fun reportHistogram(
        startTime: Long?,
        histogramName: String,
        onDuration: (Long) -> Unit
    ) {
        if (startTime == null) {
            KAssert.fail { "start time of $histogramName is null" }
            return
        }
        val duration = currentUptime - startTime
        onDuration(duration)
        histogramReporter().reportDuration(histogramName, duration, component)
    }

    private fun reportRenderMetrics(renderMetrics: RenderMetrics) {
        with(histogramReporter()) {
            val renderConfig = renderConfig()
            reportDuration(
                DIV_RENDER_TOTAL,
                renderMetrics.totalMs,
                component,
                filter = renderConfig.totalFilter
            )
            reportDuration(
                DIV_RENDER_MEASURE,
                renderMetrics.measureMs,
                component,
                filter = renderConfig.measureFilter
            )
            reportDuration(
                DIV_RENDER_LAYOUT,
                renderMetrics.layoutMs,
                component,
                filter = renderConfig.layoutFilter
            )
            reportDuration(
                DIV_RENDER_DRAW,
                renderMetrics.drawMs,
                component,
                filter = renderConfig.drawFilter
            )
        }
    }

    private fun resetRenderMetrics() {
        renderStarted = false
        layoutStartedTime = null
        measureStartedTime = null
        drawStartedTime = null
        renderMetrics.reset()
    }

    private fun Long.toTimePassed(): Long = currentUptime - this
}
