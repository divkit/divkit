package com.yandex.divkit.regression.di

import android.content.Context
import com.yandex.divkit.regression.Div2ViewCreator
import com.yandex.divkit.regression.ScenarioViewCreator
import com.yandex.divkit.regression.data.DataSourceBindings
import com.yandex.divkit.regression.data.ScenariosRepository
import com.yandex.yatagan.BindsInstance
import com.yandex.yatagan.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataSourceBindings::class, CoroutinesModule::class])
interface RegressionComponent {

    val scenariosRepository: ScenariosRepository
    val div2ViewCreator: Div2ViewCreator
    val scenarioViewCreator: ScenarioViewCreator

    @Component.Builder
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance div2ViewCreator: Div2ViewCreator,
            @BindsInstance scenarioViewCreator: ScenarioViewCreator,
        ): RegressionComponent
    }
}
