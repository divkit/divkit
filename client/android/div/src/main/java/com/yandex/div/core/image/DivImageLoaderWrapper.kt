package com.yandex.div.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.internal.util.makeIf
import com.yandex.div.svg.SvgDivImageLoader
import javax.inject.Inject
import kotlin.math.max

/**
 * A wrapper over DivImageLoader.
 * Applies modifiers to image URL and
 * replaces chosen image loader by SVG Image Loader if image loader doesn't support svg images.
 * Downscale the uploaded image if the loader allows it.
 */
@DivScope
@InternalApi
class DivImageLoaderWrapper @Inject constructor(
    private val providedImageLoader: DivImageLoader,
    divContext: Context,
) : DivImageLoader {
    private val appContext = divContext.applicationContext

    private val modifiers: List<DivImageUrlModifier> = listOf(
        DivImageAssetUrlModifier(),
    )

    private val svgImageLoader: SvgDivImageLoader? =
        makeIf(!providedImageLoader.hasSvgSupport()) { SvgDivImageLoader(appContext) }

    private val maxDisplaySize = divContext.resources.displayMetrics.let {
        max(it.widthPixels, it.heightPixels)
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        val loader = getProperLoader(modifiedUrl)
        val wrappedCallback = createLimitImageBitmapSizeCallback(loader, callback)
        return loader.loadImage(modifiedUrl, wrappedCallback)
    }

    override fun loadAnimatedImage(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadAnimatedImage(modifiedUrl, callback)
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

    private fun createLimitImageBitmapSizeCallback(
        loader: DivImageLoader,
        callback: DivImageDownloadCallback,
    ) = if (loader.needLimitBitmapSize()) {
        CallbackWrapper(callback, appContext)
    } else {
        callback
    }

    private inner class CallbackWrapper(
        private val callback: DivImageDownloadCallback,
        private val context: Context,
    ) : DivImageDownloadCallback() {

        override fun onSuccess(cachedImage: DivCachedImage) {
            callback.onSuccess(cachedImage.scale())
        }

        override fun onSuccess(cachedBitmap: CachedBitmap) {
            callback.onSuccess(cachedBitmap.scale())
        }

        override fun onSuccess(drawable: Drawable) {
            callback.onSuccess(drawable.scale())
        }

        override fun onSuccess(pictureDrawable: PictureDrawable) {
            callback.onSuccess(pictureDrawable)
        }

        override fun onError(e: Throwable?) {
            callback.onError(e)
        }

        override fun onScheduling() {
            callback.onScheduling()
        }

        override fun getAdditionalLogInfo(): String? = callback.additionalLogInfo

        private fun DivCachedImage.scale(): DivCachedImage {
            return when (this) {
                is DivCachedImage.Bitmap ->
                    if (bitmap.isLargeSize) DivCachedImage.Bitmap(bitmap.scale(), from) else this
                is DivCachedImage.Drawable ->
                    if (drawable.isLargeSize) DivCachedImage.Drawable(drawable.scale(), from) else this
            }
        }

        private val Bitmap.isLargeSize: Boolean
            get() = width > maxDisplaySize || height > maxDisplaySize

        private val Drawable.isLargeSize: Boolean
            get() = intrinsicWidth > maxDisplaySize || intrinsicHeight > maxDisplaySize

        private fun CachedBitmap.scale() =
            if (bitmap.isLargeSize) CachedBitmap(bitmap.scale(), bytes, cacheUri, from) else this

        private fun Bitmap.scale(): Bitmap {
            val scale = maxDisplaySize.toFloat() / max(width, height)
            return scale((width * scale).toInt(), (height * scale).toInt())
        }

        private fun Drawable.scale(): Drawable {
            if (!isLargeSize) return this

            val width = intrinsicWidth
            val height = intrinsicHeight

            val scale = maxDisplaySize.toFloat() / max(width, height)

            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            setBounds(0, 0, width, height)
            draw(canvas)

            return bitmap.scale(newWidth, newHeight).toDrawable(context.resources)
        }
    }
}
