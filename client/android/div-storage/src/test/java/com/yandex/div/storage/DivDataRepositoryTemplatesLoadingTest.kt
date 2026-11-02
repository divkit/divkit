package com.yandex.div.storage

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.histogram.DivParsingHistogramReporter
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.DivStorage.LoadDataResult
import com.yandex.div.storage.analytics.CardErrorLoggerFactory
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.templates.DivParsingHistogramProxy
import com.yandex.div.storage.templates.RawTemplateData
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div2.Div
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

private const val CARD_ID = "card"
private const val GROUP_ID = "group"
private const val TEMPLATE_HASH = "hash"

@RunWith(AndroidJUnit4::class)
class DivDataRepositoryTemplatesLoadingTest {

    @Language("json")
    private val card = JSONObject(
        """
            {
                "log_id": "card",
                "states": [
                    {
                        "state_id": 0,
                        "div": {
                            "type": "container",
                            "items": [
                                {
                                    "type": "text",
                                    "text": "fallback"
                                },
                                {
                                    "type": "title"
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()
    )

    @Language("json")
    private val template = """{ "type": "text", "text": "template" }"""

    private val divStorage = mock<DivStorage> {
        on { saveData(any(), any(), any(), any()) } doReturn ExecutionResult()
        on { loadData(any(), any()) } doReturn
            LoadDataResult(listOf(DivStorage.RestoredRawData(CARD_ID, card, groupId = GROUP_ID)))
        on { readTemplateReferences() } doReturn
            LoadDataResult(listOf(DivStorage.TemplateReference(GROUP_ID, "title", TEMPLATE_HASH)))
        on { readTemplates(any()) }.doReturn(
            LoadDataResult(emptyList(), listOf(DivStorageErrorException())),
            LoadDataResult(listOf(RawTemplateData(TEMPLATE_HASH, template.toByteArray()))),
        )
    }

    private val errorLogger = ParsingErrorLogger { }
    private val histogramProxy = DivParsingHistogramProxy { DivParsingHistogramReporter.DEFAULT }
    private val templatesContainer =
        TemplatesContainer(divStorage, errorLogger, mock(), { histogramProxy })

    private val underTest = DivDataRepositoryImpl(
        divStorage,
        templatesContainer,
        mock(),
        null,
        { histogramProxy },
        CardErrorLoggerFactory(null, templatesContainer, errorLogger),
    )

    @Test
    fun `get() returns error when templates fail to load`() {
        val result = underTest.get(listOf(CARD_ID))

        assertEquals(emptyList(), result.resultData)
        assertEquals(listOf(CARD_ID), result.errors.map { it.cardId })
    }

    @Test
    fun `get() reads templates once per group when they fail to load`() {
        val cards = List(2) { DivStorage.RestoredRawData("$it", card, groupId = GROUP_ID) }
        whenever(divStorage.loadData(any(), any())) doReturn LoadDataResult(cards)

        underTest.get(listOf("0", "1"))

        verify(divStorage, times(1)).readTemplates(any())
    }

    @Test
    fun `put() reads template references once when they fail to load`() {
        whenever(divStorage.readTemplateReferences()) doReturn
            LoadDataResult(emptyList(), listOf(DivStorageErrorException()))

        underTest.put(DivDataRepository.Payload(List(2) { RawDataAndMetadata("$it", card) }))

        verify(divStorage, times(1)).readTemplateReferences()
    }

    @Test
    fun `get() restores card with templates after templates failed to load`() {
        underTest.get(listOf(CARD_ID))

        assertEquals(2, underTest.get(listOf(CARD_ID)).itemsCount())
    }

    @Test
    fun `getAll() restores card with templates after templates failed to load`() {
        underTest.getAll()

        assertEquals(2, underTest.getAll().itemsCount())
    }

    private fun DivDataRepositoryResult.itemsCount() =
        (resultData.single().divData.states.single().div as Div.Container).value.items?.size
}
