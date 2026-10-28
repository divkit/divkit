package com.yandex.div.histogram

import androidx.annotation.MainThread
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.histogram.metrics.RenderMetrics
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.histogram.util.HistogramClock
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.util.UiThreadHandler

@MainThread
class Div2ViewHistogramReporter(
    private val histogramReporter: () -> HistogramReporter,
    private val renderConfig: () -> RenderConfiguration,
) {

    var component: String? = null
    private var renderStarted = false
    private var prepareStartedTime: Long? = null

    private var bindingStartedTime: Long? = null
    private var bindingPausedTime: Long? = null
    private var bindingResumedTime: Long? = null
    private var rebindingStartedTime: Long? = null

    private var viewsStartedTime: Long? = null
    private var viewsFinishedTime: Long? = null

    @Volatile
    internal var asyncBindingTimer: Div2ViewAsyncBindingTimer? = null

    private var measureStartedTime: Long? = null
    private var layoutStartedTime: Long? = null
    private var drawStartedTime: Long? = null

    private val renderMetrics by lazy(LazyThreadSafetyMode.NONE, ::RenderMetrics)

    private val currentUptime: Long
        get() = HistogramClock.uptime()

    fun onRenderStarted() {
        renderStarted = true
        prepareStartedTime = currentUptime
        asyncBindingTimer?.onRenderStarted()
    }

    @InternalApi
    fun onPrepareFinished() {
        val startedTime = prepareStartedTime ?: return
        prepareStartedTime = null
        if (!UiThreadHandler.get().isMainThread()) return
        histogramReporter().reportDuration(
            DIV_RENDER_PREPARE,
            currentUptime - startedTime,
            component,
            filter = renderConfig().prepareFilter,
        )
    }

    fun onBindingStarted() {
        bindingStartedTime = currentUptime
        viewsStartedTime = null
        viewsFinishedTime = null
    }

    fun onBindingPaused() {
        bindingPausedTime = currentUptime
    }

    fun onBindingResumed() {
        bindingResumedTime = currentUptime
    }

    fun onBindingFinished() {
        reportHistogram(DIV_BINDING_HISTOGRAM, bindingStartedTime, bindingPausedTime, bindingResumedTime, renderMetrics::binding)
        reportBindingParts()
        bindingStartedTime = null
        bindingPausedTime = null
        bindingResumedTime = null
        viewsStartedTime = null
        viewsFinishedTime = null
    }

    @InternalApi
    fun onBindViewsStarted() {
        viewsStartedTime = if (UiThreadHandler.get().isMainThread()) currentUptime else null
    }

    @InternalApi
    fun onBindViewsFinished() {
        viewsFinishedTime = viewsStartedTime?.let { currentUptime }
    }

    /**
     * Starts measuring one [Div2View.setDataAsync] call. Must be called when the binding is queued.
     */
    @InternalApi
    fun onAsyncBindingQueued(): Div2ViewAsyncBindingTimer = Div2ViewAsyncBindingTimer(this)

    /**
     * Must be called when binding of views is deferred until [Div2View] is attached. The phases of
     * the asynchronous binding in progress are not reported, because they would not include it.
     */
    @InternalApi
    fun onBindingDeferred() {
        asyncBindingTimer?.cancel()
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

    /**
     * Reports the parts of the first binding after [DIV_BINDING_HISTOGRAM], so that reporting them
     * is not measured, and ends the last part where the reported binding ends. Views are timed only
     * on the main thread and only when binding is not deferred until attach, so asynchronous and
     * bind-on-attach bindings report no parts.
     */
    private fun reportBindingParts() {
        val bindingStarted = bindingStartedTime ?: return
        val viewsStarted = viewsStartedTime ?: return
        val viewsFinished = viewsFinishedTime ?: return
        val bindingFinishedTime = bindingStarted + renderMetrics.bindingDuration
        with(histogramReporter()) {
            reportDuration(DIV_BINDING_SETUP, viewsStarted - bindingStarted, component)
            reportDuration(DIV_BINDING_VIEWS, viewsFinished - viewsStarted, component)
            reportDuration(DIV_BINDING_FINISH, bindingFinishedTime - viewsFinished, component)
        }
    }

    private fun reportRenderMetrics(renderMetrics: RenderMetrics) {
        with(histogramReporter()) {
            val renderConfig = renderConfig()
            reportDuration(
                DIV_RENDER_TOTAL,
                renderMetrics.totalDuration,
                component,
                filter = renderConfig.totalFilter
            )
            reportDuration(
                DIV_RENDER_MEASURE,
                renderMetrics.measureDuration,
                component,
                filter = renderConfig.measureFilter
            )
            reportDuration(
                DIV_RENDER_LAYOUT,
                renderMetrics.layoutDuration,
                component,
                filter = renderConfig.layoutFilter
            )
            reportDuration(
                DIV_RENDER_DRAW,
                renderMetrics.drawDuration,
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

    internal fun reportAsyncBinding(histogramName: String, duration: Long) {
        histogramReporter().reportDuration(histogramName, duration, component)
    }

    private fun Long.toTimePassed(): Long = currentUptime - this
}

/**
 * Measures one asynchronous binding of [Div2View] from the moment it is queued. The callbacks only
 * record time; [report] sends the phases after the binding, and only when the binding rendered new
 * data ([Div2ViewHistogramReporter.onRenderStarted] during the background phase), the main
 * thread applied it ([onMainFinished]) and the measurement was not [cancel]led.
 */
@InternalApi
class Div2ViewAsyncBindingTimer internal constructor(
    private val owner: Div2ViewHistogramReporter,
) {

    private val queuedTime = HistogramClock.uptime()
    private var backgroundStartedTime = queuedTime
    private var backgroundFinishedTime = queuedTime
    private var mainStartedTime = queuedTime
    private var mainFinishedTime: Long? = null
    private var isRenderStarted = false
    private var isCancelled = false

    fun onBackgroundStarted() {
        backgroundStartedTime = HistogramClock.uptime()
        owner.asyncBindingTimer = this
    }

    internal fun onRenderStarted() {
        isRenderStarted = true
    }

    fun onBackgroundFinished() {
        backgroundFinishedTime = HistogramClock.uptime()
        owner.asyncBindingTimer = null
    }

    fun onMainStarted() {
        mainStartedTime = HistogramClock.uptime()
    }

    fun onMainFinished() {
        mainFinishedTime = HistogramClock.uptime()
    }

    /**
     * Drops the measurement of a binding that failed or whose binding of views is deferred until
     * attach.
     */
    fun cancel() {
        isCancelled = true
        if (owner.asyncBindingTimer === this) {
            owner.asyncBindingTimer = null
        }
    }

    fun report() {
        val finishedTime = mainFinishedTime ?: return
        if (!isRenderStarted || isCancelled) return
        report(DIV_ASYNC_BINDING_BACKGROUND_WAIT, backgroundStartedTime - queuedTime)
        report(DIV_ASYNC_BINDING_BACKGROUND, backgroundFinishedTime - backgroundStartedTime)
        report(DIV_ASYNC_BINDING_MAIN_WAIT, mainStartedTime - backgroundFinishedTime)
        report(DIV_ASYNC_BINDING_MAIN, finishedTime - mainStartedTime)
        report(DIV_ASYNC_BINDING_TOTAL, finishedTime - queuedTime)
    }

    private fun report(histogramName: String, duration: Long) {
        owner.reportAsyncBinding(histogramName, duration)
    }
}
