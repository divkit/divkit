package com.yandex.div.storage.database

import com.yandex.div.core.annotations.InternalApi

/**
 * Optional transaction operations for read snapshots and rollback observation.
 * The observer runs on the transaction thread and is released when that transaction ends.
 */
@InternalApi
public interface TransactionOutcomeDatabase : DatabaseOpenHelper.Database {
    public val supportsReadTransactions: Boolean

    public fun beginTransaction(onRollback: () -> Unit)

    /**
     * Starts a read transaction unless this thread already owns a transaction.
     * Returns true only when the caller must finish the newly started transaction.
     */
    public fun beginReadTransaction(): Boolean
}
