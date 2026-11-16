package com.yandex.div.storage

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DivDatabaseStorageConcurrencyTest {
    private val path = temporaryDatabasePath()
    private val executor = Executors.newFixedThreadPool(2) { task ->
        Thread(task).apply { isDaemon = true }
    }
    private val storage = createStorage()
    private var closed = false

    @AfterTest
    fun tearDown() {
        executor.shutdownNow()
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS))
        if (!closed) storage.close()
        removeDatabase(path)
    }

    @Test
    fun `concurrent writes both persist`() = withPausedWrite { resume ->
        val second = executor.submit {
            storage.writeTemplates("second-card", mapOf("second" to byteArrayOf(2)))
        }
        assertWaiting(second)
        resume.countDown()
        second.get(10, TimeUnit.SECONDS)

        assertEquals(setOf("first", "second"), storage.readAllTemplates().keys)
    }

    @Test
    fun `separate storage instances wait for the database transaction`() = withPausedWrite { resume ->
        createStorage().use { another ->
            val second = executor.submit {
                another.writeTemplates("second-card", mapOf("second" to byteArrayOf(2)))
            }
            assertWaiting(second)
            resume.countDown()
            second.get(10, TimeUnit.SECONDS)

            assertEquals(setOf("first", "second"), storage.readAllTemplates().keys)
        }
    }

    @Test
    fun `read waits for the complete transaction`() = withPausedWrite { resume ->
        val read = executor.submit<Map<String, ByteArray>> { storage.readAllTemplates() }
        assertWaiting(read)
        resume.countDown()

        assertEquals(setOf("first"), read.get(10, TimeUnit.SECONDS).keys)
    }

    @Test
    fun `close waits for the active write`() = withPausedWrite { resume ->
        val close = executor.submit { storage.close() }
        assertWaiting(close)
        resume.countDown()
        close.get(10, TimeUnit.SECONDS)
        closed = true

        createStorage().use { restored ->
            assertEquals(setOf("first"), restored.readAllTemplates().keys)
        }
    }

    @Test
    fun `failed driver read releases the connection for another worker`() {
        val failRead = AtomicBoolean(true)
        val statementClosed = AtomicBoolean(false)
        var readStatement: SQLiteStatement? = null
        val driver = object : SQLiteDriver {
            override fun open(fileName: String): SQLiteConnection {
                val connection = BundledSQLiteDriver().open(fileName)
                return object : SQLiteConnection by connection {
                    override fun prepare(sql: String): SQLiteStatement {
                        val statement = connection.prepare(sql)
                        if (sql != "SELECT * FROM templates") return statement
                        readStatement = statement
                        return object : SQLiteStatement by statement {
                            override fun step(): Boolean {
                                if (failRead.getAndSet(false)) throw SQLiteException("read failed")
                                return statement.step()
                            }
                            override fun close() {
                                statement.close()
                                statementClosed.set(true)
                            }
                        }
                    }
                }
            }
        }
        val checked = createStorage(driver)
        try {
            checked.writeTemplates("card", mapOf("value" to byteArrayOf(1)))

            assertFailsWith<SQLiteException> { checked.readAllTemplates() }
            assertTrue(statementClosed.get())
            val next = executor.submit<Map<String, ByteArray>> { checked.readAllTemplates() }
            assertEquals(setOf("value"), next.get(10, TimeUnit.SECONDS).keys)
        } finally {
            if (!statementClosed.get()) readStatement?.close()
            checked.close()
        }
    }

    @Test
    fun `failed row decoding releases the connection for another worker`() {
        BundledSQLiteDriver().open(path).use { connection ->
            connection.execSQL(
                "CREATE TABLE templates(template_id TEXT PRIMARY KEY, wrong_data BLOB)"
            )
            connection.execSQL("CREATE TABLE template_usages(card_id TEXT, template_id TEXT)")
            connection.execSQL("INSERT INTO templates VALUES ('id', X'01')")
            connection.execSQL("PRAGMA user_version = 1")
        }
        var openedCursor: DatabaseOpenHelper.Cursor? = null
        val checked = BaseDivDatabaseStorage({ _, version, create, upgrade ->
            val helper = SQLiteDatabaseOpenHelper(
                BundledSQLiteDriver(), path, version, create, upgrade
            )
            object : DatabaseOpenHelper by helper {
                override val writableDatabase: DatabaseOpenHelper.Database
                    get() {
                        val database = helper.writableDatabase
                        return object : DatabaseOpenHelper.Database by database {
                            override fun rawQuery(
                                query: String,
                                selectionArgs: Array<out String?>?,
                            ): DatabaseOpenHelper.Cursor =
                                database.rawQuery(query, selectionArgs).also { openedCursor = it }
                        }
                    }
            }
        })
        try {
            assertFailsWith<IllegalArgumentException> { checked.readAllTemplates() }

            assertTrue(requireNotNull(openedCursor).isClosed)
            executor.submit { checked.clear() }.get(10, TimeUnit.SECONDS)
        } finally {
            openedCursor?.close()
            checked.close()
        }
    }

    private fun withPausedWrite(assertOperations: (CountDownLatch) -> Unit) {
        val entered = CountDownLatch(1)
        val resume = CountDownLatch(1)
        val values = object : AbstractMap<String, ByteArray>() {
            override val entries: Set<Map.Entry<String, ByteArray>>
                get() {
                    entered.countDown()
                    check(resume.await(10, TimeUnit.SECONDS))
                    return mapOf("first" to byteArrayOf(1)).entries
                }
        }
        val write = executor.submit { storage.writeTemplates("first-card", values) }
        try {
            assertTrue(entered.await(10, TimeUnit.SECONDS))
            assertOperations(resume)
        } finally {
            resume.countDown()
            write.get(10, TimeUnit.SECONDS)
        }
    }

    private fun assertWaiting(operation: Future<*>) {
        assertFailsWith<TimeoutException> { operation.get(200, TimeUnit.MILLISECONDS) }
    }

    private fun createStorage(driver: SQLiteDriver = BundledSQLiteDriver()) = BaseDivDatabaseStorage({ _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(driver, path, version, create, upgrade)
    })
}
