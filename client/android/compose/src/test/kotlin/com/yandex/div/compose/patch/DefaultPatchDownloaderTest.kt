package com.yandex.div.compose.patch

import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestReporter
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.change
import com.yandex.div.test.data.text
import com.yandex.div2.DivPatch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.json.JSONException
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DefaultPatchDownloaderTest {
    private val url = "https://example.test/patch?id=1&version=2".toUri()

    private val responseBody = DivNetworkResponseBody.fromBytes(
        """{"patch":{"changes":[{"id":"target"}]}}""".toByteArray()
    )

    private val response = mock<DivNetworkResponse> {
        on { isSuccessful } doReturn true
        on { body } doReturn responseBody
    }

    private val client = mock<DivNetworkClient> {
        on { execute(any()) } doReturn response
    }

    private val downloader = DefaultPatchDownloader(
        networkClient = client,
        reporter = TestReporter(),
    )

    @Test
    fun `downloadPatch() sends GET to the requested url`() = runTest {
        downloader.downloadPatch(url)

        val requestCaptor = argumentCaptor<DivNetworkRequest>()
        verify(client).execute(requestCaptor.capture())

        val request = requestCaptor.firstValue
        assertEquals(url.toString(), request.url)
        assertEquals("GET", request.method)
    }

    @Test
    fun `downloadPatch() closes response after successful download`() = runTest {
        downloader.downloadPatch(url)

        verify(response).close()
    }

    @Test
    fun `downloadPatch() successfully gets parsed patch without templates`() = runTest {
        val patch = downloader.downloadPatch(url)

        val expectedPatch = DivPatch(changes = listOf(change(id = "target")))
        assertTrue(expectedPatch.equals(patch, expressionResolver, expressionResolver))
    }

    @Test
    fun `downloadPatch() successfully gets parsed patch with templates`() = runTest {
        mockResponse(
            """
            {
              "templates":{"title":{"type":"text","text":"Updated"}},
              "patch":{"changes":[{"id":"target","items":[{"type":"title"}]}]}
            }
            """
        )

        val patch = downloader.downloadPatch(url)

        val expectedPatch = DivPatch(
            changes = listOf(change(id = "target", items = listOf(text(text = "Updated"))))
        )
        assertTrue(expectedPatch.equals(patch, ExpressionResolver.EMPTY, ExpressionResolver.EMPTY))
    }

    @Test
    fun `downloadPatch() fails to process failed HTTP request`() = runTest {
        whenever(response.isSuccessful) doReturn false
        whenever(response.code) doReturn 404

        val error = assertFailsWith<IOException> {
            downloader.downloadPatch(url)
        }

        verify(response).close()

        assertEquals("Patch request failed: HTTP 404", error.message)
    }

    @Test
    fun `downloadPatch() fails to parse empty response`() = runTest {
        whenever(response.body) doReturn null

        val error = assertFailsWith<IOException> {
            downloader.downloadPatch(url)
        }

        verify(response).close()

        assertEquals("Empty patch response", error.message)
    }

    @Test
    fun `downloadPatch() fails to parse invalid json`() = runTest {
        mockResponse("invalid")

        assertFailsWith<JSONException> {
            downloader.downloadPatch(url)
        }

        verify(response).close()
    }

    @Test
    fun `cancelling job cancels network request`() = runTest {
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val downloader = DefaultPatchDownloader(
            networkClient = {
                started.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    cancelled.complete(Unit)
                }
            },
            reporter = TestReporter(),
        )

        val job = launch { downloader.downloadPatch(url) }
        started.await()

        job.cancelAndJoin()

        assertTrue(cancelled.isCompleted)
    }

    private fun mockResponse(json: String) {
        whenever(response.body) doReturn DivNetworkResponseBody.fromBytes(json.toByteArray())
    }
}

private val expressionResolver = ExpressionResolver.EMPTY
