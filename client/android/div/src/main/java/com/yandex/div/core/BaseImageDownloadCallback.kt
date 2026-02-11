package com.yandex.div.core

import android.graphics.Bitmap
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback

internal abstract class BaseImageDownloadCallback : DivImageDownloadCallback() {

    override fun onSuccess(cachedImage: DivCachedImage) {
        when (cachedImage) {
            is DivCachedImage.Bitmap -> onSuccess(cachedImage.bitmap, cachedImage.from)
            is DivCachedImage.Drawable -> dispatchDrawable(cachedImage.drawable, cachedImage.from)
        }
    }

    @Deprecated("Use onSuccess(DivCachedImage)")
    override fun onSuccess(cachedBitmap: CachedBitmap) = onSuccess(cachedBitmap.bitmap, cachedBitmap.from)

    @Deprecated("Use onSuccess(DivCachedImage)")
    override fun onSuccess(drawable: Drawable) = dispatchDrawable(drawable, BitmapSource.MEMORY)

    @Deprecated("Use onSuccess(DivCachedImage)")
    override fun onSuccess(pictureDrawable: PictureDrawable) = onSuccess(pictureDrawable, BitmapSource.MEMORY)

    protected abstract fun onSuccess(bitmap: Bitmap, source: BitmapSource)

    protected abstract fun onSuccess(drawable: Drawable, source: BitmapSource)

    protected open fun onSuccess(pictureDrawable: PictureDrawable, source: BitmapSource) =
        onSuccess(pictureDrawable as Drawable, source)

    @RequiresApi(Build.VERSION_CODES.P)
    protected open fun onSuccess(animatedDrawable: AnimatedImageDrawable, source: BitmapSource) =
        onSuccess(animatedDrawable as Drawable, source)

    protected open fun dispatchDrawable(drawable: Drawable, source: BitmapSource) {
        when (drawable) {
            is BitmapDrawable -> onSuccess(drawable.bitmap, source)
            is PictureDrawable -> onSuccess(drawable, source)
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable is AnimatedImageDrawable) {
                    onSuccess(drawable, source)
                } else {
                    onSuccess(drawable, source)
                }
            }
        }
    }
}
