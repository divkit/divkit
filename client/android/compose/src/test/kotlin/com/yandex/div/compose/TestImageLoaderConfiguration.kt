package com.yandex.div.compose

import androidx.core.graphics.createBitmap
import coil3.ComponentRegistry
import coil3.asImage
import coil3.request.SuccessResult
import com.yandex.div.compose.images.ImageLoaderConfiguration

class TestImageLoaderConfiguration : ImageLoaderConfiguration {
    val capturedUrls = mutableSetOf<String>()

    override fun applyComponents(builder: ComponentRegistry.Builder) {
        builder.add { chain ->
            capturedUrls.add(chain.request.data.toString())
            SuccessResult(
                image = createBitmap(1, 1).asImage(),
                request = chain.request
            )
        }
    }
}
