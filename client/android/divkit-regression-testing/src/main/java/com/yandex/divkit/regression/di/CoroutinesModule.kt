package com.yandex.divkit.regression.di

import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
internal object CoroutinesModule {

    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
