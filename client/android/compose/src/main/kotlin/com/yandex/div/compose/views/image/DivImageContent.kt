package com.yandex.div.compose.views.image

import android.net.Uri
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
    imageUrl: Uri,
    contentScale: ContentScale,
    alignment: Alignment,
    placeholderColor: Color,
    transformations: List<Transformation> = emptyList(),
    colorFilter: ColorFilter? = null,
    preview: @Composable () -> Any?
) {
    val imageStateStorage = LocalDivViewContext.current.component.imageStateStorage
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
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(
                    model = previewRequest,
                    imageLoader = imageLoader
                ),
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter
            )
        }

        if (imageUrl.isValid()) {
            val imageRequestParams = ImageRequestParams(
                data = imageUrl,
                transformations = transformations
            )
            val imagePainter = rememberAsyncImagePainter(
                model = rememberImageRequest(imageRequestParams),
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
            DisposableEffect(imageRequestParams) {
                onDispose {
                    imageStateStorage.setIsLoaded(data, false)
                }
            }
        }
    }
}

// TODO: remove when div-image.image_url becomes optional
private fun Uri.isValid(): Boolean {
    return toString() != "empty://"
}
