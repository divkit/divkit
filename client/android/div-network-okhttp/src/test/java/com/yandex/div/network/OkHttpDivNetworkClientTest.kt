package com.yandex.div.network

import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.readBytes
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.asResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.buffer
import okio.source
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        assertTrue(readStarted.await(5, TimeUnit.SECONDS))

        job.cancelAndJoin()

        assertTrue(streamClosed.await(5, TimeUnit.SECONDS))
        verify(call).cancel()
    }
}
