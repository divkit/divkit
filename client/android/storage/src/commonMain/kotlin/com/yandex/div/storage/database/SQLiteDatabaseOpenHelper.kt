package com.yandex.div.storage.database

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.execSQL
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.util.closeAll
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.synchronized
import kotlinx.atomicfu.locks.withLock

/**
 * SQLiteDriver adapter for the database boundary used by the existing storage algorithms.
 * Acquisitions share a reference-counted connection, like the Android open helper.
 * Transactions and cursors are confined to the thread that starts them.
 * A failed cursor read closes the cursor and releases its connection lock.
 * Connections wait up to five seconds for locks held by other connections to the same file.
 */
@InternalApi
public class SQLiteDatabaseOpenHelper(
    private val driver: SQLiteDriver,
    private val path: String,
    private val version: Int,
    private val create: DatabaseOpenHelper.CreateCallback,
    private val upgrade: DatabaseOpenHelper.UpgradeCallback,
) : DatabaseOpenHelper {
    private val managerLock = SynchronizedObject()
    private var database: SharedDatabase? = null
    private var opening = false

    init {
        require(version >= 1)
    }

    override val readableDatabase: DatabaseOpenHelper.Database get() = acquireDatabase()
    override val writableDatabase: DatabaseOpenHelper.Database get() = acquireDatabase()

    private fun acquireDatabase(): DatabaseOpenHelper.Database = synchronized(managerLock) {
        check(!opening) { "Recursive database initialization" }
        val shared = database?.takeIf { it.database.isOpen } ?: run {
            opening = true
            try {
                SharedDatabase(openDatabase()).also { database = it }
            } finally {
                opening = false
            }
        }
        shared.usersCount++
        val db = shared.database
        object : DatabaseOpenHelper.Database by db, TransactionOutcomeDatabase {
            private var closed = false

            override val supportsReadTransactions: Boolean get() = db.supportsReadTransactions

            override fun beginTransaction(onRollback: () -> Unit) = db.beginTransaction(onRollback)

            override fun beginReadTransaction(): Boolean = db.beginReadTransaction()

            override fun close() {
                val toClose = synchronized(managerLock) {
                    if (closed) {
                        null
                    } else {
                        closed = true
                        shared.usersCount--
                        if (shared.usersCount == 0) {
                            if (database === shared) database = null
                            db
                        } else null
                    }
                }
                toClose?.close()
            }
        }
    }

    private class SharedDatabase(val database: DriverDatabase) {
        var usersCount = 0
    }

    @Suppress("TooGenericExceptionCaught")
    private fun openDatabase(): DriverDatabase {
        val connection = driver.open(path)
        val database = DriverDatabase(connection)
        try {
            connection.execSQL("PRAGMA busy_timeout = 5000")
            connection.execSQL("PRAGMA foreign_keys = ON")
            val oldVersion = connection.scalar("PRAGMA user_version").toInt()
            check(oldVersion <= version) { "Cannot downgrade database" }
            if (oldVersion != version) {
                database.beginTransaction()
                AutoCloseable { database.endTransaction() }.use {
                    val currentVersion = connection.scalar("PRAGMA user_version").toInt()
                    check(currentVersion <= version) { "Cannot downgrade database" }
                    when {
                        currentVersion == 0 -> create.onCreate(database)
                        currentVersion < version -> upgrade.onUpgrade(database, currentVersion, version)
                    }
                    connection.execSQL("PRAGMA user_version = $version")
                    database.setTransactionSuccessful()
                }
            }
            return database
        } catch (error: Throwable) {
            closeAll(database, failure = error)
            throw error
        }
    }
}

private class DriverDatabase(connection: SQLiteConnection) : TransactionOutcomeDatabase {
    override val supportsReadTransactions: Boolean get() = true
    private val lock = reentrantLock()
    private val transactions = mutableListOf<Transaction>()
    private val open = atomic(true)
    val isOpen: Boolean get() = open.value
    private var openConnection: SQLiteConnection? = connection
    private val connection: SQLiteConnection
        get() = checkNotNull(openConnection) { "Database connection is closed" }

    override fun execSQL(sql: String): Unit = lock.withLock { connection.execSQL(sql) }

    override fun query(
        table: String,
        columns: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        orderBy: String?,
        limit: String?,
    ): DatabaseOpenHelper.Cursor {
        val query = buildString {
            append("SELECT ").append(columns?.joinToString() ?: "*").append(" FROM ").append(table)
            if (!selection.isNullOrEmpty()) append(" WHERE ").append(selection)
            if (!groupBy.isNullOrEmpty()) append(" GROUP BY ").append(groupBy)
            if (!having.isNullOrEmpty()) append(" HAVING ").append(having)
            if (!orderBy.isNullOrEmpty()) append(" ORDER BY ").append(orderBy)
            if (!limit.isNullOrEmpty()) append(" LIMIT ").append(limit)
        }
        return rawQuery(query, selectionArgs)
    }

    @Suppress("TooGenericExceptionCaught")
    override fun rawQuery(query: String, selectionArgs: Array<out String?>?): DatabaseOpenHelper.Cursor {
        lock.lock()
        var statement: SQLiteStatement? = null
        try {
            statement = connection.prepare(query)
            selectionArgs?.forEachIndexed { index, value ->
                if (value == null) statement.bindNull(index + 1) else statement.bindText(index + 1, value)
            }
            return DriverCursor(statement, lock) { connection }
        } catch (error: Throwable) {
            try {
                statement?.close()
            } finally {
                lock.unlock()
            }
            throw error
        }
    }

