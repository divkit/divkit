package com.yandex.div.core

import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import com.yandex.div.histogram.DIV_CONTEXT_CREATE_HISTOGRAM
import com.yandex.div.histogram.DIV_VIEW_CREATE_HISTOGRAM
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.reporter.HistogramReporter
import java.util.concurrent.atomic.AtomicBoolean

internal class DivCreationTracker(private val contextCreationStarted: Long) {

    private var contextCreatedTime: Long = INVALID_TIME

    private val contextCreationReported = AtomicBoolean(false)

    @HistogramCallType
    private val contextCreateCallType: String = when {
        isColdContextCreate.compareAndSet(true, false) -> HistogramCallType.CALL_TYPE_COLD
        else -> HistogramCallType.CALL_TYPE_COOL
    }

    private val isFirstViewCreate: AtomicBoolean = AtomicBoolean(true)

    /**
     * @return [HistogramCallType] related to view creation.
     */
    @get:HistogramCallType
    val viewCreateCallType: String
        get() = when {
            isFirstViewCreate.compareAndSet(true, false) -> {
                if (isColdViewCreate.compareAndSet(true, false)) {
                    HistogramCallType.CALL_TYPE_COLD
                } else {
                    HistogramCallType.CALL_TYPE_COOL
                }
            }

            else -> HistogramCallType.CALL_TYPE_WARM
        }

    /**
     * Tracks the time when context was created and when the first instance of context was created.
     */
    fun onContextCreationFinished() {
        if (contextCreatedTime >= 0) {
            return
        }
        contextCreatedTime = currentUptimeMillis
    }

    /**
     * Sends histogram of view creation duration [viewCreationFinish] - [viewCreationStart] and
     * histogram of context creation duration.
     *
     * Notice: [sendHistograms] must be called only once for each view to avoid duplicate records.
     */
    fun sendHistograms(
        viewCreationStart: Long,
        viewCreationFinish: Long,
        histogramReporter: HistogramReporter,
        @HistogramCallType viewCreateCallType: String,
    ) {
        if (viewCreationFinish < 0) {
            return
        }
        histogramReporter.reportDuration(
            DIV_VIEW_CREATE_HISTOGRAM,
            viewCreationFinish - viewCreationStart,
            forceCallType = viewCreateCallType
        )
        if (contextCreationReported.compareAndSet(false, true)) {
            sendContextCreationHistogram(histogramReporter)
        }
    }

    private fun sendContextCreationHistogram(histogramReporter: HistogramReporter) {
        if (contextCreatedTime < 0) {
            return
        }
        histogramReporter.reportDuration(
            DIV_CONTEXT_CREATE_HISTOGRAM,
            contextCreatedTime - contextCreationStarted,
            forceCallType = contextCreateCallType
        )
        contextCreatedTime = -1
    }

    internal companion object {

        private const val INVALID_TIME = -1L

        private val isColdContextCreate: AtomicBoolean = AtomicBoolean(true)

        private val isColdViewCreate: AtomicBoolean = AtomicBoolean(true)

        @VisibleForTesting
        fun resetColdCreation() {
            isColdContextCreate.set(true)
            isColdViewCreate.set(true)
        }

        val currentUptimeMillis: Long
            get() = SystemClock.uptimeMillis()
    }
}
