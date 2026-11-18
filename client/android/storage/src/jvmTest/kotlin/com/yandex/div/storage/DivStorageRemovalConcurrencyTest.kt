package com.yandex.div.storage

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.internal.rawjson.RawJson
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class DivStorageRemovalConcurrencyTest {
    private val path = temporaryDatabasePath()
    private val executor = Executors.newFixedThreadPool(2)
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(BundledSQLiteDriver(), path, version, create, upgrade)
    }
    private val storage = DivStorage(StorageCodec.BYTES, provider)

    @AfterTest
    fun tearDown() {
        executor.shutdownNow()
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS))
        removeDatabase(path)
    }

    @Test
    fun `raw removal does not delete a concurrent replacement outside its predicate`() {
        checkConcurrentReplacement(
            write = { target, value ->
                target.saveRawJsons(listOf(RawJson("id", byteArrayOf(value))),
                    ActionOnError.ABORT_TRANSACTION)
            },
            remove = { target -> target.removeRawJsons { it.data.contentEquals(byteArrayOf(1)) } },
            read = { it.readRawJsons(setOf("id")).restoredData.single().data },
        )
    }

    @Test
    fun `card removal does not delete a concurrent replacement outside its predicate`() {
        checkConcurrentReplacement(
            write = { target, value ->
                target.saveData("group", listOf(RawDataAndMetadata("id", byteArrayOf(value))),
                    emptyList(), ActionOnError.ABORT_TRANSACTION)
            },
            remove = { target -> target.remove { it.divData.contentEquals(byteArrayOf(1)) } },
            read = { it.loadData(listOf("id"), emptyList()).restoredData.single().divData },
        )
    }

    private fun checkConcurrentReplacement(
        write: (DivStorage<ByteArray>, Byte) -> ExecutionResult,
        remove: (DivStorage<ByteArray>) -> DivStorage.RemoveResult,
        read: (DivStorage<ByteArray>) -> ByteArray,
    ) {
        assertTrue(write(storage, 1).isSuccessful)
        val connectionClosed = CountDownLatch(1)
        val updated = CountDownLatch(1)
        val pausingProvider = DatabaseOpenHelperProvider { name, version, create, upgrade ->
            val helper = provider.provide(name, version, create, upgrade)
            object : DatabaseOpenHelper by helper {
                override val writableDatabase: Database
                    get() {
                        val db = helper.writableDatabase
                        return object : Database by db {
                            override fun close() {
                                db.close()
                                connectionClosed.countDown()
                                check(updated.await(10, TimeUnit.SECONDS))
                            }
                        }
                    }
            }
        }
        val remover = DivStorage(StorageCodec.BYTES, pausingProvider)
        val removed = executor.submit<DivStorage.RemoveResult> { remove(remover) }
        try {
            assertTrue(connectionClosed.await(10, TimeUnit.SECONDS))
            val update = executor.submit<ExecutionResult> { write(storage, 2) }
            assertTrue(update.get(10, TimeUnit.SECONDS).isSuccessful)
        } finally {
            updated.countDown()
        }
        assertTrue(removed.get(10, TimeUnit.SECONDS).errors.isEmpty())
        assertContentEquals(byteArrayOf(2), read(storage))
    }
}
