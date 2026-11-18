package com.yandex.div.storage.database

import androidx.annotation.AnyThread
import androidx.sqlite.SQLiteException as SQLException
import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Statement as SQLiteStatement
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.util.closeAll
import com.yandex.div.storage.util.sqliteException

internal class StorageStatementExecutor(
        private val sqlExceptionFactory: (String, Throwable) -> SQLException = ::sqliteException,
        private val dbProvider: () -> DatabaseOpenHelper.Database,
) {
    /**
     * Executes provided statements one by one in a single transaction.
     */
    @AnyThread
    @Suppress("TooGenericExceptionCaught")
    @Throws(SQLException::class)
    fun execute(
            actionOnError: ActionOnError,
            vararg statements: StorageStatement
    ): ExecutionResult {
        var db: DatabaseOpenHelper.Database? = null
        var compiler: ClosableSqlCompiler? = null
        var transactionStarted = false
        var failure: Throwable? = null
        var statementNumber = 1
        var lastStatement: StorageStatement? = null
        val exceptions = mutableListOf<DivStorageErrorException>()
        val executionErrorMessage = "Error during statements execution."

        try {
            db = dbProvider()
            compiler = ClosableSqlCompiler(db)
            db.beginObservedTransaction(exceptions)
            transactionStarted = true
            statements.forEach { statement ->
                lastStatement = statement
                executeCatchingSqlException(compiler, statement) { e ->
                    val errorMessage = "Exception at statement '$lastStatement' " +
                            "($statementNumber out ${statements.size})"
                    when (actionOnError) {
                        ActionOnError.ABORT_TRANSACTION -> {
                            throwWithLogging(message = errorMessage, exception = e)
                        }

                        ActionOnError.SKIP_ELEMENT -> {
                            exceptions.add(DivStorageErrorException(errorMessage, e))
                        }
                    }
                }
                statementNumber++
            }
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            failure = e
            // Ok to catch exception here:
            // statements may throw both SQLiteException or ExecutionException
            // but kotlin does not support multi-catch.
            exceptions.add(DivStorageErrorException(executionErrorMessage, e))
        } catch (e: IllegalStateException) {
            failure = e
            exceptions.add(DivStorageErrorException(executionErrorMessage, e))
        } catch (error: Throwable) {
            failure = error
            throw error
        } finally {
            closeAll(
                if (transactionStarted) AutoCloseable { db?.endTransaction() } else null,
                compiler,
                db,
                failure = failure ?: exceptions.firstOrNull()?.cause,
            )
        }
        return ExecutionResult(exceptions)
    }

    @AnyThread
    @Throws(SQLException::class)
    fun execute(
            vararg statements: StorageStatement
    ): ExecutionResult {
        return execute(ActionOnError.ABORT_TRANSACTION, *statements)
    }

    private fun DatabaseOpenHelper.Database.beginObservedTransaction(
        exceptions: MutableList<DivStorageErrorException>,
    ) {
        if (this is TransactionOutcomeDatabase) {
            beginTransaction {
                if (exceptions.isEmpty()) {
                    exceptions.add(DivStorageErrorException("Transaction was rolled back"))
                }
            }
        } else {
            beginTransaction()
        }
    }

    private fun executeCatchingSqlException(
        compiler: ClosableSqlCompiler,
        statement: StorageStatement,
        handleException: (e: Exception) -> Unit,
    ) {
        try {
            statement.execute(compiler)
        } catch (e: SQLException) {
            handleException(e)
        } catch (e: IllegalStateException) {
            handleException(e)
        }
    }

    @Throws(SQLException::class)
    private fun throwWithLogging(message: String, exception: Exception): Nothing {
        throw sqlExceptionFactory(message, exception)
    }
}

internal class ClosableSqlCompiler(private val db: DatabaseOpenHelper.Database) : SqlCompiler, AutoCloseable {
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
        val resources = createdStatements + createdCursors.filterNot { it.isClosed }
        createdStatements.clear()
        createdCursors.clear()
        closeAll(*resources.toTypedArray())
    }
}
