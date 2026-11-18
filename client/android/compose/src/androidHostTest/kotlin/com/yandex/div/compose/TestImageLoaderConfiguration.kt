package com.yandex.div.compose

import androidx.core.graphics.createBitmap
import coil3.ComponentRegistry
import coil3.asImage
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.yandex.div.compose.images.ImageLoaderConfiguration

class TestImageLoaderConfiguration : ImageLoaderConfiguration {
    val capturedUrls = mutableSetOf<String>()
    val capturedRequests = mutableListOf<ImageRequest>()

    override fun applyComponents(builder: ComponentRegistry.Builder) {
        builder.add { chain ->
            capturedUrls.add(chain.request.data.toString())
            capturedRequests.add(chain.request)
            SuccessResult(
                image = createBitmap(1, 1).asImage(),
                request = chain.request
            )
        }
    }
}
