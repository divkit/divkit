package com.yandex.div.histogram

import androidx.annotation.AnyThread
import com.yandex.div.histogram.util.Cancelable

/**
 * Interface that provide API for starting periodic CPU usage measurement for given thread and
 * sending it in histogram report.
 */
interface CpuUsageHistogramReporter {

    /**
     * Starts reporting cpu usage of thread with tid = [threadId] to histogram [histogramName].
     * Reporting can be cancelled via [Cancelable.cancel] call.
     */
    @AnyThread
    fun startReporting(histogramName: String, threadId: Int): Cancelable

    class NoOp : CpuUsageHistogramReporter {

        override fun startReporting(histogramName: String, threadId: Int) = Cancelable { }
    }
}
