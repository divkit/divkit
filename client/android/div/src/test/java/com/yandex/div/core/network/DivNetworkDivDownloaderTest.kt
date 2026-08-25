package com.yandex.div.core.network

import com.yandex.div.core.downloader.DivPatchDownloadCallback
import com.yandex.div.core.view2.Div2View
import com.yandex.div.histogram.DivParsingHistogramReporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class DivNetworkDivDownloaderTest {

    @Test
    fun `patch request goes through configured network client`() {
        val client = mock<DivNetworkClient>()
        runBlocking { whenever(client.execute(any())).thenReturn(FakeResponse()) }
        val downloader = DivNetworkDivDownloader(
            networkClient = client,
            dispatcher = Dispatchers.Unconfined,
        )

        downloader.downloadPatch(
            divView = mock<Div2View>(),
            downloadUrl = "https://example.com/patch",
            callback = mock<DivPatchDownloadCallback>(),
        )

        val request = argumentCaptor<DivNetworkRequest>()
        runBlocking { verify(client).execute(request.capture()) }
        assertEquals("https://example.com/patch", request.firstValue.url)
    }

    @Test
    fun `download failure passes its cause to callback`() {
        val cause = IllegalStateException("No network")
        val client = mock<DivNetworkClient>()
        runBlocking { whenever(client.execute(any())).thenThrow(cause) }
        val callback = mock<DivPatchDownloadCallback>()
        val divView = mock<Div2View>()
        val downloader = DivNetworkDivDownloader(
            networkClient = client,
            dispatcher = Dispatchers.Unconfined,
        )

        downloader.downloadPatch(
            divView = divView,
            downloadUrl = "https://example.com/patch",
            callback = callback,
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        verify(callback).onFail(cause)
        verify(divView).logError(cause)
    }

    @Test
    fun `patch template parsing is measured`() {
        val reporter = RecordingHistogramReporter()
        val client = mock<DivNetworkClient>()
        runBlocking {
            whenever(client.execute(any())).thenReturn(FakeResponse(
                """{"templates":{},"patch":{"mode":"partial","changes":[]}}"""
            ))
        }
        val downloader = DivNetworkDivDownloader(
            networkClient = client,
            dispatcher = Dispatchers.Unconfined,
            histogramReporter = reporter,
        )

        downloader.downloadPatch(
            divView = mock<Div2View>(),
            downloadUrl = "https://example.com/patch",
            callback = mock<DivPatchDownloadCallback>(),
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertTrue(reporter.templatesParsingMeasured)
    }

    private class RecordingHistogramReporter : DivParsingHistogramReporter {
        var templatesParsingMeasured = false

        override fun <D> measureDataParsing(
            json: JSONObject,
            componentName: String?,
            parse: () -> D,
        ): D = parse()

        override fun <T> measureTemplatesParsing(
            json: JSONObject,
            componentName: String?,
            parse: () -> T,
        ): T {
            templatesParsingMeasured = true
            return parse()
        }

        override fun measureJsonParsing(
            componentName: String?,
            parse: () -> JSONObject,
        ): JSONObject = parse()
    }

    private class FakeResponse(content: String = "{}") : DivNetworkResponse {
        override val url = "https://example.com/patch"
        override val code = 200
        override val contentType: String? = null
        override val body: DivNetworkResponseBody = DivNetworkResponseBody.fromBytes(content.toByteArray())
        override fun headers(name: String): List<String> = emptyList()
        override fun close() = Unit
    }
}
