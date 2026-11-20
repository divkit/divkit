package com.yandex.div.compose.screenshot

import android.content.Context
import android.net.Uri
import coil3.ComponentRegistry
import coil3.toAndroidUri
import com.yandex.div.compose.images.ImageLoaderConfiguration
import com.yandex.div.test.images.LocalImageLoader
import coil3.Uri as CoilUri

/**
 * [com.yandex.div.compose.images.ImageLoaderConfiguration] for Roborazzi screenshot tests.
 *
 * Loads the shared images from `test-utils` instead of using the network.
 */
class LocalImageLoaderConfiguration(context: Context) : ImageLoaderConfiguration {
    private val loader = LocalImageLoader(context.assets)

    override fun applyComponents(builder: ComponentRegistry.Builder) {
        builder.add { chain ->
            val uri = when (val data = chain.request.data) {
                is Uri -> data
                is CoilUri -> data.toAndroidUri()
                else -> null
            }
            val bytes = uri?.let(loader::load)
            if (bytes == null) {
                chain.proceed()
            } else {
                val request = chain.request.newBuilder().data(bytes).build()
                chain.withRequest(request).proceed()
            }
        }
    }
}
