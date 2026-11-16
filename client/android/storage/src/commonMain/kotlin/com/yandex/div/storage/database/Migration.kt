package com.yandex.div.storage.database

import androidx.sqlite.SQLiteException as SQLException
import com.yandex.div.core.annotations.InternalApi

@InternalApi
public fun interface Migration {
    @Throws(SQLException::class)
    public fun migrate(db: DatabaseOpenHelper.Database)
}
