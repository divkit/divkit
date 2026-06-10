package com.yandex.div.compose.dagger

import com.yandex.div.compose.storedvalues.LazyStoredValuesStorage
import com.yandex.div.evaluable.ScopedStoredValueProvider
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module

@Module
internal interface DivViewModule {

    @Binds
    fun bindStoredValueProvider(impl: LazyStoredValuesStorage): ScopedStoredValueProvider
}
