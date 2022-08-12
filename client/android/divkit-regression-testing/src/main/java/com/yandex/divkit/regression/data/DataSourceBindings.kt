package com.yandex.divkit.regression.data

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface DataSourceBindings {

    @Singleton
    @Binds
    fun bindDataSource(impl: AssetScenariosDataSource): ScenariosDataSource
}
