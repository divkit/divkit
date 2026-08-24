package com.yandex.div.compose.images

import android.content.Context
import coil3.ImageLoader
import coil3.request.allowHardware
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

@DivContextScope
internal class ImageLoaderFactory @Inject constructor(
    private val context: Context,
    private val imageLoaderConfiguration: ImageLoaderConfiguration
) {

    fun createImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .allowHardware(false)
            .components {
                imageLoaderConfiguration.applyComponents(this)
                add(DivkitAssetUriMapper())
                add(gifDecoderFactory())
            }
            .eventListener(imageLoaderConfiguration.eventListener)
            .build()
    }
}
