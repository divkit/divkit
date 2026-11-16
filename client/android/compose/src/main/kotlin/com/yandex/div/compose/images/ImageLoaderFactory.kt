package com.yandex.div.compose.images

import coil3.ImageLoader
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
internal class ImageLoaderFactory @Inject constructor(
    private val externalImageLoaderConfiguration: ImageLoaderConfiguration,
    private val platformImageLoaderConfiguration: PlatformImageLoaderConfiguration,
) {

    fun createImageLoader(): ImageLoader {
        return platformImageLoaderConfiguration.createImageLoaderBuilder()
            .components {
                platformImageLoaderConfiguration.applyComponents(this)
                externalImageLoaderConfiguration.applyComponents(this)
            }
            .eventListener(externalImageLoaderConfiguration.eventListener)
            .build()
    }
}
