package com.yandex.div.compose.images

import coil3.network.NetworkClient
import coil3.network.NetworkHeaders
import coil3.network.NetworkRequest
import coil3.network.NetworkResponse
import coil3.network.NetworkResponseBody
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.ByteArrayContent
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.cancel
import okio.Buffer
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

@DivContextScope
@Inject
internal class KtorImageNetworkClient(private val httpClient: HttpClient) : NetworkClient {

    override suspend fun <T> executeRequest(
        request: NetworkRequest,
        block: suspend (response: NetworkResponse) -> T,
    ): T {
        val requestBody = request.body?.let { body ->
            Buffer().also { body.writeTo(it) }.readByteArray()
        }
        val statement = httpClient.prepareRequest {
            url(request.url)
            method = HttpMethod.parse(request.method)
            request.headers.asMap().forEach { (name, values) ->
                values.forEach { headers.append(name.canonicalHeaderName(), it) }
            }
            if (requestBody != null) {
                setBody(ByteArrayContent(requestBody))
            }
        }
        return statement.execute { response ->
            var consumed = false
            try {
                block(response.toNetworkResponse()).also { consumed = true }
            } finally {
                if (!consumed) response.cancel()
            }
        }
    }
}

@OptIn(InternalAPI::class)
private fun HttpResponse.toNetworkResponse(): NetworkResponse {
    return NetworkResponse(
        code = status.value,
        requestMillis = requestTime.timestamp,
        responseMillis = responseTime.timestamp,
        headers = headers.toNetworkHeaders(),
        body = ChannelResponseBody(rawContent),
        delegate = this,
    )
}

private fun String.canonicalHeaderName(): String {
    return if (equals(HttpHeaders.Cookie, ignoreCase = true)) HttpHeaders.Cookie else this
}

private fun Headers.toNetworkHeaders(): NetworkHeaders {
    val builder = NetworkHeaders.Builder()
    forEach { name, values -> values.forEach { builder.add(name, it) } }
    return builder.build()
}

private const val BUFFER_SIZE = 8 * 1024

private class ChannelResponseBody(private val channel: ByteReadChannel) : NetworkResponseBody {

    override suspend fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(BUFFER_SIZE)
        while (true) {
            val count = channel.readAvailable(buffer)
            if (count < 0) break
            sink.write(buffer, 0, count)
        }
        channel.closedCause?.let { throw it }
    }

    override suspend fun writeTo(fileSystem: FileSystem, path: Path) {
        fileSystem.sink(path).buffer().use { writeTo(it) }
    }

    override fun close() = channel.cancel()
}
