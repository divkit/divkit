package com.yandex.div.lottie

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.internal.extensions.lottie.LottieData
import java.util.concurrent.ConcurrentHashMap

internal class DivLottieCompositionRepository(
    private val networkCache: DivLottieNetworkCache,
    private val logger: DivLottieLogger,
) {

    private val inlineParseLocks = ConcurrentHashMap<String, Any>()

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
            if (!supported) {
                logger.fail(
                    "Lottie preloading works unstable. Implement DivLottieNetworkCache.cacheComposition(String, onComplete)."
                )
                networkCache.cacheComposition(url.toString())
                onComplete(UriPreloadResult(url, null))
            }
        } else {
            onComplete(UriPreloadResult(url, null))
        }
    }

    private fun getComposition(context: Context, url: String): LottieResult<LottieComposition> {
        return if (url.toUri().isHttp) {
            networkCache.loadCached(url)
                ?.let { LottieCompositionFactory.fromJsonStringSync(it, url) }
                ?: LottieCompositionFactory.fromUrlSync(context, url, url)
        } else {
            LottieResult(IllegalArgumentException("Failed to retrieve lottie json from $url"))
        }
    }
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
