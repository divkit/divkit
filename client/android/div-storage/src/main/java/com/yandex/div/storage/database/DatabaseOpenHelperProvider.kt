package com.yandex.div.storage.database

import android.content.Context
import com.yandex.div.storage.database.DatabaseOpenHelper.CreateCallback
import com.yandex.div.storage.database.DatabaseOpenHelper.UpgradeCallback

internal fun interface DatabaseOpenHelperProvider {
    fun provide(
        context: Context,
        name: String,
        version: Int,
        ccb: CreateCallback,
        ucb: UpgradeCallback
    ): DatabaseOpenHelper
}
