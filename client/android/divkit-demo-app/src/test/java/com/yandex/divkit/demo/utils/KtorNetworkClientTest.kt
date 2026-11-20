package com.yandex.divkit.demo.utils

import com.yandex.div.core.network.DivNetworkRequest
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KtorNetworkClientTest {

    @Test
    fun `POST redirected with 303 is followed with GET without body`() {
        val redirected = submitWithRedirect(303)

        assertEquals("GET", redirected.method)
        assertEquals(0L, redirected.bodySize)
        assertNull(redirected.getHeader("Content-Type"))
    }

    @Test
    fun `POST redirected with 307 keeps method and body`() {
        val redirected = submitWithRedirect(307)

        assertEquals("POST", redirected.method)
        assertEquals("value=1", redirected.body.readUtf8())
    }

    @Test
    fun `Authorization is not sent to another origin after a redirect`() = runBlocking<Unit> {
        MockWebServer().use { target ->
            MockWebServer().use { source ->
                source.enqueue(
                    MockResponse().setResponseCode(302).setHeader("Location", target.url("/next"))
                )
                target.enqueue(MockResponse().setBody("next"))
                val request = DivNetworkRequest.Builder(source.url("/start").toString())
                    .addHeader("Authorization", "Bearer token")
                    .build()

                execute(request)

                assertEquals("Bearer token", source.takeRequest().getHeader("Authorization"))
                assertEquals(1, target.requestCount)
                assertNull(target.takeRequest().getHeader("Authorization"))
            }
        }
    }

    @Test
    fun `query-only Location keeps the request path`() = runBlocking<Unit> {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(302).setHeader("Location", "?page=2"))
            server.enqueue(MockResponse().setBody("page"))

            execute(DivNetworkRequest.Builder(server.url("/a/b/list?page=1").toString()).build())

            assertEquals(2, server.requestCount)
            server.takeRequest()
            assertEquals("/a/b/list?page=2", server.takeRequest().path)
        }
    }

    private fun submitWithRedirect(code: Int): RecordedRequest = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(code).setHeader("Location", "/done"))
            server.enqueue(MockResponse().setBody("done"))
            val request = DivNetworkRequest.Builder(server.url("/submit").toString())
                .method("POST")
                .body("value=1".encodeToByteArray())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            assertEquals(server.url("/done").toString(), execute(request))
            assertEquals(2, server.requestCount)
            assertEquals("POST", server.takeRequest().method)
            server.takeRequest()
        }
    }

    private suspend fun execute(request: DivNetworkRequest): String {
        return createKtorNetworkClient(OkHttpClient()).execute(request).use { response ->
            assertEquals(200, response.code)
            response.url
        }
    }
}
