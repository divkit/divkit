package com.yandex.div.compose.screenshot

import android.net.Uri
import coil3.ComponentRegistry
import com.yandex.div.compose.images.ImageLoaderConfiguration

/**
 * [com.yandex.div.compose.images.ImageLoaderConfiguration] for Roborazzi screenshot tests.
 *
 * Loads images from local resources instead of network.
 *
 * To add new test images place the file in `src/test/resources/images/`.
 */
class LocalImageLoaderConfiguration : ImageLoaderConfiguration {

    override fun applyComponents(builder: ComponentRegistry.Builder) {
        builder.add { chain ->
            when (val data = chain.request.data) {
                is Uri if (data.scheme == "divkit-asset" || data.scheme == "https") -> {
                    val fileName = data.lastPathSegment
                        ?: throw RuntimeException("Failed to get file name: $data")
                    val newRequest = chain.request
                        .newBuilder()
                        .data(loadResource(fileName))
                        .build()
                    chain.withRequest(newRequest).proceed()
                }

                else -> chain.proceed()
            }
        }
    }

    private fun loadResource(fileName: String): ByteArray {
        val stream = LocalImageLoaderConfiguration::class.java.classLoader
            ?.getResourceAsStream("images/$fileName")
        if (stream == null) {
            throw RuntimeException("Loading images from network in Roborazzi tests is forbidden. Local image not found for: $fileName")
        }
        return stream.readAllBytes()
    }
}
