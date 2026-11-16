package com.yandex.div.compose.images

import coil3.ComponentRegistry
import coil3.ImageLoader

internal interface PlatformImageLoaderConfiguration {
    fun createImageLoaderBuilder(): ImageLoader.Builder
    fun applyComponents(builder: ComponentRegistry.Builder)
}
