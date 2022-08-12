package com.yandex.div.core.histogram

import androidx.annotation.AnyThread

/**
 * Interface that provide API for starting periodic CPU usage measurement for given thread and
 * sending it in histogram report.
 */
interface CpuUsageHistogramReporter {

    /**
     * Starts reporting cpu usage of thread with tid = [threadId] to histogram [histogramName].
     * Reporting can be cancelled via [Cancellable.cancel] call.
     */
    @AnyThread
    fun startReporting(histogramName: String, threadId: Int): Cancellable

    class NoOp : CpuUsageHistogramReporter {
        override fun startReporting(histogramName: String, threadId: Int): Cancellable {
            return Cancellable { }
        }
    }
}

fun interface Cancellable {
    @AnyThread
    fun cancel()
}
