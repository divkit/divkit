package com.yandex.div.core.image

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import javax.inject.Inject

/**
 * A wrapper over DivImageLoader.
 * Applies modifiers to image URL and
 * replaces chosen image loader by SVG Image Loader if image loader doesn't support svg images.
 */
@DivScope
internal class DivImageLoaderWrapper @Inject constructor(private val loader: DivImageLoader) : DivImageLoader {

    private val modifiers: List<DivImageUrlModifier> = listOf(
        DivImageAssetUrlModifier(),
    )

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback) =
        loader.loadImage(imageUrl.modify(), callback)

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback) =
        loader.loadImageBytes(imageUrl.modify(), callback)

    private fun String.modify(): String {
        var url = this
        modifiers.forEach { url = it.modifyImageUrl(url) }
        return url
    }
}
