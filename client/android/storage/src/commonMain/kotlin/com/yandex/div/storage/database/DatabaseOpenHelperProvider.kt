package com.yandex.div.storage.database

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.database.DatabaseOpenHelper.CreateCallback
import com.yandex.div.storage.database.DatabaseOpenHelper.UpgradeCallback

@InternalApi
public fun interface DatabaseOpenHelperProvider {
    public fun provide(
        name: String,
        version: Int,
        ccb: CreateCallback,
        ucb: UpgradeCallback
    ): DatabaseOpenHelper
}
