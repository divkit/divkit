package com.yandex.div.network

import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.readBytes
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertTrue
import kotlin.test.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class OkHttpDivNetworkClientTest {

    @Test
    fun `POST without body uses empty request body`() {
        val requestCaptor = argumentCaptor<Request>()
        val call = mock<Call>()
        doAnswer { invocation ->
            invocation.getArgument<Callback>(0).onFailure(call, IOException("stop"))
        }.whenever(call).enqueue(any())
        val callFactory = mock<Call.Factory> {
            on { newCall(requestCaptor.capture()) } doReturn call
        }

        runBlocking {
            runCatching {
                OkHttpDivNetworkClient(callFactory).execute(
                    DivNetworkRequest.Builder("https://example.com")
                        .method("POST")
                        .build()
                )
            }
        }

        val body = requestCaptor.firstValue.body
        assertNotNull(body)
        assertEquals(0L, body!!.contentLength())
    }

    @Test
    fun `response header lookup ignores case and preserves all values`() {
        val request = Request.Builder().url("https://example.com").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .addHeader("Set-Cookie", "first=1")
            .addHeader("set-cookie", "second=2")
            .body("body".toResponseBody())
            .build()
        val call = mock<Call>()
        doAnswer { invocation ->
            invocation.getArgument<Callback>(0).onResponse(call, response)
        }.whenever(call).enqueue(any())
        val client = OkHttpDivNetworkClient(mock {
            on { newCall(any()) } doReturn call
        })

        runBlocking { client.execute(DivNetworkRequest.Builder(request.url.toString()).build()) }.use {
            assertEquals(listOf("first=1", "second=2"), it.headers("SET-COOKIE"))
        }
    }

    @Test
    fun `coroutine cancellation cancels okhttp call`() = runBlocking {
        val call = mock<Call>()
        val client = OkHttpDivNetworkClient(mock {
            on { newCall(any()) } doReturn call
        })
        val request = DivNetworkRequest.Builder("https://example.com").build()

        val job = async(start = CoroutineStart.UNDISPATCHED) { client.execute(request) }
        job.cancelAndJoin()

        verify(call).cancel()
    }

    @Test
    fun `coroutine cancellation closes response while body is being read`() = runBlocking {
        val readStarted = CountDownLatch(1)
        val streamClosed = CountDownLatch(1)
        val stream = object : InputStream() {
            override fun read(): Int {
                readStarted.countDown()
                streamClosed.await()
                return -1
            }

            override fun close() {
                streamClosed.countDown()
            }
        }
        val request = Request.Builder().url("https://example.com").build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(stream.source().buffer().asResponseBody())
            .build()
        val call = mock<Call>()
        doAnswer { invocation ->
            invocation.getArgument<Callback>(0).onResponse(call, response)
        }.whenever(call).enqueue(any())
        val client = OkHttpDivNetworkClient(mock {
            on { newCall(any()) } doReturn call
        })

        val job = async(Dispatchers.IO) {
            client.execute(DivNetworkRequest.Builder(request.url.toString()).build()).use {
                it.body!!.readBytes()
            }
        }
        try {
            // The test JVM limits Dispatchers.IO to one thread, occupied by this read.
            assertTrue(readStarted.await(5, TimeUnit.SECONDS))
            job.cancel()

            assertTrue(streamClosed.await(5, TimeUnit.SECONDS), "Cleanup was blocked by the body reader")
            withTimeout(5_000) { job.join() }
            verify(call).cancel()
        } finally {
            // Unblock the reader even if cleanup regresses, so a failure cannot hang the test JVM.
            streamClosed.countDown()
            job.cancelAndJoin()
        }
    }

    @Test
    fun `cancellation closes unread HTTP response off the cancelling thread`() = runBlocking {
        val cancellingThread = Thread.currentThread()
        val closed = CountDownLatch(1)
        val closeThread = AtomicReference<Thread>()
        val closeCount = AtomicInteger()
        val responseReceived = CompletableDeferred<Unit>()
        val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val body = checkNotNull(response.body)
                val observedBody = object : ResponseBody() {
                    override fun contentType() = body.contentType()
                    override fun contentLength() = body.contentLength()
                    override fun source() = body.source()

                    override fun close() {
                        closeThread.set(Thread.currentThread())
                        closeCount.incrementAndGet()
                        try {
                            body.close()
                        } finally {
                            closed.countDown()
                        }
                    }
                }
                response.newBuilder()
                    .body(observedBody)
                    .build()
            }
            .build()
        try {
            MockWebServer().use { server ->
                server.enqueue(MockResponse().setBody("{}").setBodyDelay(1, TimeUnit.SECONDS))
                val request = DivNetworkRequest.Builder(server.url("/patch.json").toString()).build()
                val job = async(start = CoroutineStart.UNDISPATCHED) {
                    OkHttpDivNetworkClient(httpClient).execute(request).use {
                        responseReceived.complete(Unit)
                        try {
                            awaitCancellation()
                        } finally {
                            // Finish cancellation cleanup before use() tries to close the response again.
                            assertTrue(closed.await(5, TimeUnit.SECONDS), "Response body was not closed")
                        }
                    }
                }
                try {
                    withTimeout(5_000) { responseReceived.await() }
                    job.cancelAndJoin()
                    assertEquals(1, closeCount.get())
                    assertNotSame(cancellingThread, closeThread.get(), "Response body closed on the cancelling thread")
                } finally {
                    job.cancelAndJoin()
                }
            }
        } finally {
            httpClient.dispatcher.executorService.shutdownNow()
            httpClient.connectionPool.evictAll()
        }
    }
}
