package com.yandex.div.storage

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteException
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BaseDivDatabaseStorageLifecycleTest {
    private val path = temporaryDatabasePath()
    private val failure = SQLiteException("database failed")
    private var failOpen = false
    private var failRead = false
    private var openCount = 0
    private var helperCount = 0
    private val driver = object : SQLiteDriver {
        override fun open(fileName: String): SQLiteConnection {
            openCount++
            if (failOpen) throw failure
            val connection = BundledSQLiteDriver().open(fileName)
            return object : SQLiteConnection by connection {
                override fun prepare(sql: String): SQLiteStatement {
                    if (failRead && sql == "SELECT * FROM templates") throw failure
                    return connection.prepare(sql)
                }
            }
        }
    }
    private val storage = BaseDivDatabaseStorage({ _, version, create, upgrade ->
        helperCount++
        SQLiteDatabaseOpenHelper(driver, path, version, create, upgrade)
    })

    @AfterTest
    fun tearDown() {
        storage.close()
        removeDatabase(path)
    }

    @Test
    fun `close before use creates the database schema`() {
        storage.close()

        BundledSQLiteDriver().open(path).use { connection ->
            connection.prepare("SELECT name FROM sqlite_master WHERE type='table'").use { query ->
                val tables = mutableSetOf<String>()
                while (query.step()) tables.add(query.getText(0))
                assertEquals(setOf("templates", "template_usages"), tables)
            }
        }
    }

    @Test
    fun `close retries its initial opening after the provider recovers`() {
        failOpen = true
        assertSame(failure, assertFailsWith<SQLiteException> { storage.close() })
        failOpen = false

        storage.close()

        assertEquals(2, openCount)
        assertFailsWith<IllegalStateException> { storage.readAllTemplates() }
    }

    @Test
    fun `read retries after an initial close could not open the database`() {
        failOpen = true
        assertSame(failure, assertFailsWith<SQLiteException> { storage.close() })
        failOpen = false

        val templates = storage.readAllTemplates()

        assertTrue(templates.isEmpty())
        assertEquals(2, openCount)
    }

    @Test
    fun `close retries after an initial read could not open the database`() {
        failOpen = true
        assertSame(failure, assertFailsWith<SQLiteException> { storage.readAllTemplates() })
        failOpen = false

        storage.close()

        assertEquals(2, openCount)
        assertFailsWith<IllegalStateException> { storage.readAllTemplates() }
    }

    @Test
    fun `read retries the complete initial acquisition after opening fails`() {
        failOpen = true
        assertSame(failure, assertFailsWith<SQLiteException> { storage.readAllTemplates() })
        failOpen = false

        val templates = storage.readAllTemplates()

        assertTrue(templates.isEmpty())
        assertEquals(2, helperCount)
        assertEquals(2, openCount)
    }

    @Test
    fun `close after an opened database operation fails does not reopen it`() {
        storage.writeTemplates("card", mapOf("template" to byteArrayOf(1)))
        failRead = true
        assertSame(failure, assertFailsWith<SQLiteException> { storage.readAllTemplates() })
        failOpen = true

        storage.close()

        assertEquals(1, openCount)
        assertFailsWith<IllegalStateException> { storage.readAllTemplates() }
    }

    @Test
    fun `repeated close does not reopen an already closed database`() {
        assertTrue(storage.readAllTemplates().isEmpty())
        storage.close()

        storage.close()

        assertEquals(1, openCount)
        assertFailsWith<IllegalStateException> { storage.readAllTemplates() }
    }
}
