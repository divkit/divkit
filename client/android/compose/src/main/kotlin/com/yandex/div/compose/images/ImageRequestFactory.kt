package com.yandex.div.compose.images

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Precision
import coil3.size.Scale
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject
import kotlin.math.max

@DivContextScope
internal class ImageRequestFactory @Inject constructor(
    private val context: Context,
    private val imageRequestListener: ImageRequestListener,
) {
    private val maxDisplaySize = context.resources.displayMetrics.let {
        max(it.widthPixels, it.heightPixels)
    }

    fun build(params: ImageRequestParams): ImageRequest {
        return ImageRequest.Builder(context)
            .data(params.data)
            .transformations(params.transformations)
            .listener(imageRequestListener)
            .apply {
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
    val factory = divContext.component.imageRequestFactory
    return remember(params) { factory.build(params) }
}
