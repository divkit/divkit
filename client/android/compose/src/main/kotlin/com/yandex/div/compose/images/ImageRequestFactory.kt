package com.yandex.div.compose.images

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.request.ImageRequest
import coil3.request.transformations
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

@DivContextScope
internal class ImageRequestFactory @Inject constructor(
    private val context: Context,
    private val imageRequestListener: ImageRequestListener,
) {
    fun build(params: ImageRequestParams): ImageRequest = ImageRequest.Builder(context)
        .data(params.data)
        .transformations(params.transformations)
        .listener(imageRequestListener)
        .build()
}

@Composable
internal fun rememberImageRequest(params: ImageRequestParams): ImageRequest {
    val factory = divContext.component.imageRequestFactory
    return remember(params) { factory.build(params) }
}
