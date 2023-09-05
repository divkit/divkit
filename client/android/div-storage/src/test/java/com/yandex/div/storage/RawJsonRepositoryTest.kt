package com.yandex.div.storage

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.rawjson.RawJson
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val ID_0 = "id0"
private const val KEY_VALUE = "value"
private const val KEY_TYPE = "type"
private const val KEY_LIFETIME = "lifetime"
private val listWithOneId = listOf(ID_0)

/**
 * Integration test for [RawJsonRepository] implementation.
 */
@RunWith(RobolectricTestRunner::class)
class RawJsonRepositoryTest {
    @Language("json")
    private val rawJsonData = JSONObject().apply {
        put(KEY_VALUE, "snapshot_test_raw_value")
        put(KEY_TYPE, "string")
        put(KEY_LIFETIME, 1000)
    }

    private val underTest = createSUT()
    private val loggedErrors = ArrayList<Exception>()

    private fun createInternalStorageComponent() = DivStorageComponent.create(
        ApplicationProvider.getApplicationContext(),
        errorLogger = { loggedErrors.add(it) }
    )
    private fun createSUT() = createInternalStorageComponent().rawJsonRepository

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
        assertJsonContentEquals(rawJsonData, deserialized.data)
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
        assertJsonContentEquals(rawJsonData, deserialized.data)
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
        assertJsonContentEquals(rawJsonData, deserialized.data)
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
            "Got unexpected errors: ${errors.map { it.stackTraceToString() }}",
            0, errors.size
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
            "Got unexpected errors: ${errors.map { it.stackTraceToString() }}",
            0, errors.size
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

    private fun assertJsonContentEquals(expected: JSONObject, actual: JSONObject) {
        assertEquals(expected.getString(KEY_VALUE), actual.getString(KEY_VALUE))
        assertEquals(expected.getString(KEY_TYPE), actual.getString(KEY_TYPE))
        assertEquals(expected.getInt(KEY_LIFETIME), actual.getInt(KEY_LIFETIME))
    }

}
