package com.yandex.div.core.dagger

import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.DivParsingHistogramReporterImpl
import com.yandex.div.histogram.HistogramColdTypeChecker
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.internal.util.DoubleCheckProvider
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
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
        histogramReporterDelegate: Provider<HistogramReporterDelegate>,
        executorService: Provider<ExecutorService>
    ): DivParsingHistogramReporter {
        return when {
            histogramConfiguration.isReportingEnabled -> {
                val executorProvider =
                    provideCalculateSizeExecutor(histogramConfiguration, executorService)
                val histogramReporterProvider = provideHistogramReporter(
                    histogramReporterDelegate.get()
                )
                DivParsingHistogramReporterImpl(
                    histogramReporter = histogramReporterProvider::get,
                    calculateSizeExecutor = executorProvider::get
                )
            }

            else -> DivParsingHistogramReporter.DEFAULT
        }
    }

    @Provides
    fun provideHistogramReporterDelegate(
        histogramConfiguration: HistogramConfiguration,
        histogramRecorderProvider: Provider<HistogramRecorder>,
        histogramColdTypeCheckerProvider: Provider<HistogramColdTypeChecker>
    ): HistogramReporterDelegate {
        return when {
            histogramConfiguration.isReportingEnabled ->
                createHistogramReporterDelegate(
                    histogramConfiguration,
                    histogramRecorderProvider,
                    histogramColdTypeCheckerProvider
                )
            else -> HistogramReporterDelegate.NoOp
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

            else -> Provider { Executor {} }
        }
    }

    private fun provideHistogramReporter(
        histogramReporterDelegate: HistogramReporterDelegate,
    ): Provider<HistogramReporter> {
        return DoubleCheckProvider {
            createHistogramReporter(
                histogramReporterDelegate
            )
        }
    }
}
