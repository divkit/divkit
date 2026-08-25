package com.yandex.div.network

import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(InternalCoroutinesApi::class)
public class OkHttpDivNetworkClient(
    private val callFactory: Call.Factory,
) : DivNetworkClient {
    override suspend fun execute(request: DivNetworkRequest): DivNetworkResponse {
        val mediaType = request.contentType?.toMediaTypeOrNull()
        val requestBody = request.body
        val body = when {
            requestBody != null -> requestBody.toRequestBody(mediaType)
            request.method.uppercase() in METHODS_REQUIRING_BODY -> ByteArray(0).toRequestBody(mediaType)
            else -> null
        }
        val okHttpRequest = Request.Builder()
            .url(request.url)
            .method(request.method, body)
            .apply { request.headers.forEach { addHeader(it.name, it.value) } }
            .build()
        val call = callFactory.newCall(okHttpRequest)
        val job = coroutineContext[Job]
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val networkResponse = OkHttpDivNetworkResponse(response, call)
                    val cancellationHandle = job?.invokeOnCompletion(
                        onCancelling = true,
                        invokeImmediately = true,
                    ) { cause ->
                        if (cause != null) networkResponse.cancel()
                    }
                    networkResponse.attachCancellationHandle(cancellationHandle)
                    continuation.resume(networkResponse) { _, response, _ -> response.close() }
                }

                override fun onFailure(call: Call, e: java.io.IOException) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    private companion object {
        val METHODS_REQUIRING_BODY = setOf("POST", "PUT", "PATCH", "PROPPATCH", "REPORT")
    }
}

private class InputStreamResponseBody(
    private val stream: InputStream,
) : DivNetworkResponseBody {
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int = stream.read(buffer, offset, length)
    override fun skip(byteCount: Long): Long = stream.skip(byteCount)
    override fun close() = stream.close()
}

private class OkHttpDivNetworkResponse(
    private val response: Response,
    private val call: Call,
) : DivNetworkResponse {
    private val closed = AtomicBoolean()
    private val cancellationHandle = AtomicReference<DisposableHandle?>()

    override val url: String get() = response.request.url.toString()
    override val code: Int get() = response.code
    override val contentType: String? get() = response.body?.contentType()?.toString()
    override val body: DivNetworkResponseBody? get() = response.body?.byteStream()?.let(::InputStreamResponseBody)
    override fun headers(name: String): List<String> = response.headers.values(name)
    override fun close() {
        if (closed.compareAndSet(false, true)) {
            cancellationHandle.getAndSet(null)?.dispose()
            response.close()
        }
    }

    fun attachCancellationHandle(handle: DisposableHandle?) {
        if (handle == null) return
        if (!cancellationHandle.compareAndSet(null, handle) || closed.get()) {
            cancellationHandle.compareAndSet(handle, null)
            handle.dispose()
        }
    }

    fun cancel() {
        call.cancel()
        close()
    }
}

