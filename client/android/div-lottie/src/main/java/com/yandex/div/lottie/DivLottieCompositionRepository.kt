package com.yandex.div.lottie

import android.content.Context
import android.net.Uri
import android.util.LruCache
import androidx.core.net.toUri
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkResponse
import com.yandex.div.core.network.DivNetworkResponseBody
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.internal.extensions.lottie.LottieData
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.ZipInputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

internal class DivLottieCompositionRepository(
    private val networkCache: DivLottieNetworkCache,
    private val logger: DivLottieLogger,
    private val networkClient: DivNetworkClient? = null,
    private val networkScope: CoroutineScope,
) {

    private val inlineParseLocks = ConcurrentHashMap<String, Any>()
    private val compositionCache = LruCache<String, LottieComposition>(COMPOSITION_CACHE_SIZE)

    internal fun receiveLottieComposition(
        data: LottieData,
        context: Context
    ): LottieResult<LottieComposition> {
        val result = when (data) {
            is LottieData.Asset ->
                LottieCompositionFactory.fromAssetSync(context, data.assetName)

            is LottieData.Json -> return receiveInlineComposition(data)

            is LottieData.RawRes ->
                LottieCompositionFactory.fromRawResSync(context, data.id, data.url)

            is LottieData.Url -> getComposition(context, data.url)
        }
        result.value?.decodeBase64Images(logger)
        return result
    }

    internal suspend fun receiveLottieCompositionAsync(
        data: LottieData,
        context: Context,
    ): LottieResult<LottieComposition> {
        if (data !is LottieData.Url) return receiveLottieComposition(data, context)
        val result = getCompositionAsync(context, data.url)
        result.value?.decodeBase64Images(logger)
        return result
    }

    private fun receiveInlineComposition(data: LottieData.Json): LottieResult<LottieComposition> {
        // Concurrent requests for the same inline JSON serialize on a per-key lock:
        // the first one parses and predecodes images, the rest hit the composition cache
        // inside fromJsonStringSync and observe already decoded bitmaps.
        synchronized(inlineParseLocks.getOrPut(data.cacheKey) { Any() }) {
            val result = LottieCompositionFactory.fromJsonStringSync(data.json, data.cacheKey)
            result.value?.decodeBase64Images(logger)
            return result
        }
    }

    internal fun preloadInlineComposition(
        data: LottieData.Json,
        onComplete: (PreloadResult) -> Unit
    ) {
        val result = try {
            receiveInlineComposition(data)
        } catch (e: Exception) {
            logger.fail("Failed to preload inline lottie composition.", e)
            LottieResult<LottieComposition>(e)
        }
        onComplete(UriPreloadResult(data.inlinePreloadUri, result.exception))
    }

    internal fun preloadLottieComposition(url: Uri, onComplete: (PreloadResult) -> Unit) {
        if (url.isHttp) {
            val supported = networkCache.cacheComposition(url.toString()) { error ->
                onComplete(UriPreloadResult(url, error))
            }
            if (supported) return
            val client = networkClient
            if (client != null) {
                preloadComposition(client, url, onComplete)
                return
            }
            logger.fail(
                "Lottie preloading works unstable. Implement DivLottieNetworkCache.cacheComposition(String, onComplete)."
            )
            networkCache.cacheComposition(url.toString())
            onComplete(UriPreloadResult(url, null))
        } else {
            onComplete(UriPreloadResult(url, null))
        }
    }

    private fun getComposition(context: Context, url: String): LottieResult<LottieComposition> {
        return if (url.toUri().isHttp) {
            getCachedComposition(url)
                ?.let { LottieResult(it) }
                ?: networkCache.loadCached(url)
                ?.let { cacheComposition(url, LottieCompositionFactory.fromJsonStringSync(it, url)) }
                ?: LottieCompositionFactory.fromUrlSync(context, url, url)
        } else {
            LottieResult(IllegalArgumentException("Failed to retrieve lottie json from $url"))
        }
    }

    private suspend fun getCompositionAsync(
        context: Context,
        url: String,
    ): LottieResult<LottieComposition> {
        if (!url.toUri().isHttp) {
            return LottieResult(IllegalArgumentException("Failed to retrieve lottie json from $url"))
        }
        return getCachedComposition(url)
            ?.let { LottieResult(it) }
            ?: networkCache.loadCached(url)
                ?.let { cacheComposition(url, LottieCompositionFactory.fromJsonStringSync(it, url)) }
            ?: networkClient?.let { fetchComposition(it, url) }
            ?: LottieCompositionFactory.fromUrlSync(context, url, url)
    }

    private fun preloadComposition(
        networkClient: DivNetworkClient,
        url: Uri,
        onComplete: (PreloadResult) -> Unit,
    ) {
        val urlString = url.toString()
        getCachedComposition(urlString)?.let {
            onComplete(UriPreloadResult(url, null))
            return
        }
        networkCache.loadCached(urlString)?.let {
            val result = cacheComposition(
                urlString,
                LottieCompositionFactory.fromJsonStringSync(it, urlString),
            )
            onComplete(UriPreloadResult(url, result.exception))
            return
        }
        val completed = AtomicBoolean(false)
        val job = networkScope.launch {
            val error = fetchComposition(networkClient, urlString).exception
            if (completed.compareAndSet(false, true)) {
                onComplete(UriPreloadResult(url, error))
            }
        }
        job.invokeOnCompletion { cause ->
            if (cause != null && completed.compareAndSet(false, true)) {
                onComplete(UriPreloadResult(url, cause))
            }
        }
    }

    private suspend fun fetchComposition(
        networkClient: DivNetworkClient,
        url: String,
    ): LottieResult<LottieComposition> {
        return runCatching {
            networkClient.execute(DivNetworkRequest.Builder(url).build()).use { response ->
                cacheComposition(url, parseResponse(response, url))
            }
        }.onFailure {
            if (it is CancellationException) throw it
        }.fold(
            onSuccess = { it },
            onFailure = { LottieResult(it) },
        )
    }

    @Synchronized
    private fun getCachedComposition(url: String): LottieComposition? = compositionCache[url]

    @Synchronized
    private fun cacheComposition(
        url: String,
        result: LottieResult<LottieComposition>,
    ): LottieResult<LottieComposition> {
        result.value?.let { compositionCache.put(url, it) }
        return result
    }

    private fun parseResponse(response: DivNetworkResponse, url: String): LottieResult<LottieComposition> {
        if (!response.isSuccessful) throw IOException("Server response code ${response.code}")
        val body = response.body ?: throw IOException("No response body received")
        val isZip = response.url.hasZipExtension() || url.hasZipExtension() ||
            response.contentType?.contains("zip", ignoreCase = true) == true
        return if (isZip) {
            LottieCompositionFactory.fromZipStreamSync(ZipInputStream(body.asInputStream()), url)
        } else {
            LottieCompositionFactory.fromJsonInputStreamSync(body.asInputStream(), url)
        }
    }
}

private fun String.hasZipExtension(): Boolean {
    val path = substringBefore("#").substringBefore("?")
    return path.endsWith(".zip", ignoreCase = true) || path.endsWith(".lottie", ignoreCase = true)
}

private fun DivNetworkResponseBody.asInputStream(): InputStream = object : InputStream() {
    private val singleByte = ByteArray(1)

    override fun read(): Int {
        return when (read(singleByte, 0, 1)) {
            -1 -> -1
            else -> singleByte[0].toInt() and 0xff
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return this@asInputStream.read(buffer, offset, length)
    }

    override fun skip(byteCount: Long): Long =
        if (byteCount <= 0) 0 else this@asInputStream.skip(byteCount)

    override fun close() = Unit
}

internal val LottieData.Json.cacheKey: String
    get() = Integer.toHexString(json.hashCode())

internal val LottieData.Json.inlinePreloadUri: Uri
    get() = "$INLINE_PRELOAD_SCHEME://$cacheKey".toUri()

private val Uri.isHttp: Boolean
    get() {
        return when (scheme) {
            "http", "https" -> true
            else -> false
        }
    }

private const val INLINE_PRELOAD_SCHEME = "lottie-json"
private const val COMPOSITION_CACHE_SIZE = 20
