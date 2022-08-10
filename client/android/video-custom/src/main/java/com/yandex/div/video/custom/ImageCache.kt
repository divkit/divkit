package com.yandex.div.video.custom

import android.graphics.Bitmap

interface ImageCache {
    fun getImageBitmap(url: String): Bitmap?

    fun storeImage(url: String, bitmap: Bitmap)
}
