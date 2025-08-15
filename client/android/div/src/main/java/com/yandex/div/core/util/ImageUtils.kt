package com.yandex.div.core.util

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.CachedBitmap

internal fun PictureDrawable.toCachedBitmap(imageUrl: Uri, bytes: ByteArray? = null) : CachedBitmap {
    return CachedBitmap(toBitmap(), bytes, imageUrl, BitmapSource.MEMORY)
}
