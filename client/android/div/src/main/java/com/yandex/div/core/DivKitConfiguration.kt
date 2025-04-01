package com.yandex.div.core

import com.yandex.android.beacon.SendBeaconConfiguration
import com.yandex.div.core.dagger.ExternalOptional
import com.yandex.div.core.dagger.Names
import com.yandex.div.histogram.CpuUsageHistogramReporter
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.storage.DivStorageComponent
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
class DivKitConfiguration private constructor(
    private val sendBeaconConfiguration: Provider<SendBeaconConfiguration>?,
    private val executorService: ExecutorService,
    private val histogramConfiguration: Provider<HistogramConfiguration>,
    private val divStorageComponent: Provider<DivStorageComponent>?,
    private val divRequestExecutor: Provider<DivRequestExecutor>,
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

    @Singleton
    @Provides
    @Named(Names.HAS_DEFAULTS)
    fun externalDivStorageComponent(): ExternalOptional<DivStorageComponent> {
        return ExternalOptional.ofNullable(divStorageComponent?.get())
    }

    @Singleton
    @Provides
    fun divRequestExecutor(): DivRequestExecutor = divRequestExecutor.get()

    class Builder {

        private var sendBeaconConfiguration: Provider<SendBeaconConfiguration>? = null
        private var executorService: ExecutorService? = null
        private var histogramConfiguration = Provider { HistogramConfiguration.DEFAULT }
        private var divStorageComponent: Provider<DivStorageComponent>? = null
        private var divRequestExecutor = Provider { DivRequestExecutor.STUB }

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

        fun histogramConfiguration(configuration: Provider<HistogramConfiguration>): Builder {
            histogramConfiguration = configuration
            return this
        }

        fun divStorageComponent(component: Provider<DivStorageComponent>): Builder {
            divStorageComponent = component
            return this
        }

        fun divRequestExecutor(requestExecutor: Provider<DivRequestExecutor>): Builder {
            divRequestExecutor = requestExecutor
            return this
        }

        fun build(): DivKitConfiguration {
            return DivKitConfiguration(
                sendBeaconConfiguration,
                executorService ?: Executors.newSingleThreadExecutor(),
                histogramConfiguration,
                divStorageComponent,
                divRequestExecutor
            )
        }
    }
}
