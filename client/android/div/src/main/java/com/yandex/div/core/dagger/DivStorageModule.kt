package com.yandex.div.core.dagger

import android.content.Context
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.storage.DivStorageComponent
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
internal object DivStorageModule {

    @Provides
    @Singleton
    fun provideDivStorageComponent(
        @Named(Names.HAS_DEFAULTS) divStorageComponent: DivStorageComponent?,
        @Named(Names.APP_CONTEXT) context: Context,
        histogramReporterDelegate: HistogramReporterDelegate,
        parsingHistogramReporter: DivParsingHistogramReporter,
    ): DivStorageComponent {
        if (divStorageComponent != null) {
            return divStorageComponent
        }
        return DivStorageComponent.create(
            context = context,
            histogramReporter = histogramReporterDelegate,
            parsingHistogramReporter = { parsingHistogramReporter },
        )
    }

}
