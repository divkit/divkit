package com.yandex.div.histogram.reporter

import com.yandex.div.histogram.HistogramCallType

/**
 * Delegate to report histograms.
 */
interface HistogramReporterDelegate {

    /**
     * Reports [duration] for the given [histogramName] and [forceCallType].
     */
    fun reportDuration(histogramName: String,
                       duration: Long,
                       @HistogramCallType forceCallType: String? = null)

    /**
     * Reports [size] for the given [histogramName].
     */
    fun reportSize(histogramName: String, size: Int)

    /**
     * No op implementation of [HistogramReporterDelegate].
     */
    object NoOp : HistogramReporterDelegate {
        override fun reportDuration(histogramName: String,
                                    duration: Long,
                                    @HistogramCallType forceCallType: String?) = Unit
        override fun reportSize(histogramName: String, size: Int) = Unit
    }
}
