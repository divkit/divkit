package com.yandex.div.compose.views.image

import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.observeNetworkRestoration
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.imageLoader
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div.compose.utils.toColor
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivImage

@Composable
internal fun DivImageView(
    modifier: Modifier,
    data: DivImage
) {
    val context = divContext
    val density = LocalDensity.current.density

    val scale = data.scale.observedValue()
    val contentScale = scale.toContentScale(density)
    val alignment = toAlignment(
        data.contentAlignmentHorizontal.observedValue(),
        data.contentAlignmentVertical.observedValue()
    )
    val colorFilter = data.tintColor?.observedValue()?.let {
        toColorFilter(it, data.tintMode.observedValue())
    }
    val transformations = data.filters.resolveTransformations(context, density)

    val imageRequestParams = ImageRequestParams(
        data = data.imageUrl.observedValue(),
        scale = scale,
        transformations = transformations
    )
    val imageRequest = context.rememberImageRequest(imageRequestParams)

    var isImageLoaded by remember(imageRequestParams) { mutableStateOf(false) }

    val preview = if (isImageLoaded) {
        null
    } else {
        data.preview?.observedValue(transform = ::decodePreview)
    }
    val previewRequest = if (preview == null) {
        null
    } else {
        context.rememberImageRequest(
            ImageRequestParams(
                data = preview,
                scale = scale,
                transformations = transformations
            )
        )
    }

    val backgroundModifier = if (!isImageLoaded && previewRequest == null) {
        modifier.background(data.placeholderColor.observedColorValue())
    } else {
        modifier
    }

    Box(modifier = backgroundModifier) {
        if (!isImageLoaded && previewRequest != null) {
            val previewPainter = rememberAsyncImagePainter(
                model = previewRequest,
                imageLoader = imageLoader,
            )
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = previewPainter,
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter
            )
        }

        val imagePainter = rememberAsyncImagePainter(
            model = imageRequest,
            imageLoader = imageLoader,
            onSuccess = {
                isImageLoaded = true
            }
        )
        imagePainter.observeNetworkRestoration()
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = imagePainter,
            contentDescription = null,
            contentScale = contentScale,
            alignment = alignment,
            colorFilter = colorFilter
        )
    }
}

private fun decodePreview(data: String): ByteArray {
    return Base64.decode(
        if (data.startsWith("data:")) {
            data.substring(data.indexOf(',') + 1)
        } else {
            data
        },
        Base64.DEFAULT
    )
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
