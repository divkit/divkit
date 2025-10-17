package com.yandex.div.core.image

import android.content.Context
import android.widget.ImageView
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.internal.util.makeIf
import com.yandex.div.svg.SvgDivImageLoader
import javax.inject.Inject

/**
 * A wrapper over DivImageLoader.
 * Applies modifiers to image URL and
 * replaces chosen image loader by SVG Image Loader if image loader doesn't support svg images.
 */
@DivScope
@InternalApi
class DivImageLoaderWrapper @Inject constructor(
    private val providedImageLoader: DivImageLoader,
    divContext: Context,
) : DivImageLoader {
    private val modifiers: List<DivImageUrlModifier> = listOf(
        DivImageAssetUrlModifier(),
    )

    private val svgImageLoader: SvgDivImageLoader? =
        makeIf(!providedImageLoader.hasSvgSupport()) { SvgDivImageLoader(divContext) }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadImage(modifiedUrl, callback)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadImage(modifiedUrl, imageView)
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadImageBytes(modifiedUrl, callback)
    }

    private fun getModifiedUrl(initialUrl: String): String {
        var url = initialUrl
        modifiers.forEach { modifier ->
            url = modifier.modifyImageUrl(url)
        }
        return url
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
        val pathEndIndex = if (queryStartIndex < 0) imageUrl.length else queryStartIndex
        return imageUrl.substring(0, pathEndIndex).endsWith(".svg")
    }
}
