package com.yandex.divkit.demo.div.histogram

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.histogram.HistogramBridge
import com.yandex.div.core.util.KLog
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.divkit.demo.perf.PerfMetricReporter
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

internal class LoggingHistogramBridge : HistogramBridge {

    private val lastSavedHistograms: HashMap<String, String> = HashMap()

    fun getLastSavedHistogram(name: String): String? {
        return if (lastSavedHistograms.containsKey(name)) {
            lastSavedHistograms[name]
        } else null
    }

    override fun recordBooleanHistogram(name: String, sample: Boolean) {
        recordHistogram(name, "$sample")
    }

    override fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int) {
        recordHistogram(name, "$sample")
    }

    override fun recordLinearCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ) {
        recordHistogram(name, "$sample")
    }

    override fun recordCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ) {
        recordHistogram(name, "$sample")
        dispatcher?.dispatch(name)
        if (sample <= 0) return
        PerfMetricReporter.reportCountMetric(name, sample.toLong())
    }

    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: TimeUnit,
        bucketCount: Long
    ) {
        recordHistogram(name, "${unit.toMillis(duration)}")
        dispatcher?.dispatch(name)
        if (duration <= 0) return
        PerfMetricReporter.reportTimeMetric(name, TimeUnit.MILLISECONDS, unit.toMillis(duration))
    }

    override fun recordSparseSlowlyHistogram(name: String, sample: Int) {
        recordHistogram(name, "$sample")
    }

    private fun recordHistogram(name: String, sample: String) {
        KLog.d(TAG) { "$name: $sample" }
        lastSavedHistograms[name.removeSuffix(".Cool").removeSuffix(".Cold").removeSuffix(".Warm")] = sample
    }

    companion object {
        @Volatile
        @VisibleForTesting
        var dispatcher: HistogramDispatcher? = null
    }
}

internal fun interface HistogramDispatcher {
    fun dispatch(histogramName: String)
}
