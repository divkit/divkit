package com.yandex.div.compose.views.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import coil3.transform.Transformation
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.observeNetworkRestoration
import com.yandex.div.compose.images.rememberImageRequest

@Composable
internal fun DivImageContent(
    modifier: Modifier,
    model: Any,
    contentScale: ContentScale,
    alignment: Alignment,
    placeholderColor: Color,
    transformations: List<Transformation> = emptyList(),
    colorFilter: ColorFilter? = null,
    preview: @Composable () -> Any? = { null },
) {
    val imageLoader = divContext.component.imageLoader

    val imageRequestParams = ImageRequestParams(
        data = model,
        transformations = transformations
    )
    val imageRequest = rememberImageRequest(imageRequestParams)

    var isImageLoaded by remember(imageRequestParams) { mutableStateOf(false) }

    val previewModel = if (isImageLoaded) null else preview()
    val previewRequest = if (previewModel == null) {
        null
    } else {
        rememberImageRequest(
            ImageRequestParams(
                data = previewModel,
                transformations = transformations
            )
        )
    }

    val backgroundModifier = if (!isImageLoaded && previewRequest == null) {
        modifier.background(placeholderColor)
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
