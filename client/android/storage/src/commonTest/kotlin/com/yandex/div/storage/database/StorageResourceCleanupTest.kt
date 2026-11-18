package com.yandex.div.storage.database

import androidx.sqlite.SQLiteException
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.StorageCodec
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelper.Statement
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.removeDatabase
import com.yandex.div.storage.temporaryDatabasePath
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StorageResourceCleanupTest {
    private val path = temporaryDatabasePath()
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(BundledSQLiteDriver(), path, version, create, upgrade)
    }

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `executor closes statements and database when transaction finalization fails`() {
        val real = provider.provide("test", 1, { }, { _, _, _ -> }).writableDatabase
        val events = mutableListOf<String>()
        val failure = SQLiteException("commit failed")
        val db = object : Database by real {
            override fun endTransaction() = throw failure
            override fun compileStatement(sql: String): Statement {
                val statement = real.compileStatement(sql)
                return object : Statement by statement {
                    override fun close() {
                        events.add("statement")
                        statement.close()
                    }
                }
            }
            override fun close() {
                events.add("database")
                real.close()
            }
        }
        try {
            val error = assertFailsWith<SQLiteException> {
                StorageStatementExecutor { db }.execute(object : StorageStatement {
                    override fun execute(compiler: SqlCompiler) {
                        compiler.compileStatement("SELECT 1")
                    }
                })
            }
            assertSame(failure, error)
            assertEquals(listOf("statement", "database"), events)
        } finally {
            if ("database" !in events) real.close()
        }
    }

    @Test
    fun `compiler closes remaining resources when one statement close fails`() {
        val real = provider.provide("test", 1, { }, { _, _, _ -> }).writableDatabase
        val events = mutableListOf<String>()
        val failure = SQLiteException("statement close failed")
        val db = object : Database by real {
            override fun compileStatement(sql: String): Statement {
                val statement = real.compileStatement(sql)
                return object : Statement by statement {
                    override fun close() {
                        events.add(sql)
                        statement.close()
                        if (sql == "SELECT 1") throw failure
                    }
                }
            }
            override fun close() {
                events.add("database")
                real.close()
            }
        }
        try {
            val error = assertFailsWith<SQLiteException> {
                StorageStatementExecutor { db }.execute(object : StorageStatement {
                    override fun execute(compiler: SqlCompiler) {
                        compiler.compileStatement("SELECT 1")
                        compiler.compileStatement("SELECT 2")
                    }
                })
            }
            assertSame(failure, error)
            assertEquals(listOf("SELECT 1", "SELECT 2", "database"), events)
        } finally {
            if ("database" !in events) real.close()
        }
    }

    @Test
    fun `read state releases its database when cursor close fails`() {
        val real = provider.provide("test", 1, { }, { _, _, _ -> }).writableDatabase
        var databaseClosed = false
        val failure = SQLiteException("cursor close failed")
        val cursor = real.rawQuery("SELECT 1", null)
        val state = ReadState(onCloseState = {
            real.close()
            databaseClosed = true
        }) {
            object : Cursor by cursor {
                override fun close() {
                    cursor.close()
                    throw failure
                }
            }
        }
        try {
            state.cursor

            assertSame(failure, assertFailsWith<SQLiteException> { state.close() })
            assertTrue(databaseClosed)
        } finally {
            if (!cursor.isClosed) cursor.close()
            if (!databaseClosed) real.close()
        }
    }

    @Test
    fun `partial compilation in clear closes statements and database`() {
        val events = mutableListOf<String>()
        var acquisition: Database? = null
        val failure = SQLiteException("compile failed")
        val wrappingProvider = DatabaseOpenHelperProvider { name, version, create, upgrade ->
            val helper = provider.provide(name, version, create, upgrade)
            object : DatabaseOpenHelper by helper {
                override val writableDatabase: Database
                    get() {
                        val real = helper.writableDatabase
                        acquisition = real
                        var compiled = false
                        return object : Database by real {
                            override fun compileStatement(sql: String): Statement {
                                if (compiled) throw failure
                                compiled = true
                                val statement = real.compileStatement(sql)
                                return object : Statement by statement {
                                    override fun close() {
                                        events.add("statement")
                                        statement.close()
                                    }
                                }
                            }
                            override fun close() {
                                events.add("database")
                                real.close()
                            }
                        }
                    }
            }
        }
        try {
            val result = DivStorage(StorageCodec.BYTES, wrappingProvider).removeAllTemplates()

            assertSame(failure, assertNotNull(result).cause)
            assertEquals(listOf("statement", "database"), events)
        } finally {
            if ("database" !in events) acquisition?.close()
        }
    }

}
