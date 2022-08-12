package com.yandex.divkit.regression.screenrecord

import com.yandex.divkit.regression.RegressionConfig
import com.yandex.divkit.regression.di.ActivityScope
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
internal object ScreenRecordModule {

    @Provides
    @ActivityScope
    fun provideScreenRecord(
        impl: Provider<ScenarioScreenRecord>,
        regressionConfig: RegressionConfig,
    ): ScreenRecord {
        return if (regressionConfig.isRecordScreenEnabled) {
            impl.get()
        } else {
            ScreenRecord.Stub
        }
    }
}
