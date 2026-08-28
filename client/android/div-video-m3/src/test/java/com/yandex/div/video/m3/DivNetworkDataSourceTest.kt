package com.yandex.div.video.m3

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException
import androidx.media3.datasource.TransferListener
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DivNetworkDataSourceTest {
    private val requestScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Test
    fun `range request preserves method body and requested length`() {
        val response = FakeResponse("response".toByteArray(), 206, mapOf("Content-Length" to listOf("5")))
        val client = FakeClient(response)
        val source = dataSource(client)
        val body = "request".toByteArray()
        val dataSpec = DataSpec.Builder()
            .setUri(Uri.parse("https://example.com/video"))
            .setHttpMethod(DataSpec.HTTP_METHOD_POST)
            .setHttpBody(body)
            .setPosition(10)
            .setLength(5)
            .build()

        assertEquals(5, source.open(dataSpec))
        assertEquals("POST", client.request.method)
        assertArrayEquals(body, client.request.body)
        assertEquals("bytes=10-14", client.request.headers.single { it.name == "Range" }.value)
        source.close()
    }

    @Test
    fun `skips requested position when server ignores range`() {
        val response = FakeResponse("012345".toByteArray(), 200, mapOf("Content-Length" to listOf("6")))
        val source = dataSource(FakeClient(response))
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setPosition(2)
            .build()

        assertEquals(4, source.open(dataSpec))
        val buffer = ByteArray(4)
        assertEquals(4, source.read(buffer, 0, buffer.size))
        assertEquals("2345", buffer.decodeToString())
        assertEquals(C.RESULT_END_OF_INPUT, source.read(buffer, 0, buffer.size))
        source.close()
    }

    @Test
    fun `skipped bytes are reported while skipping after transfer starts`() {
        val events = mutableListOf<String>()
        val body = RecordingResponseBody {
            events += "skip"
            1
        }
        val source = sourceWith(body)
        source.addTransferListener(RecordingTransferListener(
            onStart = { events += "start" },
            onBytes = { events += "bytes:" + it },
        ))
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setPosition(2)
            .build()

        source.open(dataSpec)

        assertEquals(listOf("start", "skip", "bytes:1", "skip", "bytes:1"), events)
        source.close()
    }

    @Test
    fun `close during transfer initialization prevents request`() {
        var requestExecuted = false
        val source = dataSource(DivNetworkClient {
            requestExecuted = true
            FakeResponse(ByteArray(0), 200)
        })
        source.addTransferListener(RecordingTransferListener(onInitializing = { it.close() }))

        org.junit.Assert.assertThrows(HttpDataSourceException::class.java) {
            source.open(DataSpec(Uri.parse("https://example.com/video")))
        }

        assertFalse(requestExecuted)
        assertEquals(null, source.uri)
    }

    @Test
    fun `reentrant close stops skipped byte reporting and ends transfer once`() {
        val events = mutableListOf<String>()
        val body = object : DivNetworkResponseBody {
            override fun read(buffer: ByteArray, offset: Int, length: Int): Int = -1
            override fun skip(byteCount: Long): Long {
                events += "skip"
                return byteCount
            }
            override fun close() = Unit
        }
        val response = object : DivNetworkResponse {
            override val code = 200
            override val contentType: String? = null
            override val body = body
            override val url = "https://example.com/video"
            override fun headers(name: String): List<String> = emptyList()
            override fun close() = Unit
        }
        val source = dataSource(DivNetworkClient { response })
        val listener = object : TransferListener {
            override fun onTransferInitializing(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) = Unit
            override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
                events += "start"
            }
            override fun onBytesTransferred(
                source: DataSource,
                dataSpec: DataSpec,
                isNetwork: Boolean,
                bytesTransferred: Int,
            ) {
                events += "bytes:$bytesTransferred"
                source.close()
            }
            override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
                events += "end"
            }
        }
        source.addTransferListener(listener)
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setPosition(Int.MAX_VALUE.toLong() + 1)
            .build()

        org.junit.Assert.assertThrows(HttpDataSourceException::class.java) { source.open(dataSpec) }

        assertEquals(listOf("start", "skip", "bytes:2147483647", "end"), events)
    }

    @Test
    fun `head response without body opens as empty input`() {
        val source = dataSource(FakeClient(FakeResponse(null, 200)))
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setHttpMethod(DataSpec.HTTP_METHOD_HEAD)
            .build()

        assertEquals(0, source.open(dataSpec))
        assertEquals(C.RESULT_END_OF_INPUT, source.read(ByteArray(1), 0, 1))
        source.close()
    }

    @Test
    fun `range at resource end is treated as empty input`() {
        val response = FakeResponse(null, 416, mapOf("Content-Range" to listOf("bytes */6")))
        val source = dataSource(FakeClient(response))
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setPosition(6)
            .build()

        assertEquals(0, source.open(dataSpec))
        assertEquals(C.RESULT_END_OF_INPUT, source.read(ByteArray(1), 0, 1))
        source.close()
    }

    @Test
    fun `range overflow is reported as Media3 open error`() {
        val source = dataSource(FakeClient(FakeResponse(ByteArray(0), 200)))
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setPosition(Long.MAX_VALUE)
            .setLength(2)
            .build()

        val error = try {
            source.open(dataSpec)
            null
        } catch (error: HttpDataSourceException) {
            error
        }

        assertEquals(HttpDataSourceException.TYPE_OPEN, error?.type)
    }

    @Test
    fun `close releases response`() {
        val response = FakeResponse(ByteArray(0), 200)
        val client = FakeClient(response)
        val source = dataSource(client)
        source.open(DataSpec(Uri.parse("https://example.com/video")))

        source.close()

        assertTrue(response.closed)
    }

    @Test
    fun `uses resolved response URL`() {
        val response = FakeResponse(
            body = ByteArray(0),
            code = 200,
            url = "https://cdn.example.com/video/playlist.m3u8",
        )
        val source = dataSource(FakeClient(response))

        source.open(DataSpec(Uri.parse("https://example.com/redirect")))

        assertEquals(response.url, source.uri.toString())
        source.close()
    }

    @Test
    fun `premature end is reported as Media3 read error`() {
        val source = dataSource(FakeClient(FakeResponse("12".toByteArray(), 200)))
        val dataSpec = DataSpec.Builder()
            .setUri("https://example.com/video")
            .setLength(5)
            .build()
        source.open(dataSpec)
        val buffer = ByteArray(5)

        assertEquals(2, source.read(buffer, 0, buffer.size))
        val error = try {
            source.read(buffer, 2, buffer.size - 2)
            null
        } catch (error: HttpDataSourceException) {
            error
        }

        assertEquals(HttpDataSourceException.TYPE_READ, error?.type)
        assertTrue(error?.cause is EOFException)
    }

    @Test
    fun `non successful response preserves HTTP status`() {
        val source = dataSource(FakeClient(FakeResponse(ByteArray(0), 404)))
        val dataSpec = DataSpec(Uri.parse("https://example.com/missing"))

        val error = try {
            source.open(dataSpec)
            null
        } catch (error: InvalidResponseCodeException) {
            error
        }

        assertEquals(404, error?.responseCode)
        assertEquals(dataSpec, error?.dataSpec)
    }

    @Test
    fun `network failure is reported as Media3 open error`() {
        val cause = IOException("Network unavailable")
        val client = DivNetworkClient { throw cause }
        val source = dataSource(client)

        val error = try {
            source.open(DataSpec(Uri.parse("https://example.com/video")))
            null
        } catch (error: HttpDataSourceException) {
            error
        }

        assertEquals(HttpDataSourceException.TYPE_OPEN, error?.type)
        assertEquals(cause.message, error?.cause?.message)
    }

    @Test
    fun `cancelling request scope cancels request while open is waiting`() {
        val started = CountDownLatch(1)
        val cancelled = CountDownLatch(1)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val client = DivNetworkClient {
            suspendCancellableCoroutine { continuation ->
                started.countDown()
                continuation.invokeOnCancellation { cancelled.countDown() }
            }
        }
        val source = DivNetworkDataSource(client, scope)
        val opener = thread {
            runCatching { source.open(DataSpec(Uri.parse("https://example.com/video"))) }
        }
        assertTrue(started.await(5, TimeUnit.SECONDS))

        scope.cancel()

        assertTrue(cancelled.await(5, TimeUnit.SECONDS))
        opener.join(5_000)
        assertFalse(opener.isAlive)
    }

    @Test
    fun `close cancels request while open is waiting`() {
        val started = CountDownLatch(1)
        val cancelled = CountDownLatch(1)
        val client = DivNetworkClient {
            suspendCancellableCoroutine { continuation ->
                started.countDown()
                continuation.invokeOnCancellation { cancelled.countDown() }
            }
        }
        val source = dataSource(client)
        val opener = thread {
            runCatching { source.open(DataSpec(Uri.parse("https://example.com/video"))) }
        }
        assertTrue(started.await(5, TimeUnit.SECONDS))

        source.close()

        assertTrue(cancelled.await(5, TimeUnit.SECONDS))
        opener.join(5_000)
        assertFalse(opener.isAlive)
    }

    @Test
    fun `close interrupts body skip during open`() {
        val skipStarted = CountDownLatch(1)
        val bodyClosed = CountDownLatch(1)
        val body = object : DivNetworkResponseBody {
            override fun read(buffer: ByteArray, offset: Int, length: Int): Int = -1
            override fun skip(byteCount: Long): Long {
                skipStarted.countDown()
                bodyClosed.await(5, TimeUnit.SECONDS)
                return 0
            }
            override fun close() {
                bodyClosed.countDown()
            }
        }
        val response = object : DivNetworkResponse {
            override val url = "https://example.com/video"
            override val code = 200
            override val contentType: String? = null
            override val body: DivNetworkResponseBody = body
            override fun headers(name: String): List<String> = emptyList()
            override fun close() = body.close()
        }
        val source = dataSource(DivNetworkClient { response })
        val openError = AtomicReference<Throwable?>()
        val opener = thread {
            runCatching {
                source.open(
                    DataSpec.Builder()
                        .setUri("https://example.com/video")
                        .setPosition(1)
                        .build()
                )
            }.exceptionOrNull()?.let(openError::set)
        }
        assertTrue(skipStarted.await(5, TimeUnit.SECONDS))

        source.close()

        opener.join(5_000)
        assertFalse(opener.isAlive)
        assertTrue(openError.get() is HttpDataSourceException)
    }

    private fun sourceWith(body: DivNetworkResponseBody): DivNetworkDataSource {
        return dataSource(DivNetworkClient { StreamingResponse(body) })
    }

    private fun dataSource(client: DivNetworkClient): DivNetworkDataSource {
        return DivNetworkDataSource(client, requestScope)
    }

    private class RecordingResponseBody(
        private val onSkip: (Long) -> Long,
    ) : DivNetworkResponseBody {
        override fun read(buffer: ByteArray, offset: Int, length: Int): Int = -1
        override fun skip(byteCount: Long): Long = onSkip(byteCount)
        override fun close() = Unit
    }

    private class RecordingTransferListener(
        private val onInitializing: (DataSource) -> Unit = {},
        private val onStart: () -> Unit = {},
        private val onBytes: (Int) -> Unit = {},
        private val onEnd: () -> Unit = {},
    ) : TransferListener {
        override fun onTransferInitializing(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) {
            onInitializing(source)
        }

        override fun onTransferStart(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) = onStart()

        override fun onBytesTransferred(
            source: DataSource,
            dataSpec: DataSpec,
            isNetwork: Boolean,
            bytesTransferred: Int,
        ) = onBytes(bytesTransferred)

        override fun onTransferEnd(source: DataSource, dataSpec: DataSpec, isNetwork: Boolean) = onEnd()
    }

    private class StreamingResponse(
        override val body: DivNetworkResponseBody,
    ) : DivNetworkResponse {
        override val code = 200
        override val contentType: String? = null
        override val url = "https://example.com/video"
        override fun headers(name: String): List<String> = emptyList()
        override fun close() = body.close()
    }

    private class FakeClient(private val response: FakeResponse) : DivNetworkClient {
        lateinit var request: DivNetworkRequest

        override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
            this.request = request
            return response
        }
    }

    private class FakeResponse(
        body: ByteArray?,
        override val code: Int,
        private val responseHeaders: Map<String, List<String>> = emptyMap(),
        override val url: String = "https://example.com/video",
    ) : DivNetworkResponse {
        override val contentType: String? = null
        override val body: DivNetworkResponseBody? = body?.let(DivNetworkResponseBody::fromBytes)
        var closed = false
        override fun headers(name: String): List<String> = responseHeaders[name].orEmpty()
        override fun close() { closed = true }
    }
}
