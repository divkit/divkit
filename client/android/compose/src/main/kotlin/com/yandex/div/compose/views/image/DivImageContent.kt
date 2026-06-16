package com.yandex.div.compose.views.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import coil3.transform.Transformation
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.observeNetworkRestoration
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div2.DivBase

@Composable
internal fun DivImageContent(
    modifier: Modifier,
    data: DivBase,
    model: Any,
    contentScale: ContentScale,
    alignment: Alignment,
    placeholderColor: Color,
    transformations: List<Transformation> = emptyList(),
    colorFilter: ColorFilter? = null,
    preview: @Composable () -> Any?
) {
    val imageStateStorage = LocalDivViewContext.current.component.imageStateStorage

    val imageRequestParams = ImageRequestParams(
        data = model,
        transformations = transformations
    )

    DisposableEffect(imageRequestParams) {
        onDispose {
            imageStateStorage.setIsLoaded(data, false)
        }
    }

    val isImageLoaded = imageStateStorage.isLoaded(data)
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
        val imageLoader = divContext.component.imageLoader
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

        val imageRequest = rememberImageRequest(imageRequestParams)
        val imagePainter = rememberAsyncImagePainter(
            model = imageRequest,
            imageLoader = imageLoader,
            onSuccess = {
                imageStateStorage.setIsLoaded(data, true)
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
