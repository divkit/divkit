package com.yandex.div.compose.screenshot

import android.content.Context
import android.net.Uri
import coil3.ComponentRegistry
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.test.images.LocalImageLoader

/**
 * [com.yandex.div.compose.images.ImageLoaderConfiguration] for Roborazzi screenshot tests.
 *
 * Loads the shared images from `test-utils` instead of using the network.
 */
class LocalImageLoaderConfiguration(context: Context) : ImageLoaderConfiguration {
    private val loader = LocalImageLoader(context.assets)

    override fun applyComponents(builder: ComponentRegistry.Builder) {
        builder.add { chain ->
            val data = chain.request.data
            val bytes = (data as? Uri)?.let(loader::load)
            if (bytes == null) {
                chain.proceed()
            } else {
                val request = chain.request.newBuilder().data(bytes).build()
                chain.withRequest(request).proceed()
            }
        }
    }
}
