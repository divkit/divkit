package com.yandex.div.video.m3

import android.net.Uri
import androidx.media3.common.C as M3Constants
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@UnstableApi
internal class DivNetworkDataSource(
    private val client: DivNetworkClient,
    private val requestScope: CoroutineScope,
) : BaseDataSource(true) {
    private val resourceLock = Any()
    private val activeRequest = AtomicReference<Deferred<DivNetworkResponse>?>(null)
    private val activeResponse = AtomicReference<DivNetworkResponse?>(null)
    @Volatile
    private var closed = false
    private var input: DivNetworkResponseBody? = null
    private var uri: Uri? = null
    private var bytesRemaining = M3Constants.LENGTH_UNSET.toLong()
    private var openedDataSpec: DataSpec? = null
    private var opened = false

    override fun open(dataSpec: DataSpec): Long {
        initializeOpen(dataSpec)
        transferInitializing(dataSpec)

        try {
            ensureNotClosed()
            val response = executeRequest(buildRequest(dataSpec), dataSpec)
            val currentInput = response.responseBody(dataSpec)
            startTransfer(dataSpec, response, currentInput)
            if (response.code == HTTP_OK && dataSpec.position > 0) {
                skipFully(currentInput, dataSpec.position, ::reportSkippedBytesTransferred)
            }
            val remainingBytes = response.calculateBytesRemaining(dataSpec)
            synchronized(resourceLock) {
                ensureNotClosed()
                bytesRemaining = remainingBytes
            }
            return remainingBytes
        } catch (error: Throwable) {
            endTransferIfOpened()
            closeResources()
            throw error.asHttpDataSourceException(dataSpec, HttpDataSourceException.TYPE_OPEN)
        }
    }

    private fun initializeOpen(dataSpec: DataSpec) {
        synchronized(resourceLock) {
            closed = false
            uri = dataSpec.uri
            openedDataSpec = dataSpec
        }
    }

    private fun buildRequest(dataSpec: DataSpec): DivNetworkRequest {
        val headers = dataSpec.httpRequestHeaders.map { it.key to it.value }.toMutableList()
        if (dataSpec.position != 0L || dataSpec.length != M3Constants.LENGTH_UNSET.toLong()) {
            headers.removeAll { it.first.equals(RANGE_HEADER, ignoreCase = true) }
            val end = dataSpec.length.takeIf { it != M3Constants.LENGTH_UNSET.toLong() }
                ?.let { length -> Math.addExact(dataSpec.position, length - 1) }
            headers += RANGE_HEADER to buildString {
                append("bytes=")
                append(dataSpec.position)
                append('-')
                if (end != null) append(end)
            }
        }
        return DivNetworkRequest.Builder(dataSpec.uri.toString())
            .method(dataSpec.httpMethod.toMethodName())
            .body(dataSpec.httpBody)
            .apply { headers.forEach { (name, value) -> addHeader(name, value) } }
            .build()
    }

    private fun executeRequest(request: DivNetworkRequest, dataSpec: DataSpec): DivNetworkResponse {
        val currentRequest = requestScope.async { client.execute(request) }
        activeRequest.set(currentRequest)
        if (closed) currentRequest.cancel()
        val response = runBlocking { currentRequest.await() }
        activeRequest.compareAndSet(currentRequest, null)
        activeResponse.set(response)
        ensureNotClosed()
        if (!response.isSuccessful && !response.isEmptyRangeResponse(dataSpec)) {
            throw InvalidResponseCodeException(
                response.code,
                null,
                null,
                emptyMap(),
                dataSpec,
                ByteArray(0),
            )
        }
        return response
    }

    private fun DivNetworkResponse.responseBody(dataSpec: DataSpec): DivNetworkResponseBody {
        return body ?: if (
            dataSpec.httpMethod == DataSpec.HTTP_METHOD_HEAD || isEmptyRangeResponse(dataSpec)
        ) {
            DivNetworkResponseBody.fromBytes(ByteArray(0))
        } else {
            throw IOException("No response body received")
        }
    }

    private fun startTransfer(
        dataSpec: DataSpec,
        response: DivNetworkResponse,
        currentInput: DivNetworkResponseBody,
    ) {
        synchronized(resourceLock) {
            ensureNotClosed()
            uri = Uri.parse(response.url)
            input = currentInput
            opened = true
            transferStarted(dataSpec)
            ensureNotClosed()
        }
    }

    private fun DivNetworkResponse.calculateBytesRemaining(dataSpec: DataSpec): Long {
        val contentLength = headers(CONTENT_LENGTH_HEADER).firstOrNull()?.toLongOrNull()
        return when {
            isEmptyRangeResponse(dataSpec) -> 0
            dataSpec.httpMethod == DataSpec.HTTP_METHOD_HEAD -> 0
            dataSpec.length != M3Constants.LENGTH_UNSET.toLong() -> dataSpec.length
            contentLength == null -> M3Constants.LENGTH_UNSET.toLong()
            code == HTTP_OK -> contentLength - dataSpec.position
            else -> contentLength
        }
    }

    private fun ensureNotClosed() {
        if (closed) throw IOException("Data source was closed while opening")
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        if (bytesRemaining == 0L) return M3Constants.RESULT_END_OF_INPUT
        val bytesToRead = if (bytesRemaining == M3Constants.LENGTH_UNSET.toLong()) {
            length
        } else {
            min(bytesRemaining, length.toLong()).toInt()
        }
        val read = try {
            input?.read(buffer, offset, bytesToRead) ?: return M3Constants.RESULT_END_OF_INPUT
        } catch (error: IOException) {
            throw HttpDataSourceException.createForIOException(
                error,
                checkNotNull(openedDataSpec),
                HttpDataSourceException.TYPE_READ,
            )
        }
        if (read == -1) {
            if (bytesRemaining > 0) {
                throw HttpDataSourceException.createForIOException(
                    EOFException("Unexpected end of input"),
                    checkNotNull(openedDataSpec),
                    HttpDataSourceException.TYPE_READ,
                )
            }
            return M3Constants.RESULT_END_OF_INPUT
        }
        if (bytesRemaining != M3Constants.LENGTH_UNSET.toLong()) bytesRemaining -= read
        bytesTransferred(read)
        return read
    }

    override fun getUri(): Uri? = uri

    override fun close() {
        closed = true
        activeRequest.getAndSet(null)?.cancel()
        activeResponse.getAndSet(null)?.close()
        synchronized(resourceLock) {
            closeResourcesLocked()
        }
        endTransferIfOpened()
    }

    private fun endTransferIfOpened() {
        val shouldEndTransfer = synchronized(resourceLock) {
            val wasOpened = opened
            opened = false
            wasOpened
        }
        if (shouldEndTransfer) transferEnded()
    }

    private fun closeResources() {
        activeResponse.getAndSet(null)?.close()
        synchronized(resourceLock) { closeResourcesLocked() }
    }

    private fun closeResourcesLocked() {
        activeRequest.getAndSet(null)?.cancel()
        input = null
        uri = null
        bytesRemaining = M3Constants.LENGTH_UNSET.toLong()
        openedDataSpec = null
    }

    private fun skipFully(
        stream: DivNetworkResponseBody,
        bytesToSkip: Long,
        onBytesSkipped: (Int) -> Unit,
    ) {
        var remaining = bytesToSkip
        val singleByte = ByteArray(1)
        while (remaining > 0) {
            if (closed) throw IOException("Data source was closed while opening")
            val skipped = stream.skip(remaining)
            if (skipped > 0) {
                remaining -= skipped
                if (closed) throw IOException("Data source was closed while opening")
                reportSkippedBytes(skipped, onBytesSkipped)
                continue
            }
            if (stream.read(singleByte, 0, 1) == -1) throw EOFException("Unable to skip to position $bytesToSkip")
            remaining--
            if (closed) throw IOException("Data source was closed while opening")
            onBytesSkipped(1)
        }
    }

    private fun reportSkippedBytesTransferred(bytes: Int) {
        synchronized(resourceLock) {
            if (closed || !opened) throw IOException("Data source was closed while opening")
            bytesTransferred(bytes)
        }
    }

    private fun reportSkippedBytes(skipped: Long, onBytesSkipped: (Int) -> Unit) {
        var remaining = skipped
        while (remaining > 0) {
            val chunk = remaining.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            if (closed) throw IOException("Data source was closed while opening")
            onBytesSkipped(chunk)
            remaining -= chunk
        }
    }

    private fun DivNetworkResponse.isEmptyRangeResponse(dataSpec: DataSpec): Boolean {
        if (code != HTTP_RANGE_NOT_SATISFIABLE) return false
        val resourceSize = headers(CONTENT_RANGE_HEADER).firstOrNull()
            ?.let(CONTENT_RANGE_SIZE_REGEX::matchEntire)
            ?.groupValues
            ?.get(1)
            ?.toLongOrNull()
        return resourceSize == dataSpec.position
    }

    private fun Int.toMethodName(): String = when (this) {
        DataSpec.HTTP_METHOD_GET -> "GET"
        DataSpec.HTTP_METHOD_POST -> "POST"
        DataSpec.HTTP_METHOD_HEAD -> "HEAD"
        else -> throw IOException("Unsupported HTTP method: $this")
    }

    private fun Throwable.asHttpDataSourceException(
        dataSpec: DataSpec,
        type: Int,
    ): HttpDataSourceException {
        if (this is HttpDataSourceException) return this
        val ioException = this as? IOException ?: IOException(message, this)
        return HttpDataSourceException.createForIOException(ioException, dataSpec, type)
    }

    private companion object {
        const val HTTP_OK = 200
        const val HTTP_RANGE_NOT_SATISFIABLE = 416
        const val RANGE_HEADER = "Range"
        const val CONTENT_LENGTH_HEADER = "Content-Length"
        const val CONTENT_RANGE_HEADER = "Content-Range"
        val CONTENT_RANGE_SIZE_REGEX = Regex("bytes \\*/(\\d+)", RegexOption.IGNORE_CASE)
    }
}
