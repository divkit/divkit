package com.yandex.div.core.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.CachedBitmap
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
        override fun onSuccess(cachedBitmap: CachedBitmap) {
            if (cachedBitmap.isLargeSize) {
                callback.onSuccess(cachedBitmap.scale())
            } else {
                callback.onSuccess(cachedBitmap)
            }
        }

        override fun onSuccess(drawable: Drawable) {
            if (drawable.isLargeSize) {
                callback.onSuccess(drawable.scale())
            } else {
                callback.onSuccess(drawable)
            }
        }

        override fun onSuccess(pictureDrawable: PictureDrawable) {
            callback.onSuccess(pictureDrawable)
        }

        override fun onError() {
            callback.onError()
        }

        override fun onCancel() {
            callback.onCancel()
        }

        override fun onScheduling() {
            callback.onScheduling()
        }

        override fun getAdditionalLogInfo(): String? = callback.additionalLogInfo

        private val CachedBitmap.isLargeSize: Boolean
            get() = bitmap.width > maxDisplaySize || bitmap.height > maxDisplaySize

        private val Drawable.isLargeSize: Boolean
            get() = intrinsicWidth > maxDisplaySize || intrinsicHeight > maxDisplaySize

        private fun CachedBitmap.scale(): CachedBitmap {
            val width = bitmap.width
            val height = bitmap.height

            val scale = maxDisplaySize.toFloat() / max(width, height)

            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            val scaledBitmap = bitmap.scale(newWidth, newHeight)

            return CachedBitmap(scaledBitmap, bytes, cacheUri, from)
        }

        private fun Drawable.scale(): Drawable {
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
