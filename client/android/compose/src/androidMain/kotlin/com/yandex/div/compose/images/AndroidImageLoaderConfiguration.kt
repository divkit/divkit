package com.yandex.div.compose.images

import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.allowHardware
import com.yandex.div.compose.di.DivContextScope
import com.yandex.div.compose.di.Names
import com.yandex.div.internal.coil.GifDecoderFactory
import com.yandex.div.internal.coil.svg.LazySvgDecoderFactory
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named

@DivContextScope
internal class AndroidImageLoaderConfiguration @Inject constructor(
    private val context: PlatformContext,
    @Named(Names.MAX_DISPLAY_SIZE)
    private val maxDisplaySize: Int,
) : PlatformImageLoaderConfiguration {

    override fun createImageLoaderBuilder(): ImageLoader.Builder {
        return ImageLoader.Builder(context).allowHardware(false)
    }

    override fun applyComponents(builder: ComponentRegistry.Builder) {
        builder.add(DivkitAssetUriMapper())
        builder.add(GifDecoderFactory)
        builder.add(LazySvgDecoderFactory(maxDisplaySize))
        builder.add(UnthrottledImageDecoderFactory)
    }
}
