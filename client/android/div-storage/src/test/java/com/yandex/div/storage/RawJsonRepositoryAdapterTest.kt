package com.yandex.div.storage

import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.internal.DivStorage as CommonDivStorage
import com.yandex.div.storage.internal.rawjson.RawJson as CommonRawJson
import com.yandex.div.storage.rawjson.RawJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.json.JSONObject
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RawJsonRepositoryAdapterTest {
    private val data = JSONObject().put("value", "stored")
    private val rawJson = RawJson("id", data)
    private val storage = mock<CommonDivStorage<JSONObject>> {
        on { saveRawJsons(any(), any()) } doReturn ExecutionResult()
        on { readRawJsons(any()) } doReturn CommonDivStorage.LoadDataResult(listOf(CommonRawJson("id", data)))
    }
    private val underTest = RawJsonRepositoryImpl(storage)

    @Test
    fun `put returns original list and JSON objects`() {
        val jsons = listOf(rawJson)

        val result = underTest.put(RawJsonRepository.Payload(jsons))

        assertSame(jsons, result.resultData)
        assertSame(data, result.resultData.single().data)
    }

    @Test
    fun `cache returns original raw json after put`() {
        underTest.put(RawJsonRepository.Payload(listOf(rawJson)))

        val result = underTest.get(listOf("id"))

        assertSame(rawJson, result.resultData.single())
    }

    @Test
    fun `cache returns same Android raw json after load`() {
        val loaded = underTest.get(listOf("id")).resultData.single()

        val cached = underTest.get(listOf("id")).resultData.single()

        assertIs<RawJson.Ready>(loaded)
        assertSame(loaded, cached)
        assertSame(data, cached.data)
    }

    @Test
    fun `put passes skip element action to storage`() {
        underTest.put(RawJsonRepository.Payload(emptyList(), DivDataRepository.ActionOnError.SKIP_ELEMENT))

        verify(storage).saveRawJsons(emptyList(), ActionOnError.SKIP_ELEMENT)
    }

    @Test
    fun `put preserves lazy payload access`() {
        val lazyJson = object : RawJson {
            override val id = "lazy"
            override val data: JSONObject get() = error("Unexpected data access")
        }

        val result = underTest.put(RawJsonRepository.Payload(listOf(lazyJson)))

        assertSame(lazyJson, result.resultData.single())
    }

    @Test
    fun `remove by id does not evaluate JSON data`() {
        val lazyJson = object : CommonRawJson<JSONObject> {
            override val id = "lazy"
            override val data: JSONObject get() = error("Unexpected data access")
        }
        whenever(storage.removeRawJsons(any())) doAnswer {
            val predicate = it.getArgument<(CommonRawJson<JSONObject>) -> Boolean>(0)
            assertTrue(predicate(lazyJson))
            CommonDivStorage.RemoveResult(setOf("lazy"))
        }

        val result = underTest.remove { it.id == "lazy" }

        assertEquals(setOf("lazy"), result.ids)
    }

    @Test
    fun `storage failure preserves Android exception chain and json id`() {
        val cause = IllegalStateException("failed")
        val error = DivStorageErrorException("read", cause, "id")
        whenever(storage.readRawJsons(setOf("id"))) doReturn
            CommonDivStorage.LoadDataResult(emptyList(), listOf(error))

        val result = underTest.get(listOf("id"))

        val repositoryError = result.errors.single()
        assertEquals("id", repositoryError.jsonId)
        assertEquals(error.message, repositoryError.message)
        val storageError = assertIs<DivStorageErrorException>(repositoryError.cause)
        assertEquals("id", storageError.cardId)
        assertSame(cause, storageError.cause)
    }
}
