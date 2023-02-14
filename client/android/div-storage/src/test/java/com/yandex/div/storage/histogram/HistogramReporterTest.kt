package com.yandex.div.storage.histogram

import androidx.test.core.app.ApplicationProvider
import com.yandex.div.histogram.reporter.HistogramReporterDelegate
import com.yandex.div.storage.DivDataRepository.Payload
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawDataAndMetadata
import org.intellij.lang.annotations.Language
import org.json.JSONObject

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

private const val ID_0 = "id#0"
private const val TEMPLATE_0 = "template_id#0"
private const val TEMPLATE_SOURCE_TYPE_0 = "template_source_type#0"

@Language("json")
private val testRawDivData = """
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
class HistogramReporterTest {

    private val rawDivData = JSONObject(testRawDivData)
    private val histogramReporter: HistogramReporterDelegate = mock()
    private val histogramNameProvider: HistogramNameProvider = object : HistogramNameProvider {
        override val componentName: String = "ComponentName#1"
        override val divLoadTemplatesReportName: String = "Div.LoadTemplates"
        override val divDataLoadReportName: String = "Div.DataLoad"
        override val divParsingHistogramName: String = "Div.Parsing"
        override fun getHistogramNameFromCardId(cardId: String) = "Histogram Name"
    }

    private val template0 = JSONObject().put("template", "0")

    private val underTest = createSUT()

    @Test
    fun `load templates histogram is being recorded`() {
        // ACT
        whenParsed()

        // ASSERT
        verify(histogramReporter, atLeastOnce())
                .reportDuration(any(), any(), anyOrNull())
    }

    @Test
    fun `load templates histogram has expected name`() {
        // ACT
        whenParsed()

        // ASSERT
        verify(histogramReporter, atLeastOnce())
                .reportDuration(
                        histogramName = argThat {
                            contains(histogramNameProvider.divDataLoadReportName)
                        },
                        duration = any(),
                        forceCallType = anyOrNull(),
                )
    }

    @Test
    fun `data load histogram is being recorded`() {
        // ACT
        whenParsed()

        // ASSERT
        verify(histogramReporter, atLeastOnce())
                .reportDuration(any(), any(), anyOrNull())
    }

    @Test
    fun `data load histogram has expected name`() {
        // ACT
        whenParsed()

        // ASSERT
        verify(histogramReporter, atLeastOnce())
                .reportDuration(
                        histogramName = argThat {
                            contains(histogramNameProvider.divDataLoadReportName)
                        },
                        duration = any(),
                        forceCallType = anyOrNull(),
                )
    }

    @Test
    fun `load templates histogram is being recorded when sourceType is null`() {
        // ACT
        whenParsedWithNullSourceType()

        // ASSERT
        verify(histogramReporter, atLeastOnce())
                .reportDuration(any(), any(), anyOrNull())
    }

    @Test
    fun `data load histogram is being recorded when sourceType is null`() {
        // ACT
        whenParsedWithNullSourceType()

        // ASSERT
        verify(histogramReporter, atLeastOnce())
                .reportDuration(any(), any(), anyOrNull())
    }

    private fun createSUT() = DivStorageComponent.create(
            ApplicationProvider.getApplicationContext(),
            histogramReporter = histogramReporter,
            histogramNameProvider = histogramNameProvider,
    ).repository

    private fun createPayload() = Payload(
            divs = listOf(RawDataAndMetadata(ID_0, rawDivData)),
            templates = mapOf(TEMPLATE_0 to template0),
            sourceType = TEMPLATE_SOURCE_TYPE_0
    )

    private fun createPayload_nullSourceType() = Payload(
            divs = listOf(RawDataAndMetadata(ID_0, rawDivData)),
            templates = mapOf(TEMPLATE_0 to template0),
            sourceType = null
    )

    private fun whenParsed() {
        underTest.put(createPayload())
    }

    private fun whenParsedWithNullSourceType() {
        underTest.put(createPayload_nullSourceType())
    }
}
