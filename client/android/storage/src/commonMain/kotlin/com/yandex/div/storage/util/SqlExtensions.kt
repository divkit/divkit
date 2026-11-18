package com.yandex.div.storage.util

import androidx.sqlite.SQLiteException
import com.yandex.div.storage.database.DatabaseOpenHelper.Cursor
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelper.Statement as SQLiteStatement
import com.yandex.div.storage.database.TransactionOutcomeDatabase

internal fun SQLiteStatement.bindNullableBlob(index: Int, value: ByteArray?) {
    if (value == null) bindNull(index) else bindBlob(index, value)
}

internal fun AutoCloseable.closeSilently() {
    close()
}

internal const val SQL_BIND_LIMIT = 900

internal fun Collection<*>.sqlPlaceholders(): String = joinToString(
    prefix = "(", separator = ",", postfix = ")"
) { "?" }

@Suppress("TooGenericExceptionCaught")
internal fun closeAll(vararg resources: AutoCloseable?, failure: Throwable? = null) {
    var error = failure
    resources.forEach { resource ->
        try {
            resource?.close()
        } catch (closeError: Throwable) {
            if (error == null) error = closeError
            else if (closeError !== error) error.addSuppressed(closeError)
        }
    }
    if (failure == null) error?.let { throw it }
}

internal fun Cursor.getBlobOrNull(
    index: Int,
): ByteArray? = if (isNull(index)) null else getBlob(index)

internal fun Cursor.getColumnIndexOrThrow(
    name: String,
): Int = getColumnIndex(name).also { require(it >= 0) { "Column '$name' not found" } }

internal fun sqliteException(message: String, cause: Throwable): SQLiteException =
    SQLiteException(message).also { it.addSuppressed(cause) }

internal inline fun <T> Database.withReadSnapshot(required: Boolean, block: () -> T): T {
    if (!required) return block()
    val ownsTransaction = if (this is TransactionOutcomeDatabase) {
        beginReadTransaction()
    } else {
        beginTransaction()
        true
    }
    if (!ownsTransaction) return block()
    return AutoCloseable { endTransaction() }.use {
        val result = block()
        setTransactionSuccessful()
        result
    }
}

internal fun Database.forEachReadBatch(
    batches: List<List<String>>,
    block: (sqlIds: String, selectionArgs: List<String>) -> Unit,
) {
    if (requiresQuotedIds(batches.size)) {
        block(quoteIds(batches.flatten()), emptyList())
        return
    }
    withReadSnapshot(required = batches.size > 1) {
        batches.forEach { ids -> block(ids.sqlPlaceholders(), ids) }
    }
}

internal fun Database.requiresQuotedIds(batchCount: Int): Boolean =
    batchCount > 1 && this is TransactionOutcomeDatabase && !supportsReadTransactions

/**
 * Quotes bound IDs using the database text encoding, preserving embedded zero characters.
 * Only validated SQLite-generated hex literals are inserted into the resulting SQL list.
 */
internal fun Database.quoteIds(ids: List<String>): String {
    val literals = ArrayList<String>(ids.size)
    ids.chunked(SQL_COMPOUND_SELECT_LIMIT).forEach { batch ->
        val query = batch.joinToString(" UNION ALL ") { "SELECT quote(CAST(? AS BLOB))" }
        rawQuery(query, batch.toTypedArray()).use { cursor ->
            while (cursor.moveToNext()) {
                val literal = cursor.getString(0)
                check(SQL_BLOB_LITERAL.matches(literal)) { "Unexpected SQLite blob literal" }
                literals.add("CAST($literal AS TEXT)")
            }
        }
    }
    return literals.joinToString(prefix = "(", postfix = ")")
}

private const val SQL_COMPOUND_SELECT_LIMIT = 500
private val SQL_BLOB_LITERAL = Regex("X'[0-9A-F]*'")
