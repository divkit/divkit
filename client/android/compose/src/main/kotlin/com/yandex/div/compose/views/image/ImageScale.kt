package com.yandex.div.compose.views.image

import android.content.Context
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import com.yandex.div2.DivImageScale
import coil3.size.Size as CoilSize

internal fun DivImageScale.toContentScale(density: Float): ContentScale {
    return when (this) {
        DivImageScale.FILL -> ContentScale.Crop
        DivImageScale.FIT -> ContentScale.Fit
        DivImageScale.NO_SCALE -> NoScaleContentScale(density)
        DivImageScale.STRETCH -> ContentScale.FillBounds
    }
}

internal fun buildImageRequest(
    context: Context,
    data: Any?,
    scale: DivImageScale,
    filterTransformations: List<Transformation>,
): ImageRequest {
    return ImageRequest.Builder(context).apply {
        data(data)
        if (scale == DivImageScale.NO_SCALE) {
            size(CoilSize.ORIGINAL)
        }
        if (filterTransformations.isNotEmpty()) {
            transformations(filterTransformations)
        }
    }.build()
}

private class NoScaleContentScale(private val density: Float) : ContentScale {
    override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
        return ScaleFactor(density, density)
    }
}
