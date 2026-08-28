package com.yandex.div.core.network

import android.net.Uri
import com.yandex.div.core.DivRequestExecutor
import com.yandex.div.json.LoadingErrorLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivNetworkAdaptersTest {

    @Test
    fun `submit GET sends headers without body`() {
        val requestCaptor = argumentCaptor<DivNetworkRequest>()
        val client = successfulClient(requestCaptor)
        val executor = DivNetworkDivRequestExecutor(client, CoroutineScope(Dispatchers.Unconfined))

        executor.execute(
            DivRequestExecutor.Request(
                Uri.parse("https://example.com/submit"),
                "GET",
                listOf(DivRequestExecutor.Header("X-Test", "value")),
                "ignored",
            ),
            null,
        )

        assertEquals("GET", requestCaptor.firstValue.method)
        assertEquals("value", requestCaptor.firstValue.headers.single().value)
        assertNull(requestCaptor.firstValue.body)
    }

    @Test
    fun `submit POST sends body`() {
        val requestCaptor = argumentCaptor<DivNetworkRequest>()
        val executor = DivNetworkDivRequestExecutor(
            successfulClient(requestCaptor),
            CoroutineScope(Dispatchers.Unconfined),
        )

        executor.execute(
            DivRequestExecutor.Request(Uri.parse("https://example.com"), "POST", null, "payload"),
            null,
        )

        assertArrayEquals("payload".toByteArray(), requestCaptor.firstValue.body)
    }

    @Test
    fun `submit failure is logged with request context`() {
        val cause = IllegalStateException("Network unavailable")
        val client = mock<DivNetworkClient>()
        runBlocking { whenever(client.execute(any())).thenThrow(cause) }
        val logger = mock<LoadingErrorLogger>()
        val executor = DivNetworkDivRequestExecutor(
            client,
            CoroutineScope(Dispatchers.Unconfined),
            logger,
        )

        executor.execute(
            DivRequestExecutor.Request(Uri.parse("https://example.com"), "POST", null, "body"),
            null,
        )

        val error = argumentCaptor<Exception>()
        verify(logger).logError(error.capture())
        assertEquals(cause, error.firstValue.cause)
        assertEquals(
            "Error while executing request [POST https://example.com]",
            error.firstValue.message,
        )
    }

    private fun successfulClient(requestCaptor: org.mockito.kotlin.KArgumentCaptor<DivNetworkRequest>): DivNetworkClient {
        val response = mock<DivNetworkResponse> {
            on { code } doReturn 200
            on { isSuccessful } doReturn true
        }
        return mock<DivNetworkClient>().also { client ->
            runBlocking { whenever(client.execute(requestCaptor.capture())).thenReturn(response) }
        }
    }
}
