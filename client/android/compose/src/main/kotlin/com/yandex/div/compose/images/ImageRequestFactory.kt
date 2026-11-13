package com.yandex.div.compose.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.PlatformContext
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Precision
import coil3.size.Scale
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.di.DivContextScope
import com.yandex.div.compose.di.Names
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlin.coroutines.EmptyCoroutineContext

@DivContextScope
internal class ImageRequestFactory @Inject constructor(
    private val context: PlatformContext,
    private val imageRequestListener: ImageRequestListener,
    @Named(Names.MAX_DISPLAY_SIZE)
    private val maxDisplaySize: Int,
) {
    fun build(params: ImageRequestParams): ImageRequest {
        return ImageRequest.Builder(context)
            .data(params.data)
            .transformations(params.transformations)
            .listener(imageRequestListener)
            .apply {
                if (params.synchronous) {
                    extras[UnthrottledImageDecoderFactory.enabled] = true
                    coroutineContext(EmptyCoroutineContext)
                }
                if (params.limitToDisplaySize) {
                    size(maxDisplaySize, maxDisplaySize)
                    scale(Scale.FIT)
                    precision(Precision.INEXACT)
                }
            }
            .build()
    }
}

@Composable
internal fun rememberImageRequest(params: ImageRequestParams): ImageRequest {
    val factory = LocalDivContext.current.component.imageRequestFactory
    return remember(params) { factory.build(params) }
}
