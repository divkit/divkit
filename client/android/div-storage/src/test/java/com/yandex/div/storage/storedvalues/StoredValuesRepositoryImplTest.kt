package com.yandex.div.storage.storedvalues

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.json.asJsonObject
import com.yandex.div.internal.storedvalues.StoredValueScope
import com.yandex.div.internal.storedvalues.StoredValuesStorage
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.rawjson.RawJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import org.json.JSONObject
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoredValuesRepositoryImplTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val rawRepository = DivStorageComponent.create(context).rawJsonRepository
    private val repository = StoredValuesRepositoryImpl(rawRepository)
    private val storage = StoredValuesStorage(repository)

    @Test
    fun `repeated dictionary reads reuse the cached JSON`() {
        val value = JSONObject("""{"nested":{"value":1}}""")
        rawRepository.put(RawJsonRepository.Payload(listOf(RawJson(
            "stored_value_name", JSONObject().put("type", "dict").put("value", value)
        ))))

        repeat(2) {
            assertSame(value, storage.getValue("name", StoredValueScope.Global, "card")?.getValue())
        }
    }

    @Test
    fun `envelope survives recreating the Android repository`() {
        repository.put(
            "stored_value_name",
            JsonObject("""{"type":"string","value":"saved","timestamp":42,"lifetime":3600}"""),
        )

        val restored = requireNotNull(createRepository().get("stored_value_name"))

        assertEquals("string", restored.getString("type"))
        assertEquals("saved", restored.getString("value"))
        assertEquals(42L, restored.getLong("timestamp"))
        assertEquals(3600L, restored.getLong("lifetime"))
    }

    @Test
    fun `legacy envelope is readable through the portable contract`() {
        DivStorageComponent.create(context).rawJsonRepository.put(
            RawJsonRepository.Payload(
                listOf(RawJson("stored_value_name", JSONObject(
                    """{"type":"integer","value":9007199254740993,"expiration_time":1000}"""
                )))
            )
        )

        val restored = requireNotNull(repository.get("stored_value_name"))

        assertEquals("integer", restored.getString("type"))
        assertEquals(9007199254740993L, restored.getLong("value"))
        assertEquals(1000L, restored.getLong("expiration_time"))
    }

    @Test
    fun `common JSON persists nested values and precise integers`() {
        repository.put("stored_value_name", JsonObject(
            """{"type":"dict","value":{"items":[{"number":9007199254740993}]}}"""
        ))

        val restored = requireNotNull(createRepository().get("stored_value_name"))

        assertEquals(
            9007199254740993L,
            restored.getObject("value").getArray("items").getObject(0).getLong("number")
        )
    }

    @Test
    fun `raw repository reuses the Android record written by stored values`() {
        val ids = listOf("stored_value_name")
        val envelope = JSONObject("""{"type":"string","value":"saved"}""")

        repository.put(ids.single(), envelope.asJsonObject())

        val firstRead = assertIs<RawJson.Ready>(rawRepository.get(ids).resultData.single())
        assertSame(envelope, firstRead.data)
        assertSame(firstRead, rawRepository.get(ids).resultData.single())
    }

    @Test
    fun `reading an expired value removes it from the Android cache and database`() {
        val id = "stored_value_name"
        val envelope = JsonObject("""{"type":"string","value":"old","expiration_time":1000}""")
        val expiringStorage = StoredValuesStorage(repository, currentTimeMillis = { 1000L })
        repository.put(id, envelope)
        assertNotNull(createRepository().get(id))

        val expired = expiringStorage.getValue("name", StoredValueScope.Global, "card")

        assertNull(expired)
        assertNull(repository.get(id))
        assertNull(createRepository().get(id))
    }

    private fun createRepository(): StoredValuesRepositoryImpl =
        StoredValuesRepositoryImpl(DivStorageComponent.create(context).rawJsonRepository)
}
