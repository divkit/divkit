package com.yandex.div.core.dagger

import com.yandex.div.histogram.HistogramCallTypeProvider
import com.yandex.div.histogram.HistogramColdTypeChecker
import com.yandex.div.histogram.HistogramConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.histogram.reporter.HistogramReporterDelegateImpl
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
internal object DivHistogramsModule {

    @Provides
    @DivScope
    fun provideHistogramReporter(
        histogramConfiguration: HistogramConfiguration,
        histogramRecorderProvider: Provider<HistogramRecorder>,
        histogramColdTypeChecker: Provider<HistogramColdTypeChecker>
    ): HistogramReporter {
        return createHistogramReporter(
            histogramConfiguration,
            histogramRecorderProvider,
            histogramColdTypeChecker
        )
    }
}

internal fun createHistogramReporter(
    histogramConfiguration: HistogramConfiguration,
    histogramRecorderProvider: Provider<HistogramRecorder>,
    histogramColdTypeChecker: Provider<HistogramColdTypeChecker>
): HistogramReporter {
    val delegate = if (!histogramConfiguration.isReportingEnabled) {
        HistogramReporterDelegate.NoOp
    } else {
        val histogramCallTypeProvider = HistogramCallTypeProvider(histogramColdTypeChecker::get)
        HistogramReporterDelegateImpl(
            histogramRecorderProvider,
            histogramCallTypeProvider,
            histogramConfiguration,
            histogramConfiguration.taskExecutorProvider
        )
    }
    return HistogramReporter(delegate)
}
