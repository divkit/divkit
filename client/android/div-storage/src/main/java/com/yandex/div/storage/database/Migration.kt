package com.yandex.div.storage.database

import android.database.SQLException

internal fun interface Migration {
    @Throws(SQLException::class)
    fun migrate(db: DatabaseOpenHelper.Database)
}
