package com.yandex.div.svg

import android.graphics.drawable.PictureDrawable
import java.util.WeakHashMap

/**
 * A simple cache manager for storing downloaded svg images
 */
internal class SvgCacheManager {

    private val cache = WeakHashMap<String, PictureDrawable>()

    fun get(imageUrl: String) : PictureDrawable? {
        return cache[imageUrl]
    }

    fun set(imageUrl: String, pictureDrawable: PictureDrawable) {
        cache[imageUrl] = pictureDrawable
    }
}
