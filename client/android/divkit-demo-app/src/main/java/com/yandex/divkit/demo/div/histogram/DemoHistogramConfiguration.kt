package com.yandex.divkit.demo.div.histogram

import androidx.annotation.VisibleForTesting
import com.yandex.div.histogram.HistogramBridge
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.RenderConfiguration
import com.yandex.div.internal.KLog
import com.yandex.divkit.demo.perf.PerfMetricReporter
import org.json.JSONObject
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

    private val lastSavedHistograms: HashMap<String, TimeHistogram> = HashMap()

    fun getLastSavedHistogram(name: String): TimeHistogram? {
        return if (lastSavedHistograms.containsKey(name)) {
            lastSavedHistograms[name]
        } else null
    }

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
        bucketCount: Int
    ) {
        recordHistogram(name, unit.toMillis(duration))
        dispatcher?.dispatch(name)
        if (duration <= 0) return
        PerfMetricReporter.reportTimeMetric(name, TimeUnit.MILLISECONDS, unit.toMillis(duration))
    }

    override fun recordSparseSlowlyHistogram(name: String, sample: Int) {
        recordHistogram(name, sample.toLong())
    }

    private fun recordHistogram(name: String, sample: Long) {
        KLog.d(TAG) { "$name: $sample" }
        val histogramName = name.removeSuffix(".Cool").removeSuffix(".Cold").removeSuffix(".Warm")
        val histogramCallType = name.takeLast(4)
        lastSavedHistograms[histogramName] = TimeHistogram(
            histogramName,
            histogramCallType,
            sample,
        )
    }

    private fun recordHistogram(name: String, sample: Boolean) {
        KLog.d(TAG) { "$name: $sample" }
    }

    companion object {
        @Volatile
        @VisibleForTesting
        var dispatcher: HistogramDispatcher? = null
    }

    class TimeHistogram(
        val name: String,
        @HistogramCallType val histogramCallType: String,
        val value: Long,
    ) {
        override fun toString(): String {
            return "$name.$histogramCallType: $value"
        }

        fun toJSONObject(): JSONObject {
            return JSONObject().apply {
                put("histogram_type", histogramCallType.toLowerCase())
                put("value", value)
            }
        }
    }
}

fun interface HistogramDispatcher {
    fun dispatch(histogramName: String)
}
