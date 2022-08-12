package com.yandex.div.histogram

import com.yandex.div.histogram.reporter.HistogramReporter
import com.yandex.div.histogram.util.HistogramUtils
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executor

@RunWith(RobolectricTestRunner::class)
class DivParsingHistogramReporterImplTest {

    private val histogramReporter = mock<HistogramReporter>()
    private val underTest =
        DivParsingHistogramReporterImpl({ histogramReporter }) { Executor(Runnable::run) }

    @Test
    fun `report data parsing histograms without component`() {
        verifyReportParsing("Div.Parsing.Data", componentName = null) { json, componentName ->
            measureDataParsing(json, componentName) {}
        }
    }

    @Test
    fun `report data parsing histograms with component`() {
        verifyReportParsing(
            "Div.Parsing.Data",
            componentName = TEST_COMPONENT
        ) { json, componentName ->
            measureDataParsing(json, componentName) {}
        }
    }

    @Test
    fun `report templates parsing without component`() {
        verifyReportParsing("Div.Parsing.Templates", componentName = null) { json, componentName ->
            measureTemplatesParsing(json, componentName) {}
        }
    }

    @Test
    fun `report templates parsing with component`() {
        verifyReportParsing(
            "Div.Parsing.Templates",
            componentName = TEST_COMPONENT
        ) { json, componentName ->
            measureTemplatesParsing(json, componentName) {}
        }
    }

    private fun verifyReportParsing(
        histogramName: String,
        componentName: String? = null,
        block: DivParsingHistogramReporter.(JSONObject, String?) -> Unit
    ) {
        val data = JSONObject(TEST_DIV)

        underTest.block(data, componentName)

        verifyReportDuration(histogramName, componentName)
        verifyReportSize(
            histogramName,
            HistogramUtils.calculateUtf8JsonByteSize(data),
            componentName
        )
    }

    private fun verifyReportDuration(histogramName: String, componentName: String?) {
        verify(histogramReporter).reportDuration(
            eq(histogramName),
            any(),
            eq(componentName),
            eq(null),
            eq(HistogramFilter.ON)
        )
    }

    private fun verifyReportSize(
        histogramName: String,
        size: Int,
        componentName: String?,
    ) {
        verify(histogramReporter).reportSize(
            eq(histogramName),
            eq(size),
            eq(componentName),
            eq(HistogramFilter.ON)
        )
    }

    private companion object {
        private const val TEST_COMPONENT = "SomeComponent"

        @Language("json")
        private val TEST_DIV = """{
                "log_id": "foo", 
                "states": [
                    {
                        "state_id": 0,
                        "div": {
                            "type": "text",
                            "text": "foo"
                        }
                    }
                ]
            }""".trimIndent()
    }
}
