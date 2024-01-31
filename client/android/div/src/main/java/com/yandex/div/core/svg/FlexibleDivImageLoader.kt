package com.yandex.div.core.svg

import android.widget.ImageView
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import javax.inject.Inject

/**
 * A wrapper over DivImageLoader.
 * Replaces chosen image loader by SVG Image Loader if image loader doesn't support svg images.
 */
@Mockable
class FlexibleDivImageLoader @Inject constructor(
    private val providedImageLoader: DivImageLoader,
    private val svgImageLoader: Lazy<SvgDivImageLoader>
) : DivImageLoader {

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val overriddenImageLoader = getSvgImageLoaderIfNeeded(imageUrl)
        return overriddenImageLoader.loadImage(imageUrl, callback)
    }

    override fun loadImage(
        imageUrl: String,
        shouldBeRasterized: Boolean,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val overriddenImageLoader = getSvgImageLoaderIfNeeded(imageUrl)
        return overriddenImageLoader.loadImage(imageUrl, shouldBeRasterized, callback)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val overriddenImageLoader = getSvgImageLoaderIfNeeded(imageUrl)
        return overriddenImageLoader.loadImage(imageUrl, imageView)
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val overriddenImageLoader = getSvgImageLoaderIfNeeded(imageUrl)
        return overriddenImageLoader.loadImageBytes(imageUrl, callback)
    }

    private fun getSvgImageLoaderIfNeeded(imageUrl: String) : DivImageLoader {
        return if (shouldCreateSvgImageLoader(imageUrl)) svgImageLoader.value else providedImageLoader
    }

    private fun shouldCreateSvgImageLoader(imageUrl: String) : Boolean {
        return isSvg(imageUrl) && !providedImageLoader.doesSupportSvg()
    }

    private fun isSvg(imageUrl: String): Boolean {
        val queryStartIndex = imageUrl.indexOf('?')
        val pathEndIndex = if (queryStartIndex == -1) imageUrl.length else queryStartIndex
        return imageUrl.substring(0, pathEndIndex).endsWith(".svg")
    }
}