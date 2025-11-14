package com.yandex.div.video.custom

import android.graphics.Bitmap

@Deprecated("Use div.video.m3 package")
interface ImageCache {
    fun getImageBitmap(url: String): Bitmap?

    fun storeImage(url: String, bitmap: Bitmap)
}
