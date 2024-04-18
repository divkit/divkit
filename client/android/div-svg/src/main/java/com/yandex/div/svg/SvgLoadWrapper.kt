package com.yandex.div.svg

import android.widget.ImageView
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference

/**
 * A wrapper over DivImageLoader.
 * Replaces chosen image loader by SVG Image Loader if image loader doesn't support svg images.
 */
@InternalApi
public class SvgLoadWrapper(
    private val providedImageLoader: DivImageLoader,
) : DivImageLoader {
    private val svgImageLoader: SvgDivImageLoader? = if (!providedImageLoader.hasSvgSupport()) {
        SvgDivImageLoader()
    } else {
        null
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        return getProperLoader(imageUrl).loadImage(imageUrl, callback)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        return getProperLoader(imageUrl).loadImage(imageUrl, imageView)
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        return getProperLoader(imageUrl).loadImageBytes(imageUrl, callback)
    }

    private fun getProperLoader(imageUrl: String) : DivImageLoader {
        return if (svgImageLoader != null && isSvg(imageUrl)) {
            svgImageLoader
        } else {
            providedImageLoader
        }
    }

    private fun isSvg(imageUrl: String): Boolean {
        val queryStartIndex = imageUrl.indexOf('?')
        val pathEndIndex = if (queryStartIndex == -1) imageUrl.length else queryStartIndex
        return imageUrl.substring(0, pathEndIndex).endsWith(".svg")
    }
}
