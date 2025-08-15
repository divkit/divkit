package com.yandex.div.histogram.reporter

import androidx.annotation.AnyThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.HistogramFilter

/**
 * Reporter of Div histograms.
 */
@AnyThread
@Mockable
class HistogramReporter(
    private val histogramReporterDelegate: HistogramReporterDelegate
) {

    /**
     * Reports [duration] for the given [histogramName], [componentName] if provided and
     * [forceCallType] if provided.
     *
     * @param filter [HistogramFilter] to filter duration histogram reporting.
     */
    fun reportDuration(
        histogramName: String,
        duration: Long,
        componentName: String? = null,
        @HistogramCallType forceCallType: String? = null,
        filter: HistogramFilter = HistogramFilter.ON
    ) {
        withComponentHistogram(histogramName, componentName) { histogram, component ->
            if (filter.report(component)) {
                histogramReporterDelegate.reportDuration(histogram, duration, forceCallType)
            }
        }
    }

    /**
     * Reports [size] for the given [histogramName] and [componentName] if provided.
     *
     * @param filter [HistogramFilter] to filter size histogram reporting.
     */
    fun reportSize(
        histogramName: String,
        size: Int,
        componentName: String? = null,
        filter: HistogramFilter = HistogramFilter.ON
    ) {
        withComponentHistogram(histogramName, componentName) { histogram, component ->
            if (filter.report(component)) {
                histogramReporterDelegate.reportSize(histogram, size)
            }
        }
    }
}

private inline fun withComponentHistogram(
    baseHistogramName: String,
    componentName: String?,
    block: (histogram: String, component: String?) -> Unit
) {
    block(baseHistogramName, null)
    componentName?.let { block("$it.$baseHistogramName", it) }
}
