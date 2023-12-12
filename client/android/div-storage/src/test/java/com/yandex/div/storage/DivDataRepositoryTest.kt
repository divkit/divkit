package com.yandex.div.storage

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.storage.DivDataRepository.Payload
import com.yandex.div2.Div
import com.yandex.div2.DivData
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val ID_0 = "id0"
private val listWithOneId = listOf(ID_0)

/**
 * Integration test for [DivDataRepository] implementation.
 */
@RunWith(RobolectricTestRunner::class)
class DivDataRepositoryTest {
    @Language("json")
    private val templateData = JSONObject("""
        {
            "boxed_text": {
                "type": "colored_container",
                "items": [
                    {
                        "type": "text",
                        "text": "some text"
                    }
                ]
            },
            "boxed_text_clone": {
                "type": "colored_container",
                "items": [
                    {
                        "type": "text",
                        "text": "some text"
                    }
                ]
            },
            "colored_container": {
                "type": "container",
                "background": [
                    {
                        "color": "#FF0000",
                        "type": "solid"
                    }
                ]
            }
        }
    """.trimIndent())

    private val rawDivData = JSONObject("""
        {
            "log_id": "snapshot_test_card",
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "boxed_text"
                    }
                }
            ]
        }
    """.trimIndent())

    private val templates: Map<String, JSONObject> = run {
        val results = mutableMapOf<String, JSONObject>()
        templateData.forEach { templateId, json: JSONObject ->
            results[templateId] = json
        }
        results
    }

    private val brokenDivData = JSONObject("""
        {
            "broken": "data"
        }
    """.trimIndent())

    private val underTest = createSUT()
    private val loggedErrors = ArrayList<Exception>()

    private fun createInternalStorageComponent() = DivStorageComponent.create(
            ApplicationProvider.getApplicationContext(),
            errorLogger = { loggedErrors.add(it) }
    )
    private fun createSUT() = createInternalStorageComponent().repository

    @Test
    fun `what is saved can be restored from repository`() {
        underTest.put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))
        val deserialized = createSUT().get(listWithOneId).resultData.first()
        Assert.assertEquals(ID_0, deserialized.id)
        Assert.assertEquals("snapshot_test_card", deserialized.divData.logId)
    }

    @Test
    fun `templates correctly restored from repository`() {
        underTest.put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))
        val deserialized = createSUT().get(listWithOneId).resultData.first()
        Assert.assertEquals(ID_0, deserialized.id)
        verifyCardStructure(deserialized.divData)
    }

    @Test
    fun `all data retrieved from repository when nothing is specified at getter`() {
        underTest.put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))
        val deserialized = underTest.getAll().resultData.first()
        Assert.assertEquals(ID_0, deserialized.id)
        Assert.assertEquals("snapshot_test_card", deserialized.divData.logId)
    }

    @Test
    fun `getAll() returns correct number of cards`() {
        underTest.remove { true }
        underTest.put(Payload(divs = listOf(
            RawDataAndMetadata(ID_0, rawDivData)), templates))
        val cards = underTest.getAll().resultData
        Assert.assertEquals(1, cards.size)
    }

    @Test
    fun `repository saves data to persistent storage`() {
        createSUT().put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))
        val deserialized = createSUT().get(listWithOneId).resultData.first()
        Assert.assertEquals(ID_0, deserialized.id)
        Assert.assertEquals("snapshot_test_card", deserialized.divData.logId)
    }

    @Test
    fun `repository saves data to persistent storage without errors`() {
        val errors = createSUT().put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates)).errors
        Assert.assertEquals("Got unexpected errors: ${errors.map { it.stackTraceToString() }}",
                0, errors.size)
    }

    @Test
    fun `repository loads data from persistent storage without errors`() {
        createSUT().put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))
        val errors = createSUT().get(listWithOneId).errors
        Assert.assertEquals("Got unexpected errors: ${errors.map { it.stackTraceToString() }}",
                0, errors.size)
    }
    @Test
    fun `corrupted data not passed to persistent storage`() {
        underTest.put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))

        underTest.put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, brokenDivData)), templates))
        val deserialized = createSUT().get(listWithOneId).resultData.first()

        Assert.assertEquals(ID_0, deserialized.id)
        Assert.assertEquals("snapshot_test_card", deserialized.divData.logId)
    }

    @Test
    fun `missing template error explanation logged`() {
        loggedErrors.clear()
        val storageComponent = createInternalStorageComponent()
        val underTest = storageComponent.repository
        underTest.put(Payload(divs = listOf(
                RawDataAndMetadata(ID_0, rawDivData)), templates))
        (storageComponent as InternalStorageComponent).storage.removeAllTemplates()
        createSUT().get(listWithOneId)
        Assert.assertEquals(2, loggedErrors.size)
        Assert.assertEquals(
            "missing template = boxed_text, reason = cached, but not loaded into memory",
            loggedErrors[0].message
        )
    }

    private fun verifyCardStructure(divData: DivData) {
        val divContainer = divData.states.first().div as Div.Container
        val divText = divContainer.value.items!!.first() as Div.Text
        Assert.assertEquals("some text",
                divText.value.text.evaluate(ExpressionResolver.EMPTY))
    }
}
