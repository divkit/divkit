package com.yandex.android.beacon

import android.net.Uri
import com.yandex.android.net.CookieStorage
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
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
class DivNetworkSendBeaconRequestExecutorTest {

    @Test
    fun `sends payload and cookies and stores response cookies`() {
        val requestCaptor = argumentCaptor<DivNetworkRequest>()
        val response = mock<DivNetworkResponse> {
            on { code } doReturn 200
            on { url } doReturn "https://cdn.example.com/redirected/beacon"
            on { headers("Set-Cookie") } doReturn listOf("session=new")
        }
        val client = mock<DivNetworkClient>()
        runBlocking { whenever(client.execute(requestCaptor.capture())).thenReturn(response) }
        val cookieStorage = mock<CookieStorage> {
            on { getCookies(any()) } doReturn "session=old"
        }

        val configuration = SendBeaconConfiguration(
            executor = mock(),
            networkClient = client,
            workerScheduler = mock(),
            perWorkerLogger = mock(),
            databaseName = "test_beacon",
        )
        val beaconResponse = configuration.requestExecutor.execute(
            SendBeaconRequest(
                Uri.parse("https://example.com/beacon"),
                mapOf("X-Test" to "value"),
                JSONObject().put("event", "shown"),
                cookieStorage,
            )
        )

        val request = requestCaptor.firstValue
        assertEquals("POST", request.method)
        assertEquals("value", request.headers.single { it.name == "X-Test" }.value)
        assertEquals("session=old", request.headers.single { it.name == "Cookie" }.value)
        assertEquals("{\"event\":\"shown\"}", request.body!!.decodeToString())
        assertEquals(200, beaconResponse.responseCode)
        assertEquals(true, beaconResponse.isValid())
        verify(response).close()
        verify(cookieStorage).processCookies(listOf("session=new"), "https://cdn.example.com/redirected/beacon")
    }

    @Test
    fun `wraps unexpected client failure into IOException`() {
        val cause = IllegalArgumentException("Malformed URL")
        val client = mock<DivNetworkClient>()
        runBlocking { whenever(client.execute(any())).thenThrow(cause) }
        val request = SendBeaconRequest(
            Uri.parse("invalid://beacon"),
            emptyMap(),
            null,
            null,
        )

        val error = try {
            DivNetworkSendBeaconRequestExecutor(client).execute(request)
            null
        } catch (error: IOException) {
            error
        }

        assertEquals(cause, error?.cause)
    }
}
