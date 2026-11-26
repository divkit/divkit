package com.yandex.div.storage.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteStatement
import android.database.sqlite.SQLiteTransactionListener
import android.os.Build
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import android.util.Log
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper.OpenedDatabase
import com.yandex.div.storage.util.closeAll
import java.io.File

@InternalApi
public fun createAndroidDatabaseOpenHelper(
    context: Context,
    name: String,
    version: Int,
    create: DatabaseOpenHelper.CreateCallback,
    upgrade: DatabaseOpenHelper.UpgradeCallback,
    reportOpenTime: (Long) -> Unit,
    openWritable: (File) -> SQLiteDatabase = { SQLiteDatabase.openOrCreateDatabase(it, null) },
): DatabaseOpenHelper = SQLiteDatabaseOpenHelper(
    version = version,
    create = create,
    upgrade = upgrade,
    open = { writable -> openAndroidDatabase(context.getDatabasePath(name), writable, openWritable) },
    reportOpenTime = reportOpenTime,
)

@InternalApi
public fun isAndroidDatabaseLocked(error: androidx.sqlite.SQLiteException): Boolean =
    error is SQLiteDatabaseLockedException

@InternalApi
public fun SQLiteDatabase.asStorageDatabase(): TransactionOutcomeDatabase = AndroidSQLiteDatabase(this)

@Suppress("TooGenericExceptionCaught")
private fun openAndroidDatabase(
    file: File,
    writable: Boolean,
    openWritable: (File) -> SQLiteDatabase,
): OpenedDatabase {
    file.parentFile?.mkdirs()
    val database = try {
        openWritable(file)
    } catch (error: SQLiteException) {
        if (writable) throw error
        Log.e("DivStorage", "Could not open database for writing. Trying read-only", error)
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY)
    }
    try {
        if (!database.isReadOnly) {
            restoreWritableFileMode(database.path)
            database.setForeignKeyConstraintsEnabled(true)
        }
        return OpenedDatabase(
            database.asStorageDatabase(),
            isReadOnly = database.isReadOnly,
            isOpen = { database.isOpen },
            schemaVersion = { database.version },
        )
    } catch (error: Throwable) {
        closeAll(database, failure = error)
        throw error
    }
}

private fun restoreWritableFileMode(path: String) {
    val mode = OsConstants.S_IRUSR or OsConstants.S_IWUSR or
        OsConstants.S_IRGRP or OsConstants.S_IWGRP
    try {
        Os.chmod(path, mode)
    } catch (_: ErrnoException) {
        // SQLiteOpenHelper continued if setting the mode failed, for example on a shared file.
    }
}

private class AndroidSQLiteDatabase(private val db: SQLiteDatabase) : TransactionOutcomeDatabase {
    override val supportsReadTransactions: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM || !db.isReadOnly

    override fun execSQL(sql: String) = db.execSQL(sql)

    override fun query(
        table: String,
        columns: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        orderBy: String?,
        limit: String?,
    ) = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
        .asStorageCursor()

    override fun rawQuery(query: String, selectionArgs: Array<out String?>?) =
        db.rawQuery(query, selectionArgs).asStorageCursor()

    override fun beginTransaction() = db.beginTransaction()

    override fun beginTransaction(onRollback: () -> Unit) =
        db.beginTransactionWithListener(rollbackListener(onRollback))

    override fun beginReadTransaction(): Boolean {
        if (db.inTransaction()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            db.beginTransactionReadOnly()
        } else {
            check(!db.isReadOnly)
            db.beginTransactionNonExclusive()
        }
        return true
    }

    override fun setTransactionSuccessful() = db.setTransactionSuccessful()
    override fun endTransaction() = db.endTransaction()
    override fun compileStatement(sql: String) = db.compileStatement(sql).asStorageStatement()
    override fun close() = db.close()
}

private fun rollbackListener(onRollback: () -> Unit) = object : SQLiteTransactionListener {
    override fun onBegin() = Unit
    override fun onCommit() = Unit
    override fun onRollback() = onRollback.invoke()
}

private fun Cursor.asStorageCursor(): DatabaseOpenHelper.Cursor =
    object : DatabaseOpenHelper.Cursor {
        override val isClosed: Boolean get() = this@asStorageCursor.isClosed
        override fun moveToFirst(): Boolean = this@asStorageCursor.moveToFirst()
        override fun moveToNext(): Boolean = this@asStorageCursor.moveToNext()
        override fun getColumnIndex(name: String): Int = this@asStorageCursor.getColumnIndex(name)
        override fun getString(index: Int): String = this@asStorageCursor.getString(index)
        override fun getBlob(index: Int): ByteArray = this@asStorageCursor.getBlob(index)
        override fun isNull(index: Int): Boolean = this@asStorageCursor.isNull(index)
        override fun close(): Unit = this@asStorageCursor.close()
    }

private fun SQLiteStatement.asStorageStatement(): DatabaseOpenHelper.Statement =
    object : DatabaseOpenHelper.Statement {
        override fun bindString(index: Int, value: String) = this@asStorageStatement.bindString(index, value)
        override fun bindBlob(index: Int, value: ByteArray) = this@asStorageStatement.bindBlob(index, value)
        override fun bindNull(index: Int) = this@asStorageStatement.bindNull(index)
        override fun execute() = this@asStorageStatement.execute()
        override fun executeInsert(): Long = this@asStorageStatement.executeInsert()
        override fun executeUpdateDelete(): Int = this@asStorageStatement.executeUpdateDelete()
        override fun close(): Unit = this@asStorageStatement.close()
    }