    override fun beginTransaction() = beginTransaction {}

    @Suppress("TooGenericExceptionCaught")
    override fun beginTransaction(onRollback: () -> Unit) {
        lock.lock()
        try {
            check(transactions.lastOrNull()?.successful != true) { "Transaction is already successful" }
            if (transactions.isEmpty()) connection.execSQL("BEGIN EXCLUSIVE TRANSACTION")
            transactions.add(Transaction(onRollback))
        } catch (error: Throwable) {
            lock.unlock()
            throw error
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun beginReadTransaction(): Boolean {
        lock.lock()
        if (transactions.isNotEmpty()) {
            lock.unlock()
            return false
        }
        try {
            connection.execSQL("BEGIN DEFERRED TRANSACTION")
            transactions.add(Transaction {})
            return true
        } catch (error: Throwable) {
            lock.unlock()
            throw error
        }
    }

    override fun setTransactionSuccessful(): Unit = lock.withLock {
        val transaction = checkNotNull(transactions.lastOrNull()) { "No transaction is active" }
        check(!transaction.successful) { "Transaction is already successful" }
        transaction.successful = true
    }

    override fun endTransaction(): Unit = lock.withLock {
        check(transactions.isNotEmpty()) { "No transaction is active" }
        val transaction = transactions.removeAt(transactions.lastIndex)
        try {
            val successful = transaction.successful && !transaction.childFailed
            if (transactions.isEmpty()) {
                finishTransaction(successful)
            } else if (!successful) {
                transactions.last().childFailed = true
            }
            if (!successful) transaction.onRollback()
        } finally {
            lock.unlock()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun finishTransaction(successful: Boolean) {
        try {
            connection.execSQL(if (successful) "COMMIT" else "ROLLBACK")
        } catch (error: Throwable) {
            val recovered = successful && tryCleanup(error) { connection.execSQL("ROLLBACK") }
            if (!recovered) tryCleanup(error) { close() }
            throw error
        }
    }

    override fun compileStatement(sql: String): DatabaseOpenHelper.Statement = lock.withLock {
        DriverStatement({ connection }, connection.prepare(sql), lock)
    }

    override fun close(): Unit = lock.withLock {
        val database = openConnection ?: return@withLock
        openConnection = null
        open.value = false
        database.close()
    }

    private class Transaction(val onRollback: () -> Unit) {
        var successful = false
        var childFailed = false
    }
}

@Suppress("TooGenericExceptionCaught")
private inline fun tryCleanup(error: Throwable, cleanup: () -> Unit): Boolean {
    return try {
        cleanup()
        true
    } catch (cleanupError: Throwable) {
        if (cleanupError !== error) error.addSuppressed(cleanupError)
        false
    }
}

private class DriverCursor(
    private val statement: SQLiteStatement,
    private val lock: ReentrantLock,
    private val connectionProvider: () -> SQLiteConnection,
) : DatabaseOpenHelper.Cursor {
    override var isClosed: Boolean = false
        private set

    override fun moveToFirst(): Boolean = read {
        statement.reset()
        statement.step()
    }

    override fun moveToNext(): Boolean = read { statement.step() }
    override fun getColumnIndex(name: String): Int = read { statement.getColumnNames().indexOf(name) }
    override fun getString(index: Int): String = read { statement.readText(index) }
    override fun getBlob(index: Int): ByteArray = read { statement.getBlob(index) }
    override fun isNull(index: Int): Boolean = read { statement.isNull(index) }

    @Suppress("TooGenericExceptionCaught")
    private inline fun <T> read(block: () -> T): T = lock.withLock {
        check(!isClosed) { "Cursor is closed" }
        try {
            connectionProvider()
            block()
        } catch (error: Throwable) {
            try {
                close()
            } catch (closeError: Throwable) {
                error.addSuppressed(closeError)
            }
            throw error
        }
    }

    override fun close(): Unit = lock.withLock {
        if (!isClosed) {
            try {
                statement.close()
            } finally {
                isClosed = true
                lock.unlock()
            }
        }
    }
}

private class DriverStatement(
    private val connectionProvider: () -> SQLiteConnection,
    private val statement: SQLiteStatement,
    private val lock: ReentrantLock,
) : DatabaseOpenHelper.Statement {
    override fun bindString(index: Int, value: String): Unit =
        withConnection { statement.bindText(index, value) }
    override fun bindBlob(index: Int, value: ByteArray): Unit =
        withConnection { statement.bindBlob(index, value) }
    override fun bindNull(index: Int): Unit = withConnection { statement.bindNull(index) }

    override fun execute(): Unit = withConnection {
        try {
            statement.step()
        } finally {
            statement.reset()
        }
    }

    override fun executeInsert(): Long = withConnection { connection ->
        execute()
        if (connection.scalar("SELECT changes()") == 0L) -1
        else connection.scalar("SELECT last_insert_rowid()")
    }

    override fun executeUpdateDelete(): Int = withConnection { connection ->
        execute()
        connection.scalar("SELECT changes()").toInt()
    }

    override fun close(): Unit = lock.withLock { statement.close() }

    private inline fun <T> withConnection(block: (SQLiteConnection) -> T): T = lock.withLock {
        block(connectionProvider())
    }
}

private fun SQLiteConnection.scalar(sql: String): Long = prepare(sql).use {
    check(it.step())
    it.getLong(0)
}
