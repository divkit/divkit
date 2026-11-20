package com.yandex.div.compose.images

import coil3.network.HttpException
import coil3.network.NetworkRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame

class KtorImageNetworkClientTest {

    @Test
    fun `rejecting a response does not wait for its body`() = runBlocking<Unit> {
        MockWebServer().use { server ->
            server.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody("error")
                    .setBodyDelay(2, TimeUnit.SECONDS)
            )
            HttpClient(OkHttp).use { httpClient ->
                val client = KtorImageNetworkClient(httpClient)
                val request = NetworkRequest(server.url("/image.png").toString())

                val error = withTimeout(1_000) {
                    assertFailsWith<HttpException> {
                        client.executeRequest(request) { response ->
                            delay(100)
                            throw HttpException(response)
                        }
                    }
                }

                assertEquals(500, error.response.code)
            }
        }
    }

    @Test
    fun `response body is not closed on the thread that cancels the request`() = runBlocking<Unit> {
        val closingThread = CompletableDeferred<Thread>()
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .body(ClosingThreadRecordingBody(response.body!!, closingThread))
                    .build()
            }
            .build()
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("image"))
            ImageHttpClientFactory.create(okHttpClient).use { httpClient ->
                Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { caller ->
                    val read = CompletableDeferred<Unit>()
                    val request = NetworkRequest(server.url("/image.png").toString())
                    val job = launch(caller) {
                        KtorImageNetworkClient(httpClient).executeRequest(request) { response ->
                            response.body?.writeTo(Buffer())
                            read.complete(Unit)
                            awaitCancellation()
                        }
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
