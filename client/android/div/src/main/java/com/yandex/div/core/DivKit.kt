package com.yandex.div.core

import android.content.Context
import androidx.annotation.AnyThread
import com.yandex.android.beacon.SendBeaconManager
import com.yandex.div.BuildConfig
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.dagger.DaggerDivKitComponent
import com.yandex.div.core.dagger.DivKitComponent
import com.yandex.div.evaluable.function.BuiltinFunctionProvider
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.internal.Assert
import com.yandex.div.internal.Log

@PublicApi
class DivKit private constructor(
    context: Context,
    configuration: DivKitConfiguration
) {

    internal val component: DivKitComponent = DaggerDivKitComponent.builder()
        .applicationContext(context.applicationContext)
        .configuration(configuration)
        .build()

    val sendBeaconManager: SendBeaconManager?
        get() = component.sendBeaconManager

    val parsingHistogramReporter: DivParsingHistogramReporter
        get() = component.parsingHistogramReporter

    val histogramReporterDelegate: HistogramReporterDelegate
        get() = component.histogramReporterDelegate

    companion object {

        private val DEFAULT_CONFIGURATION = DivKitConfiguration.Builder().build()

        private var configuration: DivKitConfiguration? = null
        @Volatile
        private var instance: DivKit? = null

        @JvmStatic
        @AnyThread
        fun configure(configuration: DivKitConfiguration) {
            synchronized(this) {
                if (this.configuration == null) {
                    this.configuration = configuration
                } else {
                    Assert.fail("DivKit already configured")
                }
            }
        }

        @JvmStatic
        @AnyThread
        fun getInstance(context: Context): DivKit {
            instance?.let {
                return it
            }

            synchronized(this) {
                instance?.let {
                    return it
                }

                val divKit = DivKit(context, configuration ?: DEFAULT_CONFIGURATION)
                return divKit.also { instance = it }
            }
        }

        @JvmStatic
        val versionName: String
            get() = BuildConfig.VERSION_NAME

        // Put any classes with heavy <clinit> here so it could be warmed up by DivKit client
        // (possibly on background thread).
        @JvmStatic
        @AnyThread
        fun warmUpStatics() {
            // Bootstraps all built in expression functions.
            BuiltinFunctionProvider { _ -> null }
        }

        @JvmStatic
        @AnyThread
        fun isLoggingEnabled(): Boolean = Log.isEnabled()

        @JvmStatic
        @AnyThread
        fun enableLogging(enabled: Boolean) {
            Log.setEnabled(enabled)
        }

        @JvmStatic
        @AnyThread
        fun isAssertionsEnabled(): Boolean = Assert.isEnabled()

        @JvmStatic
        @AnyThread
        fun enableAssertions(enabled: Boolean) {
            Assert.setEnabled(enabled)
        }
    }

}
