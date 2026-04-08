package com.yandex.div.compose.views.image

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.platform.LocalDensity
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.imageLoader
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedIntValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reporter
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div.compose.utils.toColor
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivFilter
import com.yandex.div2.DivImage
import com.yandex.div2.DivImageScale
import coil3.size.Size as CoilSize

@Composable
internal fun DivImageView(
    modifier: Modifier,
    data: DivImage
) {
    val imageUrl = data.imageUrl.observedValue()
    val scale = data.scale.observedValue()
    val contentAlignmentHorizontal = data.contentAlignmentHorizontal.observedValue()
    val contentAlignmentVertical = data.contentAlignmentVertical.observedValue()
    val preview = data.preview?.observedValue()
    val tintColor = data.tintColor?.observedValue()
    val density = LocalDensity.current.density
    val contentScale = scale.toContentScale(density)
    val alignment = toAlignment(contentAlignmentHorizontal, contentAlignmentVertical)
    val colorFilter = tintColor?.let {
        toColorFilter(it, data.tintMode.observedValue())
    }

    val context = divContext
    val filterTransformations = data.filters.resolveTransformations(context, density)

    var imageLoaded by remember { mutableStateOf(false) }

    val previewModel = if (preview != null) {
        remember(preview, scale, filterTransformations) {
            buildImagePreviewRequest(context, preview, scale, filterTransformations)
        }
    } else null
    val remoteModel = remember(imageUrl, scale, filterTransformations) {
        buildImageRequest(context, imageUrl, scale, filterTransformations)
    }

    val backgroundModifier = if (!imageLoaded && previewModel == null) {
        modifier.background(data.placeholderColor.observedColorValue())
    } else {
        modifier
    }

    Box(modifier = backgroundModifier) {
        if (!imageLoaded && previewModel != null) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = previewModel,
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter,
            )
        }

        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = remoteModel,
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = contentScale,
            alignment = alignment,
            colorFilter = colorFilter,
            onSuccess = {
                imageLoaded = true
            },
        )
    }
}

@Composable
private fun List<DivFilter>?.resolveTransformations(
    context: Context,
    density: Float,
): List<Transformation> {
    if (this == null) return emptyList()
    val transformations = mutableListOf<Transformation>()
    for (filter in this) {
        when (filter) {
            is DivFilter.Blur -> {
                val radiusDp = filter.value.radius.observedIntValue()
                if (radiusDp > 0) {
                    val transformation = BlurTransformation(context, radiusDp, density)
                    transformations += transformation

                    if (radiusDp > transformation.maxRadiusDp) {
                        reporter.reportWarning(
                            "The maximum supported blur radius is 25. Values exceeding this limit will be automatically downscaled"
                        )
                    }
                }
            }
            is DivFilter.RtlMirror -> reporter.reportError("Not implemented")
        }
    }
    return transformations
}

private fun buildImagePreviewRequest(
    context: Context,
    preview: String,
    scale: DivImageScale,
    filterTransformations: List<Transformation>,
): ImageRequest {
    val decodedBase64 = Base64.decode(
        extractBase64String(preview),
        Base64.DEFAULT
    )

    return buildImageRequest(context, decodedBase64, scale, filterTransformations)
}

private fun buildImageRequest(
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

private fun extractBase64String(rawBase64: String): String {
    if (rawBase64.startsWith("data:")) {
        return rawBase64.substring(rawBase64.indexOf(',') + 1)
    }
    return rawBase64
}

private fun DivImageScale.toContentScale(density: Float): ContentScale {
    return when (this) {
        DivImageScale.FILL -> ContentScale.Crop
        DivImageScale.FIT -> ContentScale.Fit
        DivImageScale.NO_SCALE -> NoScaleContentScale(density)
        DivImageScale.STRETCH -> ContentScale.FillBounds
    }
}

private class NoScaleContentScale(private val density: Float) : ContentScale {
    override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
        return ScaleFactor(density, density)
    }
}

private fun toColorFilter(tintColor: Int, tintMode: DivBlendMode): ColorFilter {
    val blendMode = when (tintMode) {
        DivBlendMode.SOURCE_IN -> BlendMode.SrcIn
        DivBlendMode.SOURCE_ATOP -> BlendMode.SrcAtop
        DivBlendMode.DARKEN -> BlendMode.Darken
        DivBlendMode.LIGHTEN -> BlendMode.Lighten
        DivBlendMode.MULTIPLY -> BlendMode.Multiply
        DivBlendMode.SCREEN -> BlendMode.Screen
    }
    return ColorFilter.tint(tintColor.toColor(), blendMode)
}
