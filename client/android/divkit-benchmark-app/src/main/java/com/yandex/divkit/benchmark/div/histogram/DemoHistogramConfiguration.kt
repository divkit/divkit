package com.yandex.divkit.benchmark.div.histogram

import com.yandex.div.histogram.HistogramBridge
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.div.internal.KLog
import com.yandex.divkit.benchmark.perf.PerfMetricReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Provider

internal class DemoHistogramConfiguration(
    override val histogramBridge: Provider<HistogramBridge>,
    override val renderConfiguration: Provider<RenderConfiguration>,
    override val isReportingEnabled: Boolean = true,
    override val isColdRecordingEnabled: Boolean = true,
    override val isCoolRecordingEnabled: Boolean = true,
    override val isSizeRecordingEnabled: Boolean = true,
    override val isWarmRecordingEnabled: Boolean = true,
) : HistogramConfiguration.DefaultHistogramConfiguration()

private const val TAG = "DIV_HISTOGRAM_LOGGER"

class LoggingHistogramBridge : HistogramBridge {
    private val _histograms = MutableStateFlow("")
    val histograms: StateFlow<String> = _histograms.asStateFlow()

    override fun recordBooleanHistogram(name: String, sample: Boolean) {
        recordHistogram(name, sample)
    }

    override fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int) {
        recordHistogram(name, sample.toLong())
    }

    override fun recordLinearCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ) {
        recordHistogram(name, sample.toLong())
    }

    override fun recordCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ) {
        recordHistogram(name, sample.toLong())
        if (sample <= 0) return
        PerfMetricReporter.reportCountMetric(name, sample.toLong())
    }

    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: TimeUnit,
        bucketCount: Int
    ) {
        recordHistogram(name, duration)
        if (duration <= 0) return
        PerfMetricReporter.reportTimeMetric(name, unit, duration)
    }

    override fun recordSparseSlowlyHistogram(name: String, sample: Int) {
        recordHistogram(name, sample.toLong())
    }

    private fun recordHistogram(name: String, sample: Long) {
        KLog.d(TAG) { "$name: ${sample / 1000} ms" }
        _histograms.value = name
    }

    private fun recordHistogram(name: String, sample: Boolean) {
        KLog.d(TAG) { "$name: $sample" }
        _histograms.value = name
    }
}
