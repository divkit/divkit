package com.yandex.div.storage.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import androidx.annotation.VisibleForTesting
import com.yandex.div.histogram.util.HistogramClock
import com.yandex.div.internal.Assert
import com.yandex.div.storage.database.DatabaseOpenHelper.CreateCallback
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
    ) : DatabaseOpenHelper.Database {

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
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
        }

        override fun rawQuery(query: String, selectionArgs: Array<out String?>?): Cursor {
            return db.rawQuery(query, selectionArgs)
        }

        override fun beginTransaction() {
            db.beginTransaction()
        }

        override fun setTransactionSuccessful() {
            db.setTransactionSuccessful()
        }

        override fun endTransaction() {
            db.endTransaction()
        }

        override fun compileStatement(sql: String): SQLiteStatement {
            return db.compileStatement(sql)
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
        private var readableUsersCount = 0
        private var readableDatabase: SQLiteDatabase? = null

        private var writableUsersCount = 0
        private var writableDatabase: SQLiteDatabase? = null

        @Synchronized
        fun openWritableDatabase(): SQLiteDatabase {
            databaseOpenTracker.measureOpen {
                writableDatabase = databaseHelper.writableDatabase
            }?.let(histogramRecorder::reportDatabaseOpenTime)
            writableUsersCount++
            return writableDatabase!!
        }

        @Synchronized
        fun openReadableDatabase(): SQLiteDatabase {
            databaseOpenTracker.measureOpen {
                readableDatabase = databaseHelper.readableDatabase
            }?.let(histogramRecorder::reportDatabaseOpenTime)
            readableUsersCount++
            return readableDatabase!!
        }

        @Synchronized
        fun closeDatabase(db: SQLiteDatabase) {
            if (db == writableDatabase) {
                if (writableUsersCount > 0) {
                    writableUsersCount--
                }
                if (writableUsersCount == 0) {
                    db.close()
                }
            } else if (db == readableDatabase) {
                if (readableUsersCount > 0) {
                    readableUsersCount--
                }
                if (readableUsersCount == 0) {
                    db.close()
                }
            } else {
                Assert.fail("Trying to close unknown database from DatabaseManager")
                db.close()
            }
        }
    }
}
