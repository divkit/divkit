package com.yandex.div.core

import com.yandex.android.beacon.SendBeaconConfiguration
import com.yandex.div.histogram.CpuUsageHistogramReporter
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.HistogramRecorder
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Provider
import javax.inject.Singleton

@Module
class DivKitConfiguration private constructor(
    private val sendBeaconConfiguration: Provider<SendBeaconConfiguration>?,
    private val executorService: ExecutorService,
    private val histogramConfiguration: Provider<HistogramConfiguration>
) {

    @Provides
    fun sendBeaconConfiguration(): SendBeaconConfiguration? {
        return sendBeaconConfiguration?.get()
    }

    @Provides
    fun executorService(): ExecutorService {
        return executorService
    }

    @Provides
    fun histogramRecordConfiguration(): HistogramRecordConfiguration {
        return histogramConfiguration.get()
    }

    @Provides
    fun histogramConfiguration(): HistogramConfiguration = histogramConfiguration.get()

    @Singleton
    @Provides
    fun histogramRecorder(): HistogramRecorder {
        return HistogramRecorder(histogramConfiguration.get().histogramBridge.get())
    }

    @Singleton
    @Provides
    fun cpuUsageHistogramReporter(): CpuUsageHistogramReporter {
        return histogramConfiguration.get().cpuUsageHistogramReporter.get()
    }

    class Builder {

        private var sendBeaconConfiguration: Provider<SendBeaconConfiguration>? = null
        private var executorService: ExecutorService? = null
        private var histogramConfiguration = Provider { HistogramConfiguration.DEFAULT }

        fun sendBeaconConfiguration(configuration: SendBeaconConfiguration): Builder {
            sendBeaconConfiguration = Provider { configuration }
            return this
        }

        fun sendBeaconConfiguration(configuration: Provider<SendBeaconConfiguration>): Builder {
            sendBeaconConfiguration = configuration
            return this
        }

        fun executorService(service: ExecutorService): Builder {
            executorService = service
            return this
        }

        @Deprecated("Use histogramConfiguration(configuration: Provider<HistogramConfiguration>")
        fun histogramConfiguration(configuration: HistogramConfiguration): Builder {
            histogramConfiguration = Provider { configuration }
            return this
        }

        fun histogramConfiguration(configuration: Provider<HistogramConfiguration>): Builder {
            histogramConfiguration = configuration
            return this
        }

        fun build(): DivKitConfiguration {
            return DivKitConfiguration(
                sendBeaconConfiguration,
                executorService ?: Executors.newSingleThreadExecutor(),
                histogramConfiguration
            )
        }
    }
}
