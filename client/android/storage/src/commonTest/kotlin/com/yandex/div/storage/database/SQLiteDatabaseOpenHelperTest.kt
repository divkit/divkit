package com.yandex.div.storage.database

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.yandex.div.storage.removeDatabase
import com.yandex.div.storage.temporaryDatabasePath
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SQLiteDatabaseOpenHelperTest {
    private val path = temporaryDatabasePath()
    private val driver = FailingDriver()
    private val helper = SQLiteDatabaseOpenHelper(driver, path, 1, {}, { _, _, _ -> })

    @AfterTest
    fun tearDown() {
        driver.close()
        removeDatabase(path)
    }

    @Test
    fun `failed commit rolls back before the next transaction`() {
        helper.writableDatabase.use { db ->
            db.execSQL("CREATE TABLE parent(id TEXT PRIMARY KEY)")
            db.execSQL("CREATE TABLE child(parent_id TEXT REFERENCES parent(id) " +
                "DEFERRABLE INITIALLY DEFERRED)")
            db.beginTransaction()
            db.execSQL("INSERT INTO child VALUES ('missing')")
            db.setTransactionSuccessful()

            assertFailsWith<SQLiteException> { db.endTransaction() }
            db.beginTransaction()
            db.execSQL("INSERT INTO parent VALUES ('saved')")
            db.execSQL("INSERT INTO child VALUES ('saved')")
            db.setTransactionSuccessful()
            db.endTransaction()

            db.rawQuery("SELECT parent_id FROM child", null).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("saved", cursor.getString(0))
                assertFalse(cursor.moveToNext())
            }
        }
    }

    @Test
    fun `failed recovery invalidates the connection and preserves the commit error`() {
        helper.writableDatabase.use { db ->
            val commitError = SQLiteException("commit failed")
            val rollbackError = SQLiteException("rollback failed")
            driver.failures = mapOf("COMMIT" to commitError, "ROLLBACK" to rollbackError)
            db.beginTransaction()
            db.setTransactionSuccessful()

            val error = assertFailsWith<SQLiteException> { db.endTransaction() }

            assertSame(commitError, error)
            assertEquals(listOf(rollbackError), error.suppressedExceptions)
            assertTrue(driver.closed)
            assertFailsWith<IllegalStateException> { db.beginTransaction() }
        }
    }

    @Test
    fun `failed rollback invalidates the connection`() {
        helper.writableDatabase.use { db ->
            val rollbackError = SQLiteException("rollback failed")
            driver.failures = mapOf("ROLLBACK" to rollbackError)
            db.beginTransaction()

            val error = assertFailsWith<SQLiteException> { db.endTransaction() }

            assertSame(rollbackError, error)
            assertTrue(driver.closed)
            assertFailsWith<IllegalStateException> { db.execSQL("SELECT 1") }
        }
    }

    @Test
    fun `failed close does not replace the commit error or allow connection reuse`() {
        helper.writableDatabase.use { db ->
            val commitError = SQLiteException("commit failed")
            val rollbackError = SQLiteException("rollback failed")
            val closeError = SQLiteException("close failed")
            driver.failures = mapOf("COMMIT" to commitError, "ROLLBACK" to rollbackError)
            driver.closeError = closeError
            db.beginTransaction()
            db.setTransactionSuccessful()

            val error = assertFailsWith<SQLiteException> { db.endTransaction() }

            assertSame(commitError, error)
            assertEquals(listOf(rollbackError, closeError), error.suppressedExceptions)
            assertFailsWith<IllegalStateException> { db.execSQL("SELECT 1") }
        }
    }

    @Test
    fun `existing statement cannot execute after connection invalidation`() {
        helper.writableDatabase.use { db ->
            db.execSQL("CREATE TABLE records(value TEXT)")
            db.compileStatement("INSERT INTO records VALUES ('unexpected')").use { statement ->
                driver.failures = mapOf(
                    "COMMIT" to SQLiteException("commit failed"),
                    "ROLLBACK" to SQLiteException("rollback failed"),
                )
                driver.closeError = SQLiteException("close failed")
                db.beginTransaction()
                db.setTransactionSuccessful()
                assertFailsWith<SQLiteException> { db.endTransaction() }

                assertFails { statement.execute() }
            }
        }
    }

    @Test
    fun `existing cursor cannot read after connection invalidation`() {
        helper.writableDatabase.use { db ->
            db.execSQL("CREATE TABLE records(value TEXT)")
            db.execSQL("INSERT INTO records VALUES ('unexpected')")
            db.rawQuery("SELECT value FROM records", null).use { cursor ->
                driver.failures = mapOf(
                    "COMMIT" to SQLiteException("commit failed"),
                    "ROLLBACK" to SQLiteException("rollback failed"),
                )
                driver.closeError = SQLiteException("close failed")
                db.beginTransaction()
                db.setTransactionSuccessful()
                assertFailsWith<SQLiteException> { db.endTransaction() }

                assertFails { cursor.moveToFirst() }
            }
        }
    }

    @Test
    fun `new acquisition recovers while an invalidated lease remains open`() {
        helper.writableDatabase.use { stale ->
            stale.execSQL("CREATE TABLE records(value TEXT)")
            driver.failures = mapOf("ROLLBACK" to SQLiteException("rollback failed"))
            stale.beginTransaction()
            assertFailsWith<SQLiteException> { stale.endTransaction() }
            driver.failures = emptyMap()

            helper.writableDatabase.use { recovered ->
                stale.close()
                recovered.execSQL("INSERT INTO records VALUES ('recovered')")

                helper.readableDatabase.use { reader ->
                    reader.rawQuery("SELECT value FROM records", null).use { cursor ->
                        assertTrue(cursor.moveToFirst())
                        assertEquals("recovered", cursor.getString(0))
                    }
                }
            }
        }
    }

    @Test
    fun `reader can fall back to read only and writer opens a new connection`() {
        helper.writableDatabase.use { it.execSQL("CREATE TABLE records(value TEXT)") }
        val openError = SQLiteException("write access unavailable")
        driver.openError = openError
        val readWriteHelper = SQLiteDatabaseOpenHelper(
            version = 1,
            create = {},
            upgrade = { _, _, _ -> },
            open = { writable ->
                val isReadOnly = !writable && driver.openError != null
                val connection = if (isReadOnly) BundledSQLiteDriver().open(path) else driver.open(path)
                if (isReadOnly) {
                    connection.execSQL("PRAGMA query_only = ON")
                }
                SQLiteDatabaseOpenHelper.OpenedDatabase(
                    DriverDatabase(connection, isReadOnly),
                    isReadOnly = isReadOnly,
                    isOpen = { true },
                    schemaVersion = { 1 },
                )
            },
        )

        readWriteHelper.readableDatabase.use { reader ->
            reader.rawQuery("SELECT value FROM records", null).use { cursor ->
                assertFalse(cursor.moveToFirst())
            }
            assertSame(openError, assertFailsWith<SQLiteException> { readWriteHelper.writableDatabase })

            driver.openError = null
            readWriteHelper.writableDatabase.use { writer ->
                reader.close()
                writer.execSQL("INSERT INTO records VALUES ('saved')")
                writer.rawQuery("SELECT value FROM records", null).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("saved", cursor.getString(0))
                }
            }
        }
    }

    @Test
    fun `schema creation preserves its error when rollback also fails`() {
        val creationError = SQLiteException("create failed")
        val rollbackError = SQLiteException("rollback failed")
        driver.failures = mapOf("ROLLBACK" to rollbackError)
        val failingHelper = SQLiteDatabaseOpenHelper(
            driver, path, 1, { throw creationError }, { _, _, _ -> },
        )

        val error = assertFailsWith<SQLiteException> { failingHelper.writableDatabase }

        assertSame(creationError, error)
        assertEquals(listOf(rollbackError), error.suppressedExceptions)
    }

    @Test
    fun `failed begin does not retain its rollback observer`() {
        helper.writableDatabase.use { db ->
            val observed = kotlin.test.assertIs<TransactionOutcomeDatabase>(db)
            var rollbackCount = 0
            driver.failures = mapOf("BEGIN EXCLUSIVE TRANSACTION" to SQLiteException("begin failed"))
            assertFailsWith<SQLiteException> {
                observed.beginTransaction { rollbackCount++ }
            }
            driver.failures = emptyMap()

            db.beginTransaction()
            db.endTransaction()

            assertEquals(0, rollbackCount)
        }
    }

    private class FailingDriver : SQLiteDriver, AutoCloseable {
        var failures = emptyMap<String, SQLiteException>()
        var openError: SQLiteException? = null
        var closeError: SQLiteException? = null
        var closed = false
        private val connections = mutableListOf<SQLiteConnection>()

        override fun open(fileName: String): SQLiteConnection {
            openError?.let { throw it }
            val connection = BundledSQLiteDriver().open(fileName)
            connections.add(connection)
            return object : SQLiteConnection by connection {
                override fun prepare(sql: String): SQLiteStatement {
                    failures[sql]?.let { throw it }
                    return connection.prepare(sql)
                }

                override fun close() {
                    closeError?.let { throw it }
                    connection.close()
                    connections.remove(connection)
                    closed = true
                }
            }
        }

        override fun close() {
            connections.forEach { it.close() }
            connections.clear()
        }
    }
}
