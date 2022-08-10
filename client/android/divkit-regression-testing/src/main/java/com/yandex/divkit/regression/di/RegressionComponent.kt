package com.yandex.divkit.regression.di

import android.content.Context
import com.yandex.divkit.regression.Div2ViewCreator
import com.yandex.divkit.regression.RegressionConfig
import com.yandex.divkit.regression.data.DataSourceBindings
import com.yandex.divkit.regression.data.ScenariosRepository
import com.yandex.divkit.regression.screenrecord.ScreenRecordComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataSourceBindings::class, CoroutinesModule::class, SubcomponentsModule::class])
interface RegressionComponent {

    val scenariosRepository: ScenariosRepository
    val regressionConfig: RegressionConfig
    val div2ViewCreator: Div2ViewCreator

    val screenRecordComponent: ScreenRecordComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance viewCreator: Div2ViewCreator
        ): RegressionComponent
    }
}
