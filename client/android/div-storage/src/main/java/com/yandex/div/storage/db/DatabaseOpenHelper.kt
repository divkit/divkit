package com.yandex.div.storage.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_INIT_VERSION = 1

internal class DatabaseOpenHelper(
    context: Context,
    dbName: String,
) : SQLiteOpenHelper(context, dbName, null, DB_INIT_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TemplateSchema.CREATE_TEMPLATES_TABLE_QUERY)
        db.execSQL(TemplateSchema.CREATE_TEMPLATE_USAGES_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}
