package com.yandex.div.core.dagger

import com.yandex.div.core.actions.DivActionTypedArrayMutationHandler
import com.yandex.div.core.actions.DivActionTypedCopyToClipboardHandler
import com.yandex.div.core.actions.DivActionTypedSetVariableHandler
import com.yandex.div.core.actions.DivActionTypedHandler
import com.yandex.div.core.actions.DivActionTypedFocusElementHandler
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
internal interface DivActionTypedModule {

    @Binds
    @Singleton
    @IntoSet
    fun provideArrayMutationActionHandler(
        impl: DivActionTypedArrayMutationHandler
    ): DivActionTypedHandler

    @Binds
    @Singleton
    @IntoSet
    fun provideSetVariableActionHandler(
        impl: DivActionTypedSetVariableHandler
    ): DivActionTypedHandler

    @Binds
    @Singleton
    @IntoSet
    fun provideFocusElementActionHandler(
        impl: DivActionTypedFocusElementHandler
    ): DivActionTypedHandler

    @Binds
    @Singleton
    @IntoSet
    fun provideCopyToClipboardActionHandler(
        impl: DivActionTypedCopyToClipboardHandler
    ): DivActionTypedHandler
}
