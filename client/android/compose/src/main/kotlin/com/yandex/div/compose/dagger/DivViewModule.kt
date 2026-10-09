package com.yandex.div.compose.dagger

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.compose.storedvalues.LazyStoredValuesStorage
import com.yandex.div.evaluable.ScopedStoredValueProvider
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
        fun provideStates(data: DivData): MutableState<List<DivData.State>> = mutableStateOf(data.states)
    }
}
