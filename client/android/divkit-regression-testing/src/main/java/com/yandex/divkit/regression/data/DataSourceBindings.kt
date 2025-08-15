package com.yandex.divkit.regression.data

import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module

@Module
internal interface DataSourceBindings {

    @Binds
    fun bindDataSource(impl: AssetScenariosDataSource): ScenariosDataSource
}
