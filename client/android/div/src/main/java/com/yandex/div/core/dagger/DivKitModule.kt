package com.yandex.div.core.dagger

import android.content.Context
import com.yandex.android.beacon.SendBeaconConfiguration
import com.yandex.android.beacon.SendBeaconManager
import com.yandex.div.core.histogram.CpuUsageHistogramReporter
import com.yandex.div.internal.viewpool.ViewCreator
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
internal object DivKitModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideSendBeaconManager(
        @Named(Names.APP_CONTEXT) context: Context,
        configuration: SendBeaconConfiguration?
    ): SendBeaconManager? {
        if (configuration == null) return null
        return SendBeaconManager(context, configuration)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideViewCreator(cpuUsageHistogramReporter: CpuUsageHistogramReporter): ViewCreator {
        return ViewCreator(cpuUsageHistogramReporter)
    }
}
