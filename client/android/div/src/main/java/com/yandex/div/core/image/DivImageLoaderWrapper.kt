package com.yandex.div.core.image

import android.widget.ImageView
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.svg.SvgLoadWrapper

/**
 * Wraps implementation of DivImageLoader from DivConfiguration.
 *
 * Modifies image_url from layout before providing it to wrapped DivImageLoader.
 */
internal class DivImageLoaderWrapper(
    providedImageLoader: DivImageLoader,
): DivImageLoader {
    private val imageLoader = SvgLoadWrapper(providedImageLoader)

    private val modifiers: List<DivImageUrlModifier> = listOf(
        DivImageAssetUrlModifier(),
    )

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
       return imageLoader.loadImage(getModifiedUrl(imageUrl), callback)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        return imageLoader.loadImage(getModifiedUrl(imageUrl), imageView)
    }

    override fun loadImageBytes(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        return imageLoader.loadImageBytes(getModifiedUrl(imageUrl), callback)
    }

    private fun getModifiedUrl(initialUrl: String): String {
        var url = initialUrl
        modifiers.forEach { modifier ->
            url = modifier.modifyImageUrl(url)
        }
        return url
    }
}
