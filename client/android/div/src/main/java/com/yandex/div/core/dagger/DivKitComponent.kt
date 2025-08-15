package com.yandex.div.core.dagger

import android.content.Context
import com.yandex.android.beacon.SendBeaconManager
import com.yandex.div.core.DivKitConfiguration
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.HistogramRecordConfiguration
import com.yandex.div.histogram.HistogramRecorder
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import java.util.concurrent.ExecutorService
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [
    DivKitConfiguration::class,
    DivKitModule::class,
    DivKitHistogramsModule::class,
    DivActionTypedModule::class,
    DivStorageModule::class
], multiThreadAccess = true)
interface DivKitComponent {

    val sendBeaconManager: SendBeaconManager?

    val executorService: ExecutorService

    val histogramRecorder: HistogramRecorder

    val histogramRecordConfiguration: HistogramRecordConfiguration

    val parsingHistogramReporter: DivParsingHistogramReporter

    val histogramReporterDelegate: HistogramReporterDelegate

    fun div2Component(): Div2Component.Builder

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@Named(Names.APP_CONTEXT) context: Context): Builder

        fun configuration(configuration: DivKitConfiguration): Builder

        fun build(): DivKitComponent
    }
}
