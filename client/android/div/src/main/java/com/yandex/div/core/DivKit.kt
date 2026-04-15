package com.yandex.div.core

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.IntDef
import com.yandex.android.beacon.SendBeaconManager
import com.yandex.div.BuildConfig
import com.yandex.div.DivDataTag
import com.yandex.div.core.dagger.DivKitComponent
import com.yandex.div.core.dagger.`Yatagan$DivKitComponent`
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.internal.Assert
import com.yandex.div.internal.Log
import com.yandex.div.logging.Severity

class DivKit private constructor(
    context: Context,
    configuration: DivKitConfiguration
) {

    internal val component: DivKitComponent = `Yatagan$DivKitComponent`.builder()
        .applicationContext(context.applicationContext)
        .configuration(configuration)
        .build()

    val sendBeaconManager: SendBeaconManager?
        get() = component.sendBeaconManager

    val parsingHistogramReporter: DivParsingHistogramReporter
        get() = component.parsingHistogramReporter

    val histogramReporterDelegate: HistogramReporterDelegate
        get() = component.histogramReporterDelegate

    fun reset(@ResetFlag flags: Int = RESET_ALL, tags: List<DivDataTag> = emptyList()) {
        if (flags and RESET_STORED_VARIABLES != 0) {
            component.storageComponent.rawJsonRepository.remove { true }
        }
        if (flags and RESET_STORED_DIV_DATA != 0) {
            component.storageComponent.repository.run {
                if (tags.isEmpty()) {
                    remove { true }
                } else {
                    remove { data -> tags.any { it.id == data.id } }
                }
            }
        }
    }

    @IntDef(
        flag = true,
        value = [
            RESET_ALL,
            RESET_STORED_VARIABLES,
            RESET_STORED_DIV_DATA,
        ]
    )
    annotation class ResetFlag

    companion object {

        const val RESET_ALL = 1 shl 0
        const val RESET_STORED_VARIABLES = 1 shl 1
        const val RESET_STORED_DIV_DATA = 1 shl 2

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
            GeneratedBuiltinFunctionProvider.warmUp()
        }

        @JvmStatic
        @AnyThread
        fun isLoggingEnabled(): Boolean = Log.isEnabled

        @JvmStatic
        @AnyThread
        fun enableLogging(enabled: Boolean) {
            Log.setEnabled(enabled)
        }

        @JvmStatic
        @AnyThread
        fun getLoggingSeverity(): Severity = Log.severity

        @JvmStatic
        @AnyThread
        fun setLoggingSeverity(severity: Severity) {
            Log.severity = severity
        }

        @JvmStatic
        @AnyThread
        fun isAssertionsEnabled(): Boolean = Assert.isEnabled

        @JvmStatic
        @AnyThread
        fun enableAssertions(enabled: Boolean) {
            Assert.isEnabled = enabled
        }
    }
}
