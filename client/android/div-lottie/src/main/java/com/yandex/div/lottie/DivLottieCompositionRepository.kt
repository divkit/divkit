package com.yandex.div.lottie

import android.content.Context
import android.net.Uri
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import com.yandex.div.lottie.DivLottieRawResProvider.Companion.ASSET_SCHEME
import com.yandex.div.lottie.DivLottieRawResProvider.Companion.DIVKIT_ASSET_SCHEME
import com.yandex.div.lottie.DivLottieRawResProvider.Companion.HTTPS_SCHEME
import com.yandex.div.lottie.DivLottieRawResProvider.Companion.HTTP_SCHEME
import com.yandex.div.lottie.DivLottieRawResProvider.Companion.RES_SCHEME
import org.json.JSONObject

internal class DivLottieCompositionRepository(
    private val rawResProvider: DivLottieRawResProvider,
    private val networkCache: DivLottieNetworkCache
) {

    private fun modifyLottieUrl(lottieUrl: String): String {
        val path = lottieUrl.removePrefix("${DIVKIT_ASSET_SCHEME}://")
        return if (path.startsWith(DEFAULT_ASSET_FOLDER)) {
            path
        } else {
            "$DEFAULT_ASSET_FOLDER${path.removePrefix("/")}"
        }
    }

    internal fun receiveLottieComposition(
        data: LottieData,
        context: Context
    ): LottieResult<LottieComposition> {
        return when (data) {
            is LottieData.External -> receiveExternalComposition(context, data.url)
            is LottieData.Embedded -> receiveEmbeddedComposition(data.json)
        }
    }

    internal fun preloadLottieComposition(url: Uri) {
        when (url.scheme) {
            HTTP_SCHEME, HTTPS_SCHEME -> {
                networkCache.cacheComposition(url.toString())
            }
        }
    }

    private fun receiveExternalComposition(context: Context, url: String): LottieResult<LottieComposition> {
        return when (Uri.parse(url).scheme) {
            HTTP_SCHEME, HTTPS_SCHEME -> {
                networkCache.loadCached(url)
                    ?.let { LottieCompositionFactory.fromJsonStringSync(it, url) }
                    ?: LottieCompositionFactory.fromUrlSync(context, url, url)
            }
            RES_SCHEME -> {
                val rawRes = rawResProvider.provideRes(url)
                if (rawRes == null) {
                    LottieResult(IllegalArgumentException("Failed to map $url to internal resource"))
                } else {
                    LottieCompositionFactory.fromRawResSync(context, rawRes, url)
                }
            }
            ASSET_SCHEME -> {
                val assetFileAddress = rawResProvider.provideAssetFile(url)
                if (assetFileAddress == null) {
                    LottieResult(IllegalArgumentException("Failed to map $url to internal resource"))
                } else {
                    LottieCompositionFactory.fromAssetSync(context, assetFileAddress)
                }
            }
            DIVKIT_ASSET_SCHEME -> {
                val assetFileAddress = modifyLottieUrl(url)
                LottieCompositionFactory.fromAssetSync(context, assetFileAddress)
            }
            else -> LottieResult(IllegalArgumentException("Failed to retrieve lottie json from $url"))
        }
    }

    companion object {
        private const val DEFAULT_ASSET_FOLDER = "divkit/"
    }

    @Suppress("DEPRECATION")
    private fun receiveEmbeddedComposition(json: JSONObject): LottieResult<LottieComposition> {
        return LottieCompositionFactory.fromJsonSync(json, Integer.toHexString(json.hashCode()))
    }
}
