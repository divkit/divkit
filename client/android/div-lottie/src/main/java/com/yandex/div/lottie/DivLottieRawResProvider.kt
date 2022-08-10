package com.yandex.div.lottie

import androidx.annotation.RawRes
import com.airbnb.lottie.LottieDrawable
import com.yandex.div2.DivGifImage

/**
 * Implement this, if you planning on showing lottie jsons stored in /res/raw folder or in assets.
 */
interface DivLottieRawResProvider {

    /**
     * @param url with scheme [RES_SCHEME], that was in lottie extension under [EXTENSION_PARAM_URL] param.
     * @return raw res, or null if there isn't any. On null [DivLottieExtensionHandler] will
     * use [DivGifImage.gifUrl] as fallback.
     */
    @RawRes
    fun provideRes(url: String): Int?
    /**
     * Please, consider using [provideRes] for local lottie animations, as it uses a hard reference to R.
     * @param url with scheme [ASSET_SCHEME], that was in lottie extension under [EXTENSION_PARAM_URL] param.
     * @return raw res, or null if there isn't any. On null [DivLottieExtensionHandler] will
     * use [DivGifImage.gifUrl] as fallback.
     */
    fun provideAssetFile(url: String): String?

    /**
     * @see [LottieDrawable.imageAssetsFolder].
     */
    fun provideAssetFolder(): String? = "/"

    companion object {
        const val HTTP_SCHEME = "http"
        const val HTTPS_SCHEME = "https"
        const val RES_SCHEME = "res"
        const val ASSET_SCHEME = "asset"

        val STUB = object : DivLottieRawResProvider {
            override fun provideRes(url: String): Int? = null
            override fun provideAssetFile(url: String): String? = null
        }
    }
}
