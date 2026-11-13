package com.yandex.div.compose.images

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.allowHardware
import com.yandex.div.compose.di.DivContextScope
import com.yandex.div.internal.coil.GifDecoderFactory
import com.yandex.div.internal.coil.svg.addSvgDecoderFactoryIfAvailable
import dev.zacsweers.metro.Inject

@DivContextScope
internal class ImageLoaderFactory @Inject constructor(
    private val context: PlatformContext,
    private val imageLoaderConfiguration: ImageLoaderConfiguration
) {

    fun createImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .allowHardware(false)
            .components {
                imageLoaderConfiguration.applyComponents(this)
                add(DivkitAssetUriMapper())
                add(GifDecoderFactory)
                addSvgDecoderFactoryIfAvailable(context)
                add(UnthrottledImageDecoderFactory)
            }
            .eventListener(imageLoaderConfiguration.eventListener)
            .build()
    }
}
