package com.yandex.div.core.dagger

import com.yandex.div.core.actions.DivActionTypedArrayMutationHandler
import com.yandex.div.core.actions.DivActionTypedClearFocusHandler
import com.yandex.div.core.actions.DivActionTypedCopyToClipboardHandler
import com.yandex.div.core.actions.DivActionTypedDictSetValueHandler
import com.yandex.div.core.actions.DivActionTypedSetVariableHandler
import com.yandex.div.core.actions.DivActionTypedHandler
import com.yandex.div.core.actions.DivActionTypedFocusElementHandler
import com.yandex.div.core.actions.DivActionTypedTimerHandler
import com.yandex.yatagan.Binds
import com.yandex.yatagan.IntoSet
import com.yandex.yatagan.Module

@Module
internal interface DivActionTypedModule {

    @Binds
    @IntoSet
    fun provideArrayMutationActionHandler(
        impl: DivActionTypedArrayMutationHandler
    ): DivActionTypedHandler

    @Binds
    @IntoSet
    fun provideClearFocusActionHandler(
        impl: DivActionTypedClearFocusHandler
    ): DivActionTypedHandler

    @Binds
    @IntoSet
    fun provideCopyToClipboardActionHandler(
        impl: DivActionTypedCopyToClipboardHandler
    ): DivActionTypedHandler

    @Binds
    @IntoSet
    fun provideDictSetValueActionHandler(
        impl: DivActionTypedDictSetValueHandler
    ): DivActionTypedHandler

    @Binds
    @IntoSet
    fun provideFocusElementActionHandler(
        impl: DivActionTypedFocusElementHandler
    ): DivActionTypedHandler

    @Binds
    @IntoSet
    fun provideSetVariableActionHandler(
        impl: DivActionTypedSetVariableHandler
    ): DivActionTypedHandler

    @Binds
    @IntoSet
    fun provideTimerActionHandler(
        impl: DivActionTypedTimerHandler
    ): DivActionTypedHandler
}
