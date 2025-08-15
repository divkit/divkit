package com.yandex.div.histogram.reporter

import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.HistogramCallTypeProvider
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.histogram.TaskExecutor
import com.yandex.div.histogram.util.HistogramUtils
import com.yandex.div.histogram.util.MIN_DURATION_HISTOGRAM_VALUE
import com.yandex.div.histogram.util.MIN_SIZE_HISTOGRAM_VALUE
import java.util.concurrent.TimeUnit
import javax.inject.Provider

/**
 * Delegate to record histograms via [histogramRecorder].
 * Adds appropriate suffix according to a call type.
 */
class HistogramReporterDelegateImpl(
    private val histogramRecorder: Provider<HistogramRecorder>,
    private val histogramCallTypeProvider: HistogramCallTypeProvider,
    private val histogramRecordConfig: HistogramRecordConfiguration,
    private val taskExecutor: Provider<TaskExecutor>
) : HistogramReporterDelegate {

    override fun reportDuration(histogramName: String,
                                duration: Long,
                                @HistogramCallType forceCallType: String?) {
        val providedCallType = histogramCallTypeProvider.getHistogramCallType(histogramName)
        val callType = forceCallType ?: providedCallType
        if (!HistogramUtils.shouldRecordHistogram(callType, histogramRecordConfig)) {
            return
        }
        taskExecutor.get().post {
            histogramRecorder.get().recordShortTimeHistogram(
                "$histogramName.$callType",
                duration.coerceAtLeast(MIN_DURATION_HISTOGRAM_VALUE),
                TimeUnit.MILLISECONDS
            )
        }
    }

    override fun reportSize(histogramName: String, size: Int) {
        taskExecutor.get().post {
            histogramRecorder.get().recordCount100KHistogram(
                "$histogramName.Size",
                size.coerceAtLeast(MIN_SIZE_HISTOGRAM_VALUE)
            )
        }
    }
}
