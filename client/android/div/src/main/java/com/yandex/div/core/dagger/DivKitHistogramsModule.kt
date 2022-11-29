package com.yandex.div.core.dagger

import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.DivParsingHistogramReporterImpl
import com.yandex.div.histogram.HistogramColdTypeChecker
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.histogram.reporter.HistogramReporter
import dagger.Module
import dagger.Provides
import dagger.internal.DoubleCheck
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import javax.inject.Provider
import javax.inject.Singleton

@Module
internal object DivKitHistogramsModule {

    @Provides
    @Singleton
    fun provideDivParsingHistogramReporter(
        histogramConfiguration: HistogramConfiguration,
        histogramRecorderProvider: Provider<HistogramRecorder>,
        histogramColdTypeCheckerProvider: Provider<HistogramColdTypeChecker>,
        executorService: Provider<ExecutorService>
    ): DivParsingHistogramReporter {
        return when {
            histogramConfiguration.isReportingEnabled -> {
                val executorProvider =
                    provideCalculateSizeExecutor(histogramConfiguration, executorService)
                val histogramReporterProvider = provideHistogramReporter(
                    histogramConfiguration,
                    histogramRecorderProvider,
                    histogramColdTypeCheckerProvider
                )
                DivParsingHistogramReporterImpl(
                    histogramReporter = histogramReporterProvider::get,
                    calculateSizeExecutor = executorProvider::get
                )
            }

            else -> DivParsingHistogramReporter.DEFAULT
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun provideCalculateSizeExecutor(
        histogramConfiguration: HistogramConfiguration,
        executorService: Provider<ExecutorService>
    ): Provider<Executor> {
        return when {
            histogramConfiguration.isSizeRecordingEnabled ->
                executorService as Provider<Executor>

            else -> DoubleCheck.provider(Provider { Executor {} })
        }
    }

    private fun provideHistogramReporter(
        histogramConfiguration: HistogramConfiguration,
        histogramRecorderProvider: Provider<HistogramRecorder>,
        histogramColdTypeCheckerProvider: Provider<HistogramColdTypeChecker>
    ): Provider<HistogramReporter> {
        return DoubleCheck.provider(Provider {
            createHistogramReporter(
                histogramConfiguration,
                histogramRecorderProvider,
                histogramColdTypeCheckerProvider
            )
        })
    }
}
