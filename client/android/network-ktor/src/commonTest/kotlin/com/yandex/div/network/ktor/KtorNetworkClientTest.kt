package com.yandex.div.network.ktor

import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.readBytes
import io.ktor.client.HttpClient
import io.ktor.client.engine.mergeHeaders
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.cancel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(InternalAPI::class)
class KtorNetworkClientTest {

    @Test
    fun `request method headers and body are passed to the engine`() = runTest {
        val received = CompletableDeferred<Pair<HttpRequestData, ByteArray>>()
        mockClient { request ->
            received.complete(request to request.body.toByteArray())
            respondOk()
        }.use { httpClient ->
            KtorNetworkClient(httpClient).execute(
                DivNetworkRequest.Builder("https://example.com/submit")
                    .method("PUT")
                    .addHeader("X-Value", "first")
                    .addHeader("X-Value", "second")
                    .body(byteArrayOf(1, 2, 3))
                    .contentType("application/json")
                    .build()
            ).close()
        }

        val (request, body) = received.await()
        assertEquals(HttpMethod.Put, request.method)
        assertEquals("https://example.com/submit", request.url.toString())
        assertEquals(listOf("first", "second"), request.headers.getAll("X-Value"))
        assertEquals(ContentType.Application.Json, request.body.contentType)
        assertContentEquals(byteArrayOf(1, 2, 3), body)
    }

    @Test
    fun `headers reserved by Ktor for the engine are not forwarded`() = runTest {
        val received = CompletableDeferred<HttpRequestData>()
        mockClient { request ->
            received.complete(request)
            respondOk()
        }.use { httpClient ->
            KtorNetworkClient(httpClient).execute(
                DivNetworkRequest.Builder("https://example.com/submit")
                    .addHeader(HttpHeaders.TransferEncoding, "chunked")
                    .build()
            ).close()
        }

        assertNull(received.await().headers[HttpHeaders.TransferEncoding])
    }

    @Test
    fun `skip moves past body bytes and returns a short count at the end`() = runTest {
        mockClient { respond("0123456789") }.use { httpClient ->
            KtorNetworkClient(httpClient)
                .execute(DivNetworkRequest.Builder("https://example.com/video").build())
                .use { response ->
                    val body = assertNotNull(response.body)
                    val buffer = ByteArray(3)

                    assertEquals(4L, body.skip(4))
                    assertEquals(3, body.read(buffer, 0, buffer.size))
                    assertEquals("456", buffer.decodeToString())
                    assertEquals(3L, body.skip(100))
                }
        }
    }

    @Test
    fun `skip on a body that cannot be read throws`() = runTest {
        mockClient {
            val content = ByteChannel().apply { cancel(IOException("connection reset")) }
            respond(content, HttpStatusCode.OK)
        }.use { httpClient ->
            KtorNetworkClient(httpClient)
                .execute(DivNetworkRequest.Builder("https://example.com/video").build())
                .use { response -> assertFailsWith<IOException> { response.body?.skip(1) } }
        }
    }

    @Test
    fun `repeated cookie headers are merged with the cookie separator`() = runTest {
        val merged = CompletableDeferred<Map<String, String>>()
        mockClient { request ->
            val headers = mutableMapOf<String, String>()
            mergeHeaders(request.headers, request.body) { name, value -> headers[name] = value }
            merged.complete(headers)
            respondOk()
        }.use { httpClient ->
            KtorNetworkClient(httpClient).execute(
                DivNetworkRequest.Builder("https://example.com/submit")
                    .addHeader("cookie", "first=1")
                    .addHeader("COOKIE", "second=2")
                    .build()
            ).close()
        }

        assertEquals("first=1; second=2", merged.await()[HttpHeaders.Cookie])
    }

