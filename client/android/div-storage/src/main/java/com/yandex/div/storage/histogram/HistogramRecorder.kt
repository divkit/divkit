package com.yandex.div.storage.histogram

import androidx.annotation.AnyThread
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.HistogramFilter
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import java.util.concurrent.CopyOnWriteArraySet

@Mockable
internal class HistogramRecorder(
        histogramReporterDelegate: HistogramReporterDelegate,
        private val histogramNameProvider: HistogramNameProvider?,
) {

    private val histogramReporter = HistogramReporter(histogramReporterDelegate)

    private val recordedHistograms = CopyOnWriteArraySet<String>()

    @AnyThread
    fun reportTemplateLoadedTime(duration: Long, filter: HistogramFilter = HistogramFilter.ON) {
        histogramNameProvider?.run {
            reportDuration(divLoadTemplatesReportName, duration, filter)
        }
    }

    /**
     * Reports div data loading time.
     * First call of this method records histogram with
     * .[HistogramNameProvider.coldCallTypeSuffix] suffix.
     * Next calls record *.[HistogramNameProvider.hotCallTypeSuffix] histogram.
     */
    @AnyThread
    fun reportDivDataLoadTime(duration: Long, filter: HistogramFilter = HistogramFilter.ON) {
        histogramNameProvider?.run {
            reportDuration(divDataLoadReportName, duration, filter)
        }
    }

    private fun reportDuration(
            histogramName: String,
            time: Long,
            filter: HistogramFilter = HistogramFilter.ON,
    ) =
        histogramReporter.reportDuration(
            histogramName = histogramName,
            duration = time,
            componentName = histogramNameProvider?.componentName,
            filter = filter,
            forceCallType = getHistogramCallType(histogramName),
    )

    private fun getHistogramCallType(histogramName: String): String {
        return if (recordedHistograms.add(histogramName)) {
            histogramNameProvider?.coldCallTypeSuffix ?: HistogramCallType.CALL_TYPE_COLD
        } else {
            histogramNameProvider?.hotCallTypeSuffix ?:  HistogramCallType.CALL_TYPE_WARM
        }
    }

    fun reportTemplatesParseTime(parsingHistogramNames: Set<String>, duration: Long) {
        parsingHistogramNames.forEach {
            histogramReporter.reportDuration(it, duration)
        }
    }
}
