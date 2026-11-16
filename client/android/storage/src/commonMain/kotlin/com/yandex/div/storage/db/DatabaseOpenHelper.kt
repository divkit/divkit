package com.yandex.div.storage.db

import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelperProvider

private const val DB_INIT_VERSION = 1

internal class DatabaseOpenHelper(
    provider: DatabaseOpenHelperProvider,
    dbName: String,
) {
    private val helper = provider.provide(dbName, DB_INIT_VERSION, ::onCreate) { _, _, _ -> }

    val writableDatabase: Database get() = helper.writableDatabase

    private fun onCreate(db: Database) {
        db.execSQL(TemplateSchema.CREATE_TEMPLATES_TABLE_QUERY)
        db.execSQL(TemplateSchema.CREATE_TEMPLATE_USAGES_TABLE_QUERY)
    }

}
