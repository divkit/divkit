package com.yandex.divkit.demo.div.histogram

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.histogram.HistogramBridge
import com.yandex.div.core.util.KLog
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.perftests.core.Reporter
import com.yandex.perftests.core.Units
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
    override fun recordBooleanHistogram(name: String, sample: Boolean) {
        recordHistogram("$name: $sample")
    }

    override fun recordEnumeratedHistogram(name: String, sample: Int, boundary: Int) {
        recordHistogram("$name: $sample")
    }

    override fun recordLinearCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ) {
        recordHistogram("$name: $sample")
    }

    override fun recordCountHistogram(
        name: String,
        sample: Int,
        min: Int,
        max: Int,
        bucketCount: Int
    ) {
        recordHistogram("$name: $sample")
        dispatcher?.dispatch(name)
        if (sample <= 0) return
        Reporter.reportMetric(name, Units.BYTES, sample.toLong())
    }

    override fun recordTimeHistogram(
        name: String,
        duration: Long,
        min: Long,
        max: Long,
        unit: TimeUnit,
        bucketCount: Long
    ) {
        recordHistogram("$name: ${unit.toMillis(duration)}")
        dispatcher?.dispatch(name)
        if (duration <= 0) return
        Reporter.reportMetric(name, Units.MILLISECONDS, unit.toMillis(duration))
    }

    override fun recordSparseSlowlyHistogram(name: String, sample: Int) {
        recordHistogram("$name: $sample")
    }

    private fun recordHistogram(data: String) {
        KLog.d(TAG) { data }
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
