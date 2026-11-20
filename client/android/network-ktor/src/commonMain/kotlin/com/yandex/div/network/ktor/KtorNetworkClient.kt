package com.yandex.div.network.ktor

import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.BadContentTypeFormatException
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.parseUrl
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.cancel
import io.ktor.utils.io.discard
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException

/**
 * [DivNetworkClient] backed by a Ktor [HttpClient].
 *
 * [execute] returns as soon as the status and headers arrive. The body is streamed from the
 * connection, and a read blocks the calling thread until the engine delivers data. DivKit reads
 * bodies on `Dispatchers.IO`, so the engine needs a dispatcher of its own: an engine on
 * `Dispatchers.IO` stops delivering data once blocked reads occupy all of its threads. The
 * `KtorNetworkClient(engineFactory)` function creates a client with such a dispatcher. A client
 * passed to this constructor needs one too, for example
 * `engine { dispatcher = Dispatchers.IO.limitedParallelism(64) }`. Any failure of the body,
 * including a timeout, is thrown from the read as an [IOException]. Closing the response, or
 * cancelling the coroutine that called [execute], releases the connection on a background thread.
 *
 * Redirects, timeouts and retries follow the [HttpClient] configuration. The response URL is the
 * URL of the last request Ktor executed, so redirects that the engine follows itself are not
 * reflected in it. Responses with any status code are returned: the `expectSuccess` validation is
 * disabled for these requests, while custom response validators still apply. Request headers that
 * Ktor reserves for the engine, such as `Transfer-Encoding`, are not forwarded.
 */
public class KtorNetworkClient(
    private val httpClient: HttpClient,
) : DivNetworkClient {

    private val callScope = CoroutineScope(SupervisorJob())

    @OptIn(InternalAPI::class, InternalCoroutinesApi::class)
    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        val url = request.url.toHttpUrl()
        val statement = httpClient.prepareRequest {
            url(url)
            method = HttpMethod.parse(request.method)
            expectSuccess = false
            request.headers.forEach { header ->
                if (!HttpHeaders.isUnsafe(header.name)) {
                    headers.append(header.name.canonicalHeaderName(), header.value)
                }
            }
            val body = request.body
            if (body != null) {
                setBody(ByteArrayContent(body, request.contentType?.toContentTypeOrNull()))
            }
        }
        val received = CompletableDeferred<KtorNetworkResponse>()
        val call = callScope.async {
            statement.execute { response ->
                val callJob = currentCoroutineContext().job
                received.complete(KtorNetworkResponse(response, response.rawContent, callJob))
                awaitCancellation()
            }
        }
        call.invokeOnCompletion { cause -> cause?.let(received::completeExceptionally) }
        val response = try {
            received.await()
        } catch (e: CancellationException) {
            call.cancelInBackground()
            throw e
        }
        val cancellationHandle = currentCoroutineContext()[Job]?.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
        ) { cause ->
            if (cause != null) response.close()
        }
        if (cancellationHandle != null) {
            call.invokeOnCompletion { cancellationHandle.dispose() }
        }
        return response
    }
}

/**
 * Creates a [KtorNetworkClient] with a new [HttpClient] on [engineFactory], configured by [block].
 *
 * The engine runs on a dispatcher of its own, so body reads that block `Dispatchers.IO` threads
 * do not stop it from delivering data. Setting the engine dispatcher in [block] replaces it.
 */
public fun <T : HttpClientEngineConfig> KtorNetworkClient(
    engineFactory: HttpClientEngineFactory<T>,
    block: HttpClientConfig<T>.() -> Unit = {},
): KtorNetworkClient {
    val httpClient = HttpClient(engineFactory) {
        engine { dispatcher = engineDispatcher }
        block()
    }
    return KtorNetworkClient(httpClient)
}

private const val ENGINE_PARALLELISM = 64

private val engineDispatcher = Dispatchers.IO.limitedParallelism(ENGINE_PARALLELISM)

private val cancellationScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

private fun Job.cancelInBackground() {
    cancellationScope.launch { cancel() }
}

private fun String.toHttpUrl(): Url {
    val url = parseUrl(this)?.takeIf {
        startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)
    }
    require(url != null) { "Expected an absolute http or https URL: $this" }
    return url
}

private fun String.canonicalHeaderName(): String {
    return if (equals(HttpHeaders.Cookie, ignoreCase = true)) HttpHeaders.Cookie else this
}

private fun String.toContentTypeOrNull(): ContentType? {
    if (isBlank()) return null
    return try {
        ContentType.parse(this)
    } catch (ignored: BadContentTypeFormatException) {
        null
    }
}

private class KtorNetworkResponse(
    response: HttpResponse,
    channel: ByteReadChannel,
    private val callJob: Job,
) : DivNetworkResponse {
    private val responseHeaders: Headers = response.headers

    override val url: String = response.request.url.toString()
    override val code: Int = response.status.value
    override val contentType: String? = responseHeaders[HttpHeaders.ContentType]
    override val body: DivNetworkResponseBody = ChannelResponseBody(channel)

    override fun headers(name: String): List<String> = responseHeaders.getAll(name).orEmpty()

    override fun close() {
        body.close()
        callJob.cancelInBackground()
    }
}

private class ChannelResponseBody(private val channel: ByteReadChannel) : DivNetworkResponseBody {
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        require(offset >= 0 && length >= 0 && offset <= buffer.size - length)
        if (length == 0) return 0
        val count = readBlocking { channel.readAvailable(buffer, offset, length) }
        if (count < 0) throwIfFailed()
        return count
    }

    override fun skip(byteCount: Long): Long {
        require(byteCount >= 0)
        val count = readBlocking { channel.discard(byteCount) }
        if (count < byteCount) throwIfFailed()
        return count
    }

    private fun <T> readBlocking(block: suspend () -> T): T {
        return try {
            runBlocking { block() }
        } catch (e: CancellationException) {
            throw IOException(e.message, e)
        }
    }

    private fun throwIfFailed() {
        when (val cause = channel.closedCause) {
            null -> Unit
            is IOException -> throw cause
            else -> throw IOException(cause.message, cause)
        }
    }

    override fun close() = channel.cancel()
}
