package com.yandex.div.compose.internal

import coil3.ImageLoader
import com.yandex.div.core.annotations.InternalApi

/**
 * Provides an [ImageLoader] that is used to load images inside [com.yandex.div.compose.DivView]s.
 *
 * This interface is intended for testing purposes only. You should not implement it in the
 * production environment.
 *
 * @see DivDebugConfiguration
 */
@InternalApi
fun interface ImageLoaderProvider {
    fun provide(): ImageLoader
}
