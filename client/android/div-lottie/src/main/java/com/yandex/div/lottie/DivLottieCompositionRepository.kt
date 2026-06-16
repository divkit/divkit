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

internal class DivLottieCompositionRepository(
    private val networkCache: DivLottieNetworkCache,
    private val logger: DivLottieLogger,
) {

    internal fun receiveLottieComposition(
        data: LottieData,
        context: Context
    ): LottieResult<LottieComposition> {
        return when (data) {
            is LottieData.Asset ->
                LottieCompositionFactory.fromAssetSync(context, data.assetName)

            is LottieData.Json ->
                LottieCompositionFactory.fromJsonStringSync(
                    data.json,
                    Integer.toHexString(data.json.hashCode())
                )

            is LottieData.RawRes ->
                LottieCompositionFactory.fromRawResSync(context, data.id, data.url)

            is LottieData.Url -> getComposition(context, data.url)
        }
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

private val Uri.isHttp: Boolean
    get() {
        return when (scheme) {
            "http", "https" -> true
            else -> false
        }
    }
