package com.yandex.div.storage.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import androidx.annotation.VisibleForTesting
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.Assert
import com.yandex.div.storage.database.DatabaseOpenHelper.CreateCallback
import com.yandex.div.storage.database.DatabaseOpenHelper.UpgradeCallback
import java.io.IOException

/**
 * Impl for [DatabaseOpenHelper] for android framework
 * [SQLiteDatabase].
 */
@Mockable
internal class AndroidDatabaseOpenHelper(
    context: Context,
    name: String,
    version: Int,
    ccb: CreateCallback,
    ucb: UpgradeCallback,
    private val useDatabaseManager: Boolean  = true,
) : DatabaseOpenHelper {
    private val mSQLiteOpenHelper: SQLiteOpenHelper
    private val databaseManager: DatabaseManager

    /**
     * [SQLiteOpenHelper]'s refcount is broken:
     * [SQLiteOpenHelper.getReadableDatabase] and [SQLiteOpenHelper.getWritableDatabase] call
     * [SQLiteOpenHelper.getDatabaseLocked]
     * which can cache db object and return in twice, but the problem is that ref count isn't updated,
     * and that initial ref count == 1, and [android.database.sqlite.SQLiteClosable.close]
     * decrements it (note: querying and other methods increment-decrement it too).
     * [SQLiteDatabase.onAllReferencesReleased] implementation closes the connection.
     * * If database object is reused, one of the close calls can drop connection and fail queries&stuff.
     * * It would be not wise to call [SQLiteClosable.acquireReference] by hand on database reuse
     * as future sqlite versions might be fixed.
     * * Let's just delay close calls until there are no more concurrent database objects.
     * * links
     * SQLiteCloseable
     * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/database/sqlite/SQLiteClosable.java
     * SQLiteDatabase
     * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/database/sqlite/SQLiteDatabase.java
     * SQLiteOpenHelper
     * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/database/sqlite/SQLiteOpenHelper.java
     */
    private val mOpenCloseLock = Any()
    private val mOpenCloseInfoMap: MutableMap<SQLiteDatabase, OpenCloseInfo> = HashMap()

    init {
        mSQLiteOpenHelper = object : SQLiteOpenHelper(context, name, null, version) {
            override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
                ccb.onCreate(wrapDataBase(sqLiteDatabase))
            }
            override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                ucb.onUpgrade(wrapDataBase(sqLiteDatabase), oldVersion, newVersion)
            }
            override fun onConfigure(db: SQLiteDatabase) {
                db.setForeignKeyConstraintsEnabled(true)
            }
        }
        databaseManager = DatabaseManager(mSQLiteOpenHelper)
    }

    // to prevent close for concurrent user of the same database object
    override val readableDatabase: DatabaseOpenHelper.Database
        get() {
            return if (useDatabaseManager) {
                wrapDataBase(databaseManager.openReadableDatabase())
            } else {
                synchronized(mOpenCloseLock) { // to prevent close for concurrent user of the same database object
                    wrapDataBase(mSQLiteOpenHelper.readableDatabase)
                }
            }
        }

    // to prevent close for concurrent user of the same database object
    override val writableDatabase: DatabaseOpenHelper.Database
        get() {
            return if (useDatabaseManager) {
                wrapDataBase(databaseManager.openWritableDatabase())
            } else {
                synchronized(mOpenCloseLock) { // to prevent close for concurrent user of the same database object
                    wrapDataBase(mSQLiteOpenHelper.writableDatabase)
                }
            }
        }

    @VisibleForTesting
    fun wrapDataBase(sqLiteDatabase: SQLiteDatabase): DatabaseOpenHelper.Database {
        return AndroidSQLiteDatabase(sqLiteDatabase, getOpenCloseInfo(sqLiteDatabase))
    }

    private fun getOpenCloseInfo(sqLiteDatabase: SQLiteDatabase): OpenCloseInfo {
        synchronized(mOpenCloseLock) {
            // for wrapDataBase calls via onCreate/onUpgrade
            var info = mOpenCloseInfoMap[sqLiteDatabase]
            if (info == null) {
                info = OpenCloseInfo()
                mOpenCloseInfoMap[sqLiteDatabase] = info
            }
            ++info.currentlyOpenedCount
            return info
        }
    }

    private inner class AndroidSQLiteDatabase(private val mDb: SQLiteDatabase, private val mOpenCloseInfo: OpenCloseInfo) : DatabaseOpenHelper.Database {
        override fun execSQL(sql: String) {
            mDb.execSQL(sql)
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
            return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
        }

        override fun rawQuery(query: String, selectionArgs: Array<out String?>?): Cursor {
            return mDb.rawQuery(query, selectionArgs)
        }

        override fun beginTransaction() {
            mDb.beginTransaction()
        }

        override fun setTransactionSuccessful() {
            mDb.setTransactionSuccessful()
        }

        override fun endTransaction() {
            mDb.endTransaction()
        }

        override fun compileStatement(sql: String): SQLiteStatement {
            return mDb.compileStatement(sql)
        }

        @Throws(IOException::class)
        override fun close() {
            if (useDatabaseManager) {
                databaseManager.closeDatabase(mDb)
                return
            }

            synchronized(mOpenCloseLock) {
                if (--mOpenCloseInfo.currentlyOpenedCount > 0) {
                    ++mOpenCloseInfo.postponedCloseCount
                } else {
                    mOpenCloseInfoMap.remove(mDb)
                    // we're the last user, close as many times as it was opened, as we can't be sure
                    // that "ref count isn't incremented on reuse" bug isn't fixed
                    while (mOpenCloseInfo.postponedCloseCount > 0) {
                        mDb.close()
                        --mOpenCloseInfo.postponedCloseCount
                    }
                }
            }
        }
    }

    private class OpenCloseInfo {
        var currentlyOpenedCount = 0
        var postponedCloseCount = 0
    }

    private class DatabaseManager(
        private val databaseHelper: SQLiteOpenHelper,
    ) {
        private val readableUsers = mutableSetOf<Thread>()
        private var readableUsersCount = 0
        private var readableDatabase: SQLiteDatabase? = null

        private val writableUsers = mutableSetOf<Thread>()
        private var writableUsersCount = 0
        private var writableDatabase: SQLiteDatabase? = null

        @Synchronized
        fun openWritableDatabase(): SQLiteDatabase {
            writableDatabase = databaseHelper.writableDatabase
            writableUsersCount++
            writableUsers.add(Thread.currentThread())
            return writableDatabase!!
        }

        @Synchronized
        fun openReadableDatabase(): SQLiteDatabase {
            readableDatabase = databaseHelper.readableDatabase
            readableUsersCount++
            readableUsers.add(Thread.currentThread())
            return readableDatabase!!
        }

        @Synchronized
        fun closeDatabase(mDb: SQLiteDatabase) {
            if (mDb == writableDatabase) {
                writableUsers.remove(Thread.currentThread())
                if (writableUsers.isEmpty()) {
                    while (writableUsersCount-- > 0) {
                        writableDatabase!!.close()
                    }
                }
            } else if (mDb == readableDatabase) {
                readableUsers.remove(Thread.currentThread())
                if (readableUsers.isEmpty()) {
                    while (readableUsersCount-- > 0) {
                        readableDatabase!!.close()
                    }
                }
            } else {
                Assert.fail("Trying to close unknown database from DatabaseManager")
                mDb.close()
            }
        }
    }
}
