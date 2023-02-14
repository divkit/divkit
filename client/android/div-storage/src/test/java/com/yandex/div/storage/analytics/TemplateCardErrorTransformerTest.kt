package com.yandex.div.storage.analytics

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.DivDataRepository.Payload
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.div.storage.templates.TemplatesContainer
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

private const val MISSING_TEMPLATE_TYPE = "some_missing_template"
private val RAW_DIVDATA_WITH_MISSING_TEMPLATE = """
    {
        "type": "div2",
        "log_id": "snapshot_test_card",
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "container",
                    "items": [
                        {
                            "type": "separator"
                        },
                        {
                            "type": "$MISSING_TEMPLATE_TYPE",
                            "text": "someText"
                        }
                    ]
                }
            }
        ]
    }
""".trimIndent()

private val CORRECT_RAW_DIVDATA = """
    {
        "type": "div2",
        "log_id": "snapshot_test_card",
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "text",
                    "text": "test"
                }
            }
        ]
    }
""".trimIndent()

@RunWith(RobolectricTestRunner::class)
class TemplateCardErrorTransformerTest {

    private val templateContainer: TemplatesContainer = mock() {
        on { explainMissingTemplate(anyOrNull(), any(), any()) } doReturn
                ErrorExplanation("", "")
    }

    private val rawDivDataWithMissingTemplate = JSONObject(RAW_DIVDATA_WITH_MISSING_TEMPLATE)
    private val correctRawDivData = JSONObject(CORRECT_RAW_DIVDATA)

    @Test
    fun `consumer is used when parsing card with missing template`() {
        // ACT
        val mockCardErrorConsumer: TemplateCardErrorTransformer = mock()
        val payload = createPayload(rawDivDataWithMissingTemplate)

        underTest(mockCardErrorConsumer).repository.put(payload)

        // ASSERT
        verify(mockCardErrorConsumer, times(1))
                .tryTransformAndLog(cardError = argThat {
                    TemplateCardErrorTransformer.extractMissingTemplateException(this) != null
                })
    }

    @Test
    fun `consumer is not used when parsing correct divCard`() {
        // ACT
        val mockCardErrorConsumer: TemplateCardErrorTransformer = mock()
        val payload = createPayload(correctRawDivData)
        underTest(mockCardErrorConsumer).repository.put(payload)

        // ASSERT
        verify(mockCardErrorConsumer, times(0))
                .tryTransformAndLog(any())
    }

    @Test
    fun `validate missing template name`() {
        // SET
        val mockCardErrorConsumer = TemplateCardErrorTransformer(templateContainer, mock())
        val payload = createPayload(rawDivDataWithMissingTemplate)

        // ACT
        underTest(mockCardErrorConsumer).repository.put(payload)

        // ASSERT
        verify(templateContainer, times(1))
                .explainMissingTemplate(anyOrNull(), any(), argThat {
                    this == MISSING_TEMPLATE_TYPE
                })
    }

    @Test
    fun `validate missing template name(after restore)`() {
        // SET
        val mockCardErrorConsumer = TemplateCardErrorTransformer(templateContainer, mock())
        val payload = createPayload(rawDivDataWithMissingTemplate)
        underTest(mockCardErrorConsumer).repository.put(payload)

        // ACT
        underTest(mockCardErrorConsumer).repository.getAll()

        // ASSERT
        verify(templateContainer, times(1/*at put*/ + 1 /*at restore*/))
                .explainMissingTemplate(anyOrNull(), any(), argThat {
                    this == MISSING_TEMPLATE_TYPE
                })
    }

    private fun createPayload(divData: JSONObject): Payload {
        return Payload(
                divs = listOf(RawDataAndMetadata(
                        id = "id#0",
                        divData = divData,
                )),
                templates = emptyMap(),
        )
    }

    private fun underTest(cardErrorConsumer: TemplateCardErrorTransformer? = null) =
            DivStorageComponent.createInternal(
                    context = ApplicationProvider.getApplicationContext(),
                    cardErrorTransformer = { cardErrorConsumer }
            )
}
