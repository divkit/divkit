package com.yandex.div.storage.database

import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteFullException
import android.database.sqlite.SQLiteStatement
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.yandex.div.internal.KAssert
import com.yandex.div.storage.DivDataRepository.ActionOnError
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div.storage.util.closeSilently
import com.yandex.div.storage.util.endTransactionSilently
import java.io.Closeable

internal class StorageStatementExecutor(
        private val dbProvider: () -> DatabaseOpenHelper.Database,
) {
    /**
     * Executes provided statements one by one in a single transaction.
     */
    @WorkerThread
    @Throws(SQLException::class)
    fun execute(
            actionOnError: ActionOnError,
            vararg statements: StorageStatement
    ): ExecutionResult {
        assertOnWorkerThread()
        var db: DatabaseOpenHelper.Database? = null
        var compiler: ClosableSqlCompiler? = null
        var statementNumber = 1
        var lastStatement: StorageStatement? = null
        val exceptions = mutableListOf<DivStorageErrorException>()
        val executionErrorMessage = "Error during statements execution."

        fun handleException(e: Exception) {
            val errorMessage = "Exception at statement '$lastStatement' " +
                    "($statementNumber out ${statements.size})"
            when(actionOnError) {
                ActionOnError.ABORT_TRANSACTION -> {
                    throwWithLogging(message = errorMessage, exception = e)
                }
                ActionOnError.SKIP_ELEMENT -> {
                    exceptions.add(DivStorageErrorException(errorMessage, e))
                }
            }
        }

        fun executeCatchingSqlException(compiler: ClosableSqlCompiler, statement: StorageStatement) {
            try {
                statement.execute(compiler)
            } catch (e: SQLException) {
                handleException(e)
            } catch(e: IllegalStateException) {
                handleException(e)
            }
        }

        try {
            db = dbProvider()
            compiler = ClosableSqlCompiler(db)
            db.beginTransaction()
            statements.forEach { statement ->
                lastStatement = statement
                executeCatchingSqlException(compiler, statement)
                statementNumber++
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // Ok to catch exception here:
            // statements may throw both SQLiteException or ExecutionException
            // but kotlin does not support multi-catch.
            exceptions.add(DivStorageErrorException(executionErrorMessage, e))
        } catch (e: IllegalStateException) {
            exceptions.add(DivStorageErrorException(executionErrorMessage, e))
        } catch (e: SQLiteFullException) {
            exceptions.add(DivStorageErrorException(executionErrorMessage, e))
        } finally {
            db?.endTransactionSilently()
            compiler?.close()
            db?.closeSilently()
        }
        return ExecutionResult(exceptions)
    }

    @WorkerThread
    @Throws(SQLException::class)
    fun execute(
            vararg statements: StorageStatement
    ): ExecutionResult {
        return execute(ActionOnError.ABORT_TRANSACTION, *statements)
    }

    @VisibleForTesting
    fun assertOnWorkerThread() = KAssert.assertNotMainThread()

    @Throws(SQLException::class)
    private fun throwWithLogging(message: String, exception: Exception): Nothing {
        throw SQLException(message, exception)
    }
}

private class ClosableSqlCompiler(private val db: DatabaseOpenHelper.Database) : SqlCompiler, Closeable {
    private val createdStatements = mutableListOf<SQLiteStatement>()
    private val createdCursors = mutableListOf<Cursor>()

    override fun compileStatement(sql: String): SQLiteStatement {
        return db.compileStatement(sql).also { createdStatements.add(it) }
    }

    override fun compileQuery(sql: String, vararg selectionArgs: String): ReadState {
        return ReadState {
            db.rawQuery(sql, selectionArgs).also { createdCursors.add(it) }
        }
    }

    override fun close() {
        createdStatements.forEach { it.closeSilently() }
        createdStatements.clear()

        createdCursors.forEach {
            if (!it.isClosed) {
                it.closeSilently()
            }
        }
        createdCursors.clear()
    }
}
