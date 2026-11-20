package com.yandex.div.compose.images

import coil3.ImageLoader
import coil3.network.NetworkClient
import coil3.network.NetworkFetcher
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
@Inject
internal class ImageLoaderFactory(
    private val externalImageLoaderConfiguration: ImageLoaderConfiguration,
    private val platformImageLoaderConfiguration: PlatformImageLoaderConfiguration,
    private val networkClient: Lazy<NetworkClient>,
) {

    fun createImageLoader(): ImageLoader {
        return platformImageLoaderConfiguration.createImageLoaderBuilder()
            .components {
                platformImageLoaderConfiguration.applyComponents(this)
                externalImageLoaderConfiguration.applyComponents(this)
                add(NetworkFetcher.Factory(networkClient = { networkClient.value }))
            }
            .eventListener(externalImageLoaderConfiguration.eventListener)
            .build()
    }
}
