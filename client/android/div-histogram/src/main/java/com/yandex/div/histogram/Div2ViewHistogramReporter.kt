package com.yandex.div.histogram

import android.os.SystemClock
import androidx.annotation.MainThread
import com.yandex.div.histogram.metrics.RenderMetrics
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.internal.KAssert

@MainThread
class Div2ViewHistogramReporter(
    private val histogramReporter: () -> HistogramReporter,
    private val renderConfig: () -> RenderConfiguration,
) {

    var component: String? = null
    private var renderStarted = false

    private var bindingStartedTime: Long? = null
    private var bindingPausedTime: Long? = null
    private var bindingResumedTime: Long? = null
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

    fun onBindingPaused() {
        bindingPausedTime = currentUptime
    }

    fun onBindingResumed() {
        bindingResumedTime = currentUptime
    }

    fun onBindingFinished() {
        reportHistogram(DIV_BINDING_HISTOGRAM, bindingStartedTime, bindingPausedTime, bindingResumedTime, renderMetrics::binding)
        bindingStartedTime = null
        bindingPausedTime = null
        bindingResumedTime = null
    }

    fun onRebindingStarted() {
        rebindingStartedTime = currentUptime
    }

    fun onRebindingFinished() {
        reportHistogram(DIV_REBINDING_HISTOGRAM, rebindingStartedTime, onDuration = renderMetrics::rebinding)
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
        histogramName: String,
        startTime: Long?,
        pausedTime: Long? = null,
        resumedTime: Long? = null,
        onDuration: (Long) -> Unit
    ) {
        if (startTime == null) {
            KAssert.fail { "start time of $histogramName is null" }
            return
        }
        val duration = when {
            pausedTime != null && resumedTime != null -> {
                currentUptime - resumedTime + pausedTime - startTime
            }
            pausedTime != null || resumedTime != null -> {
                KAssert.fail { "when $histogramName has paused time it should have resumed time and otherwise" }
                return
            }
            else -> currentUptime - startTime
        }
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
