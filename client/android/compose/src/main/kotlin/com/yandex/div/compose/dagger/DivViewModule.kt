package com.yandex.div.compose.dagger

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.compose.storedvalues.LazyStoredValuesStorage
import com.yandex.div.compose.variables.ScopedVariableProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.internal.expressions.FunctionProviderDecorator
import com.yandex.div.internal.expressions.toLocalFunctions
import com.yandex.div2.DivData
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import javax.inject.Named

@Module
internal interface DivViewModule {

    @Binds
    fun bindStoredValuesStorage(impl: LazyStoredValuesStorage): DivStoredValuesStorage

    @Binds
    fun bindStoredValueProvider(impl: LazyStoredValuesStorage): ScopedStoredValueProvider

    companion object {

        @Provides
        @Named(Names.CARD_ID)
        fun provideCardId(data: DivData): String = data.logId

        @Provides
        @DivViewScope
        @Named(Names.ROOT_LOCAL_COMPONENT)
        fun provideRootLocalComponent(
            data: DivData,
            componentBuilder: DivLocalComponent.Builder,
            @Named(Names.HOST_VARIABLES)
            hostVariableController: DivVariableController,
        ): DivLocalComponent {
            val variableProvider = ScopedVariableProvider(
                controller = DivVariableController(hostVariableController),
                parent = null,
            )
            val baseFunctionProvider = FunctionProviderDecorator(GeneratedBuiltinFunctionProvider)
            val localFunctions = data.functions.orEmpty().toLocalFunctions()
            val component = componentBuilder.build(
                functionProvider = baseFunctionProvider + localFunctions,
                variableProvider = variableProvider,
            )

            data.variables.orEmpty().forEach { variableData ->
                component.variableAdapter.convert(variableData)?.let(variableProvider::declare)
            }

            return component
        }

        @Provides
        @DivViewScope
        fun provideStates(data: DivData): MutableState<List<DivData.State>> {
            return mutableStateOf(data.states)
        }
    }
}
