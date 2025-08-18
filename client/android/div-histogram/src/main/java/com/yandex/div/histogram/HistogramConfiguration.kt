package com.yandex.div.histogram

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.internal.util.DoubleCheckProvider
import javax.inject.Provider

@PublicApi
interface HistogramConfiguration : HistogramRecordConfiguration {

    val histogramBridge: Provider<HistogramBridge>
    val cpuUsageHistogramReporter: Provider<CpuUsageHistogramReporter>
    val isReportingEnabled: Boolean
    val taskExecutorProvider: Provider<TaskExecutor>

    open class DefaultHistogramConfiguration : HistogramConfiguration {

        override val histogramBridge: Provider<HistogramBridge> =
            DoubleCheckProvider(::NoOpHistogramBridge)
        override val cpuUsageHistogramReporter: Provider<CpuUsageHistogramReporter> =
            DoubleCheckProvider { CpuUsageHistogramReporter.NoOp() }
        override val isReportingEnabled: Boolean = false
        override val isColdRecordingEnabled: Boolean = false
        override val isCoolRecordingEnabled: Boolean = false
        override val isWarmRecordingEnabled: Boolean = false
        override val isSizeRecordingEnabled: Boolean = false
        override val taskExecutorProvider: Provider<TaskExecutor> =
            DoubleCheckProvider(::DefaultTaskExecutor)
        override val renderConfiguration: Provider<RenderConfiguration> =
            DoubleCheckProvider(::RenderConfiguration)
    }

    companion object {
        @JvmField
        val DEFAULT: HistogramConfiguration = DefaultHistogramConfiguration()
    }
}
