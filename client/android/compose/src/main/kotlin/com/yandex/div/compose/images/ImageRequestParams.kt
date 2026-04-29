package com.yandex.div.compose.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.yandex.div.compose.DivContext
import com.yandex.div2.DivImageScale

internal data class ImageRequestParams(
    val data: Any,
    val scale: DivImageScale,
    val transformations: List<Transformation>
)

@Composable
internal fun DivContext.rememberImageRequest(params: ImageRequestParams): ImageRequest {
    return remember(params) {
        ImageRequest.Builder(this)
            .data(params.data)
            .transformations(params.transformations)
            .apply {
                if (params.scale == DivImageScale.NO_SCALE) {
                    size(Size.ORIGINAL)
                }
            }
            .listener(component.imageRequestListener)
            .build()
    }
}
