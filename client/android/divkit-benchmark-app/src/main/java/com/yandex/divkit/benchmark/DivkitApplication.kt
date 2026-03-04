package com.yandex.divkit.benchmark

import android.app.Application
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivKitConfiguration
import com.yandex.div.internal.Assert
import com.yandex.div.network.DefaultDivRequestExecutor
import com.yandex.divkit.benchmark.utils.VisualAssertionErrorHandler

class DivkitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Container.initialize(applicationContext)

        Assert.setAssertPerformer(VisualAssertionErrorHandler(this))
        DivKit.apply {
            enableLogging(true)
            enableAssertions(BuildConfig.THROW_ASSERTS)
        }
        DivKit.configure(
            DivKitConfiguration.Builder()
                .histogramConfiguration(Container::histogramConfiguration)
                .divRequestExecutor { DefaultDivRequestExecutor(Container.httpClient) }
                .build()
        )
    }
}
