package com.yandex.div.storage

import android.content.Context
import com.yandex.div.core.DivKit
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.analytics.CardErrorLoggerFactory
import com.yandex.div.storage.database.AndroidDatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.histogram.HistogramNameProvider
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div.storage.templates.DivParsingHistogramProxy
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div.storage.util.CardErrorTransformer
import com.yandex.div.storage.util.LazyProvider
import javax.inject.Provider

interface DivStorageComponent {
    val repository: DivDataRepository

    companion object {

        fun create(
                context: Context,
                histogramReporter: HistogramReporterDelegate = HistogramReporterDelegate.NoOp,
                histogramNameProvider: HistogramNameProvider? = null,
                errorLogger: ParsingErrorLogger = ParsingErrorLogger.LOG,
                cardErrorTransformer: Provider<out CardErrorTransformer>? = null,
                parsingHistogramReporter: Provider<DivParsingHistogramReporter> =
                    LazyProvider { DivKit.getInstance(context).parsingHistogramReporter },
                experimentalUseNewDatabaseManagerToPreventConcurrencyIssuesDoNotOverride: Boolean = true,
        ): DivStorageComponent = createInternal(
                context,
                histogramReporter,
                histogramNameProvider,
                errorLogger,
                cardErrorTransformer,
                parsingHistogramReporter,
                experimentalUseNewDatabaseManagerToPreventConcurrencyIssuesDoNotOverride,
        )

        internal fun createInternal(
                context: Context,
                histogramReporter: HistogramReporterDelegate = HistogramReporterDelegate.NoOp,
                histogramNameProvider: HistogramNameProvider? = null,
                errorLogger: ParsingErrorLogger = ParsingErrorLogger.LOG,
                cardErrorTransformer: Provider<out CardErrorTransformer>? = null,
                parsingHistogramReporter: Provider<DivParsingHistogramReporter> =
                    LazyProvider { DivKit.getInstance(context).parsingHistogramReporter },
                useDatabaseManager: Boolean = true,
        ): InternalStorageComponent {
            val openHelperProvider = DatabaseOpenHelperProvider { c, name, version, ccb, ucb ->
                AndroidDatabaseOpenHelper(c, name, version, ccb, ucb, useDatabaseManager)
            }
            val divStorage = DivStorageImpl(
                    context,
                    openHelperProvider,
            )
            val parsingHistogramProxy =
                LazyProvider { DivParsingHistogramProxy { parsingHistogramReporter.get() } }
            val histogramRecorder = HistogramRecorder(histogramReporter, histogramNameProvider)
            val templatesContainer = TemplatesContainer(
                    divStorage = divStorage,
                    errorLogger = errorLogger,
                    histogramRecorder = histogramRecorder,
                    parsingHistogramProxy = parsingHistogramProxy,
                    histogramNameProvider = histogramNameProvider,
            )

            val cardErrorLoggerFactory = CardErrorLoggerFactory(
                    cardErrorTransformer,
                    templatesContainer,
                    errorLogger,
            )

            val repository = DivDataRepositoryImpl(
                    divStorage,
                    templatesContainer,
                    histogramRecorder,
                    histogramNameProvider,
                    parsingHistogramProxy,
                    cardErrorLoggerFactory,
            )
            return InternalStorageComponent(
                    repository = repository,
                    storage = divStorage,
            )
        }
    }
}