    @Test
    fun `unsuccessful response is returned with its headers and body`() = runTest {
        mockClient(expectSuccess = true) {
            respond(
                content = "missing".encodeToByteArray(),
                status = HttpStatusCode.NotFound,
                headers = headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    "Set-Cookie" to listOf("first=1", "second=2"),
                ),
            )
        }.use { httpClient ->
            KtorNetworkClient(httpClient)
                .execute(DivNetworkRequest.Builder("https://example.com/patch.json").build())
                .use { response ->
                    assertEquals(404, response.code)
                    assertFalse(response.isSuccessful)
                    assertEquals("https://example.com/patch.json", response.url)
                    assertEquals("application/json", response.contentType)
                    assertEquals(listOf("first=1", "second=2"), response.headers("SET-COOKIE"))
                    assertContentEquals("missing".encodeToByteArray(), response.body?.readBytes())
                }
        }
    }

    @Test
    fun `response is returned when its body cannot be read`() = runTest {
        mockClient {
            val content = ByteChannel().apply { cancel(IOException("connection reset")) }
            respond(content, HttpStatusCode.OK)
        }.use { httpClient ->
            KtorNetworkClient(httpClient)
                .execute(DivNetworkRequest.Builder("https://example.com/submit").build())
                .use { response ->
                    assertEquals(200, response.code)
                    assertFailsWith<IOException> { response.body?.readBytes() }
                }
        }
    }

    @Test
    fun `response is returned before its body completes`() = runTest(timeout = 5.seconds) {
        val content = ByteChannel()
        mockClient { respond(content, HttpStatusCode.OK) }.use { httpClient ->
            KtorNetworkClient(httpClient)
                .execute(DivNetworkRequest.Builder("https://example.com/submit").build())
                .use { response -> assertEquals(200, response.code) }
        }
    }

    @Test
    fun `coroutine cancellation stops a body read in progress`() = runTest(timeout = 5.seconds) {
        val readWaiting = CompletableDeferred<Unit>()
        val channel = ByteChannel()
        val content = object : ByteReadChannel by channel {
            override suspend fun awaitContent(min: Int): Boolean {
                readWaiting.complete(Unit)
                return channel.awaitContent(min)
            }
        }
        mockClient { respond(content, HttpStatusCode.OK) }.use { httpClient ->
            val request = DivNetworkRequest.Builder("https://example.com/patch.json").build()
            val job = launch(Dispatchers.Default) {
                KtorNetworkClient(httpClient).execute(request).use { response ->
                    assertFailsWith<IOException> { response.body?.readBytes() }
                }
            }
            readWaiting.await()
            job.cancel()

            val stopped = withContext(Dispatchers.Default) {
                withTimeoutOrNull(2.seconds) { job.join() }
            } != null
            channel.cancel()
            assertTrue(stopped, "The body read was not stopped by cancellation")
        }
    }

    @Test
    fun `engine failure before headers is thrown from execute`() = runTest(timeout = 5.seconds) {
        mockClient { throw IOException("connection refused") }.use { httpClient ->
            val request = DivNetworkRequest.Builder("https://example.com").build()
            assertFailsWith<IOException> { KtorNetworkClient(httpClient).execute(request) }
        }
    }

    @Test
    fun `coroutine cancellation cancels the engine request`() = runTest {
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        mockClient {
            started.complete(Unit)
            try {
                awaitCancellation()
            } catch (e: CancellationException) {
                cancelled.complete(Unit)
                throw e
            }
        }.use { httpClient ->
            val request = DivNetworkRequest.Builder("https://example.com").build()
            val job = launch { KtorNetworkClient(httpClient).execute(request) }
            started.await()
            job.cancel()

            cancelled.await()
        }
    }

    @Test
    fun `URL that is not absolute http or https is rejected`() = runTest {
        val urls = listOf(
            "/patch.json",
            "example.com/patch.json",
            "https:example.com/patch.json",
            "//example.com/patch.json",
            "ftp://example.com/patch.json",
        )
        mockClient { respondOk() }.use { httpClient ->
            urls.forEach { url ->
                assertFailsWith<IllegalArgumentException>(url) {
                    KtorNetworkClient(httpClient).execute(DivNetworkRequest.Builder(url).build())
                }
            }
        }
    }

    private fun mockClient(
        expectSuccess: Boolean = false,
        handler: MockRequestHandler,
    ): HttpClient {
        return HttpClient(MockEngine(handler)) {
            this.expectSuccess = expectSuccess
        }
    }
}
