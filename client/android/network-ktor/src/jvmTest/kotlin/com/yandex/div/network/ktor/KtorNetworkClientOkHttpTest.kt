package com.yandex.div.network.ktor

import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.readBytes
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import okio.BufferedSource
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.GZIPOutputStream
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame

private const val LARGE_BODY_SIZE = 4 * 1024 * 1024

class KtorNetworkClientOkHttpTest {

    @Test
    fun `response URL is the URL of the last request when Ktor follows a redirect`() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(
                MockResponse().setResponseCode(302).setHeader("Location", "/bundle.lottie")
            )
            server.enqueue(
                MockResponse()
                    .setHeader("Content-Type", "application/octet-stream")
                    .setBody("bundle")
            )
            HttpClient(OkHttp).use { httpClient ->
                val request = DivNetworkRequest.Builder(server.url("/animation").toString()).build()

                KtorNetworkClient(httpClient).execute(request).use { response ->
                    assertEquals(server.url("/bundle.lottie").toString(), response.url)
                }
            }
        }
    }

    @Test
    fun `request timeout while reading the body throws IOException`() = runBlocking<Unit> {
        MockWebServer().use { server ->
            server.enqueue(
                MockResponse().setBody("0123456789").throttleBody(1, 1, TimeUnit.SECONDS)
            )
            val client = KtorNetworkClient(OkHttp) {
                install(HttpTimeout) { requestTimeoutMillis = 500 }
            }
            val request = DivNetworkRequest.Builder(server.url("/patch.json").toString()).build()

            client.execute(request).use { response ->
                assertFailsWith<IOException> { response.body?.readBytes() }
            }
        }
    }

    @Test
    fun `response body is not closed on the thread that cancels the call`() = runBlocking<Unit> {
        val closingThread = CompletableDeferred<Thread>()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .body(ClosingThreadRecordingBody(response.body!!, closingThread))
                    .build()
            }
            .build()
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("patch"))
            HttpClient(OkHttp) { engine { preconfigured = okHttpClient } }.use { httpClient ->
                Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { caller ->
                    val read = CompletableDeferred<Unit>()
                    val request = DivNetworkRequest.Builder(server.url("/patch.json").toString()).build()
                    val job = launch(caller) {
                        KtorNetworkClient(httpClient).execute(request).body?.readBytes()
                        read.complete(Unit)
                        awaitCancellation()
                    }
                    read.await()

                    val callerThread = withContext(caller) {
                        job.cancel()
                        Thread.currentThread()
                    }

                    assertNotSame(callerThread, withTimeout(2_000) { closingThread.await() })
                }
            }
        }
    }

    @Test
    fun `body is read on Dispatchers IO from a client created with an engine factory`() = runBlocking<Unit> {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody(Buffer().write(ByteArray(LARGE_BODY_SIZE))))
            val request = DivNetworkRequest.Builder(server.url("/patch.json").toString()).build()
            val response = KtorNetworkClient(OkHttp).execute(request)
            val read = CountDownLatch(1)
            val timedOut = AtomicBoolean()
            val watchdog = thread {
                if (!read.await(2, TimeUnit.SECONDS)) {
                    timedOut.set(true)
                    response.close()
                }
            }

            val size = withContext(Dispatchers.IO) {
                runCatching { response.use { it.body?.readBytes()?.size } }
            }
            read.countDown()
            watchdog.join()

            assertFalse(timedOut.get(), "The body was not read on the only Dispatchers.IO thread")
            assertEquals(LARGE_BODY_SIZE, size.getOrThrow())
        }
    }

    @Test
    fun `body is returned decompressed by the ContentEncoding plugin`() = runBlocking {
        val patch = """{"patch":{"changes":[]}}"""
        val compressed = Buffer().also { buffer ->
            GZIPOutputStream(buffer.outputStream()).use { it.write(patch.encodeToByteArray()) }
        }
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setHeader("Content-Encoding", "gzip").setBody(compressed))
            HttpClient(OkHttp) { install(ContentEncoding) { gzip() } }.use { httpClient ->
                val request = DivNetworkRequest.Builder(server.url("/patch.json").toString()).build()

                KtorNetworkClient(httpClient).execute(request).use { response ->
                    assertEquals(patch, response.body?.readBytes()?.decodeToString())
                }
            }
        }
    }
}

private class ClosingThreadRecordingBody(
    private val delegate: ResponseBody,
    private val closingThread: CompletableDeferred<Thread>,
) : ResponseBody() {
    override fun contentType(): MediaType? = delegate.contentType()

    override fun contentLength(): Long = delegate.contentLength()

    override fun source(): BufferedSource = delegate.source()

    override fun close() {
        closingThread.complete(Thread.currentThread())
        delegate.close()
    }
}
