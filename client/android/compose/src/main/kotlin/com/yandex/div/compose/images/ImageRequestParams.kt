package com.yandex.div.compose.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import com.yandex.div.compose.context.divContext

internal data class ImageRequestParams(
    val data: Any,
    val transformations: List<Transformation>
)

@Composable
internal fun rememberImageRequest(params: ImageRequestParams): ImageRequest {
    val context = divContext
    return remember(params) {
        ImageRequest.Builder(context)
            .data(params.data)
            .transformations(params.transformations)
            .listener(context.component.imageRequestListener)
            .build()
    }
}
