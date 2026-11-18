package com.yandex.div.storage.database

import android.content.Context
import android.database.Cursor as AndroidCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement as AndroidStatement
import android.database.sqlite.SQLiteTransactionListener
import android.os.Build
import androidx.annotation.VisibleForTesting
import com.yandex.div.histogram.util.HistogramClock
import com.yandex.div.internal.Assert
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelper.CreateCallback
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Statement
import com.yandex.div.storage.database.DatabaseOpenHelper.UpgradeCallback
import com.yandex.div.storage.histogram.HistogramRecorder
import java.io.IOException

/**
 * Impl for [DatabaseOpenHelper] for android framework
 * [SQLiteDatabase].
 */
internal class AndroidDatabaseOpenHelper(
    context: Context,
    name: String,
    version: Int,
    ccb: CreateCallback,
    ucb: UpgradeCallback,
    histogramRecorder: HistogramRecorder,
) : DatabaseOpenHelper {
    private val databaseManager: DatabaseManager
    private val databaseOpenTracker = DatabaseOpenTracker()

    init {
        val openHelper = object : SQLiteOpenHelper(context, name, null, version) {
            override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
                ccb.onCreate(wrapDataBase(sqLiteDatabase))
            }

            override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                ucb.onUpgrade(wrapDataBase(sqLiteDatabase), oldVersion, newVersion)
            }

            override fun onConfigure(db: SQLiteDatabase) {
                db.setForeignKeyConstraintsEnabled(true)
                databaseOpenTracker.onDatabaseOpened()
            }
        }
        databaseManager = DatabaseManager(openHelper, histogramRecorder, databaseOpenTracker)
    }

    // to prevent close for concurrent user of the same database object
    override val readableDatabase: DatabaseOpenHelper.Database
        get() {
            return wrapDataBase(databaseManager.openReadableDatabase())
        }

    // to prevent close for concurrent user of the same database object
    override val writableDatabase: DatabaseOpenHelper.Database
        get() {
            return wrapDataBase(databaseManager.openWritableDatabase())
        }

    @VisibleForTesting
    fun wrapDataBase(database: SQLiteDatabase): DatabaseOpenHelper.Database {
        return AndroidSQLiteDatabase(database)
    }

    private inner class AndroidSQLiteDatabase(
        private val db: SQLiteDatabase
    ) : TransactionOutcomeDatabase {

        override val supportsReadTransactions: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM || !db.isReadOnly

        override fun execSQL(sql: String) {
            db.execSQL(sql)
        }

        override fun query(
            table: String,
            columns: Array<String?>?,
            selection: String?,
            selectionArgs: Array<String?>?,
            groupBy: String?,
            having: String?,
            orderBy: String?,
            limit: String?
        ): Cursor {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit).asStorageCursor()
        }

        override fun rawQuery(query: String, selectionArgs: Array<out String?>?): Cursor {
            return db.rawQuery(query, selectionArgs).asStorageCursor()
        }

        override fun beginTransaction() {
            db.beginTransaction()
        }

        override fun beginTransaction(onRollback: () -> Unit) {
            db.beginTransactionWithListener(object : SQLiteTransactionListener {
                override fun onBegin() = Unit
                override fun onCommit() = Unit
                override fun onRollback() = onRollback.invoke()
            })
        }

        override fun beginReadTransaction(): Boolean {
            if (db.inTransaction()) return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                db.beginTransactionReadOnly()
            } else {
                db.beginTransactionNonExclusive()
            }
            return true
        }

        override fun setTransactionSuccessful() {
            db.setTransactionSuccessful()
        }

        override fun endTransaction() {
            db.endTransaction()
        }

        override fun compileStatement(sql: String): Statement {
            return db.compileStatement(sql).asStorageStatement()
        }

        @Throws(IOException::class)
        override fun close() {
            databaseManager.closeDatabase(db)
        }
    }

    private class DatabaseOpenTracker {
        private var openedDuringMeasurement = false

        fun measureOpen(openDatabase: () -> Unit): Long? {
            openedDuringMeasurement = false
            val openingStarted = HistogramClock.uptime()
            openDatabase()
            return if (openedDuringMeasurement) HistogramClock.uptime() - openingStarted else null
        }

        fun onDatabaseOpened() {
            openedDuringMeasurement = true
        }
    }

    private class DatabaseManager(
        private val databaseHelper: SQLiteOpenHelper,
        private val histogramRecorder: HistogramRecorder,
        private val databaseOpenTracker: DatabaseOpenTracker,
    ) {
        private var usersCount = 0
        private var database: SQLiteDatabase? = null

        @Synchronized
        fun openWritableDatabase(): SQLiteDatabase {
            databaseOpenTracker.measureOpen {
                database = databaseHelper.writableDatabase
            }?.let(histogramRecorder::reportDatabaseOpenTime)
            usersCount++
            return database!!
        }

        @Synchronized
        fun openReadableDatabase(): SQLiteDatabase {
            databaseOpenTracker.measureOpen {
                database = databaseHelper.readableDatabase
            }?.let(histogramRecorder::reportDatabaseOpenTime)
            usersCount++
            return database!!
        }

        @Synchronized
        fun closeDatabase(db: SQLiteDatabase) {
            if (db == database) {
                if (usersCount > 0) {
                    usersCount--
                }
                if (usersCount == 0) {
                    db.close()
                }
            } else {
                Assert.fail("Trying to close unknown database from DatabaseManager")
                db.close()
            }
        }
    }
}

internal fun AndroidCursor.asStorageCursor(): Cursor = object : Cursor {
    override val isClosed: Boolean get() = this@asStorageCursor.isClosed
    override fun moveToFirst(): Boolean = this@asStorageCursor.moveToFirst()
    override fun moveToNext(): Boolean = this@asStorageCursor.moveToNext()
    override fun getColumnIndex(name: String): Int = this@asStorageCursor.getColumnIndex(name)
    override fun getString(index: Int): String = this@asStorageCursor.getString(index)
    override fun getBlob(index: Int): ByteArray = this@asStorageCursor.getBlob(index)
    override fun isNull(index: Int): Boolean = this@asStorageCursor.isNull(index)
    override fun close() = this@asStorageCursor.close()
}

private fun AndroidStatement.asStorageStatement(): Statement = object : Statement {
    override fun bindString(index: Int, value: String) = this@asStorageStatement.bindString(index, value)
    override fun bindBlob(index: Int, value: ByteArray) = this@asStorageStatement.bindBlob(index, value)
    override fun bindNull(index: Int) = this@asStorageStatement.bindNull(index)
    override fun execute() = this@asStorageStatement.execute()
    override fun executeInsert(): Long = this@asStorageStatement.executeInsert()
    override fun executeUpdateDelete(): Int = this@asStorageStatement.executeUpdateDelete()
    override fun close() = this@asStorageStatement.close()
}
