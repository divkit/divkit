package com.yandex.div.state.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class DivStateDbOpenHelper(context: Context, databaseName: String) : SQLiteOpenHelper(
    context, databaseName, null, StateEntry.INIT_DB_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(StateSchema.SQL_CREATE_TABLE_QUERY)
        db.execSQL(StateSchema.SQL_CREATE_INDICES_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}