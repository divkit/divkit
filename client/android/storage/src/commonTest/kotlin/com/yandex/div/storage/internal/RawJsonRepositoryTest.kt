package com.yandex.div.storage.internal

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div.storage.StorageCodec
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.removeDatabase
import com.yandex.div.storage.temporaryDatabasePath
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val ID_0 = "id0"
private val listWithOneId = listOf(ID_0)

/**
 * Integration test for [RawJsonRepository] implementation.
 */
class RawJsonRepositoryTest {
    private val rawJsonData = """{"value":"snapshot_test_raw_value","type":"string","lifetime":1000}"""
    private val path = temporaryDatabasePath()
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(BundledSQLiteDriver(), path, version, create, upgrade)
    }
    private val codec = object : StorageCodec<String> {
        override fun encode(value: String): ByteArray = value.encodeToByteArray()
        override fun decode(bytes: ByteArray): String = bytes.decodeToString()
    }
    private val storage = DivStorage(codec, provider)
    private val underTest = RawJsonRepository(storage)

    @AfterTest
    fun tearDown() = removeDatabase(path)

    private fun createSUT() = RawJsonRepository(DivStorage(codec, provider))

    @Test
    fun `what is saved can be restored from repository`() {
        underTest.put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        )
        val deserialized = createSUT().get(listWithOneId).resultData.first()
        assertEquals(ID_0, deserialized.id)
        assertEquals(rawJsonData, deserialized.data)
    }

    @Test
    fun `all data retrieved from repository when nothing is specified at getter`() {
        underTest.put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        )
        val deserialized = underTest.getAll().resultData.first()
        assertEquals(ID_0, deserialized.id)
        assertEquals(rawJsonData, deserialized.data)
    }

    @Test
    fun `what is removed cant be restored from repository`() {
        underTest.put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        )
        underTest.remove { it.id == ID_0 }
        val deserialized = underTest.get(listWithOneId).resultData
        assertEquals(0, deserialized.size)
    }

    @Test
    fun `repository saves data to persistent storage`() {
        createSUT().put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        )
        val deserialized = createSUT().get(listWithOneId).resultData.first()
        assertEquals(ID_0, deserialized.id)
        assertEquals(rawJsonData, deserialized.data)
    }

    @Test
    fun `repository saves data to persistent storage without errors`() {
        val errors = createSUT().put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        ).errors
        assertEquals(
            0, errors.size,
            "Got unexpected errors: ${errors.map { it.stackTraceToString() }}"
        )
    }

    @Test
    fun `repository loads data from persistent storage without errors`() {
        createSUT().put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        )
        val errors = createSUT().get(listWithOneId).errors
        assertEquals(
            0, errors.size,
            "Got unexpected errors: ${errors.map { it.stackTraceToString() }}"
        )
    }

    @Test
    fun `repository removes data from persistent storage without errors`() {
        createSUT().put(
            RawJsonRepository.Payload(
                jsons = listOf(
                    RawJson(ID_0, rawJsonData)
                )
            )
        )

        createSUT().remove { it.id == ID_0 }

        val deserialized = createSUT().get(listWithOneId).resultData
        assertEquals(0, deserialized.size)
    }

    @Test
    fun `put returns original raw json list`() {
        val jsons = listOf(RawJson(ID_0, rawJsonData))

        val result = underTest.put(RawJsonRepository.Payload(jsons))

        assertSame(jsons, result.resultData)
    }

    @Test
    fun `get preserves cached object identity and repeated ids`() {
        val rawJson = RawJson(ID_0, rawJsonData)
        underTest.put(RawJsonRepository.Payload(listOf(rawJson)))

        val result = underTest.get(listOf(ID_0, ID_0))

        assertEquals(listOf(rawJson, rawJson), result.resultData)
        assertSame(rawJson, result.resultData.first())
    }

    @Test
    fun `get appends cached values after storage values`() {
        val cached = RawJson("cached", "cached data")
        underTest.put(RawJsonRepository.Payload(listOf(cached)))
        storage.saveRawJsons(listOf(RawJson("stored", "stored data")), ActionOnError.ABORT_TRANSACTION)

        val result = underTest.get(listOf("cached", "stored", "cached", "stored"))

        assertEquals(listOf("stored", "cached", "cached"), result.resultData.map { it.id })
    }

    @Test
    fun `getAll returns cached object when storage read has no ids`() {
        val cached = RawJson(ID_0, rawJsonData)
        underTest.put(RawJsonRepository.Payload(listOf(cached)))

        val result = underTest.getAll()

        assertEquals(listOf(ID_0), result.resultData.map { it.id })
        assertSame(cached, result.resultData.last())
    }

    @Test
    fun `failed put retains payload in cache`() {
        val error = DivStorageErrorException("save", IllegalStateException("failure"), ID_0)
        val failingStorage = object : DivStorage<String> by storage {
            override fun saveRawJsons(rawJsons: List<RawJson<String>>, actionOnError: ActionOnError) =
                ExecutionResult(listOf(error))
        }
        val repository = RawJsonRepository(failingStorage)
        val rawJson = RawJson(ID_0, rawJsonData)
        repository.put(RawJsonRepository.Payload(listOf(rawJson)))

        val result = repository.get(listWithOneId)

        assertSame(rawJson, result.resultData.single())
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `repository failure retains storage exception and json id`() {
        val error = DivStorageErrorException("read", IllegalStateException("failure"), ID_0)
        val failingStorage = object : DivStorage<String> by storage {
            override fun readRawJsons(rawJsonIds: Set<String>) =
                DivStorage.LoadDataResult<RawJson<String>>(emptyList(), listOf(error))
        }
        val repository = RawJsonRepository(failingStorage)

        val result = repository.get(listWithOneId)

        assertEquals(ID_0, result.errors.single().jsonId)
        assertEquals(error.message, result.errors.single().message)
        assertSame(error, result.errors.single().cause)
    }
}
