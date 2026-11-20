package com.yandex.div.storage

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.yandex.div.core.json.JsonObject
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.database.SQLiteDatabaseOpenHelper
import com.yandex.div.storage.internal.DivStorage
import com.yandex.div.storage.internal.RawJsonRepository
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistentStoredValuesRepositoryTest {
    private val path = temporaryDatabasePath()
    private val provider = DatabaseOpenHelperProvider { _, version, create, upgrade ->
        SQLiteDatabaseOpenHelper(BundledSQLiteDriver(), path, version, create, upgrade)
    }
    private val codec = object : StorageCodec<JsonObject> {
        override fun encode(value: JsonObject): ByteArray = value.toString().encodeToByteArray()
        override fun decode(bytes: ByteArray): JsonObject = JsonObject(bytes.decodeToString())
    }
    private val repository = createRepository()

    @AfterTest
    fun tearDown() = removeDatabase(path)

    @Test
    fun `stored value is readable from a new repository`() {
        val envelope = JsonObject("""{"type":"integer","value":9007199254740993}""")
        repository.put("stored_value_counter", envelope)

        val restored = createRepository().get("stored_value_counter")

        assertEquals(envelope.asKtxJsonObject(), restored?.asKtxJsonObject())
    }

    @Test
    fun `remove deletes only the requested value from disk and cache`() {
        val envelope = JsonObject("""{"type":"string","value":"saved"}""")
        repository.put("stored_value_deleted", envelope)
        repository.put("stored_value_remaining", envelope)

        repository.remove("stored_value_deleted")

        val reopened = createRepository()
        assertNull(repository.get("stored_value_deleted"))
        assertNull(reopened.get("stored_value_deleted"))
        assertEquals(
            envelope.asKtxJsonObject(),
            reopened.get("stored_value_remaining")?.asKtxJsonObject(),
        )
    }

    private fun createRepository() = createPersistentStoredValuesRepository(
        RawJsonRepository(DivStorage(codec, provider)),
    )
}
