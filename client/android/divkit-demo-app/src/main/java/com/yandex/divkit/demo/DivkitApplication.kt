package com.yandex.divkit.demo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import com.yandex.android.beacon.SendBeaconConfiguration
import com.yandex.android.beacon.SendBeaconPerWorkerLogger
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivKitConfiguration
import com.yandex.div.internal.Assert
import com.yandex.div.network.DefaultDivRequestExecutor
import com.yandex.divkit.demo.beacon.SendBeaconRequestExecutorImpl
import com.yandex.divkit.demo.beacon.SendBeaconWorkerSchedulerImpl
import com.yandex.divkit.demo.utils.VisualAssertionErrorHandler
import com.yandex.divkit.regression.di.HasRegressionTesting
import com.yandex.divkit.regression.di.RegressionComponent
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import okhttp3.OkHttpClient
import java.util.concurrent.Executors


class DivkitApplication : Application(), HasRegressionTesting {

    override val regressionComponent: RegressionComponent
        get() = Container.regressionComponent

    override fun onCreate() {
        super.onCreate()

        Container.initialize(applicationContext)
        // Create an InitializerBuilder
        val initializerBuilder = Stetho.newInitializerBuilder(this)

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
            Stetho.defaultInspectorModulesProvider(this)
        )

        // Enable command line interface
        initializerBuilder.enableDumpapp(
            Stetho.defaultDumperPluginsProvider(this)
        )

        // Use the InitializerBuilder to generate an Initializer
        val initializer = initializerBuilder.build()

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer)
        AppMetrica.activate(
            this,
            AppMetricaConfig.newConfigBuilder("e48dd638-f5ba-4cb8-b272-53b6d275062f")
                .withCrashReporting(!BuildConfig.DEBUG)
                .withLocationTracking(false)
                .withNativeCrashReporting(false)
                .withLogs().build()
        )

        // Internal API used, do not reproduce this line in your app
        Assert.setAssertPerformer(VisualAssertionErrorHandler(this))
        DivKit.apply {
            enableLogging(true)
            enableAssertions(BuildConfig.THROW_ASSERTS)
        }
        val okHttpClient = OkHttpClient()
        DivKit.configure(
            DivKitConfiguration.Builder()
                .sendBeaconConfiguration(configureSendBeacon(okHttpClient))
                .histogramConfiguration(Container::histogramConfiguration)
                .divRequestExecutor { DefaultDivRequestExecutor(Container.httpClient)}
                .build()
        )

        AppCompatDelegate.setDefaultNightMode(Container.preferences.nightMode)
    }

    private fun configureSendBeacon(okHttpClient: OkHttpClient): SendBeaconConfiguration {
        return SendBeaconConfiguration(
            executor = Executors.newSingleThreadExecutor(),
            requestExecutor = SendBeaconRequestExecutorImpl(okHttpClient),
            workerScheduler = SendBeaconWorkerSchedulerImpl(this),
            perWorkerLogger = SendBeaconPerWorkerLogger.Logcat,
            databaseName = "mordaSendBeacon.db"
        )
    }
}
