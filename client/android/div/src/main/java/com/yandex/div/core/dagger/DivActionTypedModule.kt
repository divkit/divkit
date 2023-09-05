package com.yandex.div.core.dagger

import com.yandex.div.core.actions.DivActionTypedArrayMutationHandler
import com.yandex.div.core.actions.DivActionTypedSetVariableHandler
import com.yandex.div.core.actions.DivActionTypedHandler
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
internal interface DivActionTypedModule {

    @Binds
    @DivScope
    @IntoSet
    fun provideArrayMutationActionHandler(
        impl: DivActionTypedArrayMutationHandler
    ): DivActionTypedHandler

    @Binds
    @DivScope
    @IntoSet
    fun provideSetVariableActionHandler(
        impl: DivActionTypedSetVariableHandler
    ): DivActionTypedHandler
}
