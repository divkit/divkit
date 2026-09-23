package com.yandex.div.compose.views.image

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.transform.Transformation
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.isValidImageUri
import com.yandex.div.compose.images.observeNetworkRestoration
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBase

@Composable
internal fun DivImageContent(
    modifier: Modifier,
    data: DivBase,
    imageUrl: Uri?,
    contentScale: ContentScale,
    alignment: Alignment,
    placeholderColor: Color,
    transformations: List<Transformation> = emptyList(),
    colorFilter: ColorFilter? = null,
    highPriorityPreviewShow: Expression<Boolean>? = null,
    preview: @Composable () -> Any?
) {
    val component = divContext.component
    val imageLoader = component.imageLoader
    val painterStateListener = component.debugConfiguration.imagePainterStateListener
    val imageStateStorage = LocalDivViewContext.current.component.imageStateStorage
    val imageRequestParams = if (imageUrl?.isValidImageUri() == true) {
        ImageRequestParams(
            data = imageUrl,
            transformations = transformations
        )
    } else {
        null
    }
    val imagePainter = imageRequestParams?.let {
        rememberAsyncImagePainter(
            model = rememberImageRequest(it),
            imageLoader = imageLoader,
            onState = painterStateListener
        )
    }
    val imagePainterState = imagePainter?.state?.collectAsState()?.value
    val isPainterLoaded = imagePainterState is AsyncImagePainter.State.Success
    val isStoredImageLoaded = imageStateStorage.isLoaded(data)
    val isImageLoaded = isPainterLoaded || isStoredImageLoaded

    if (isPainterLoaded && !isStoredImageLoaded) {
        SideEffect {
            imageStateStorage.setIsLoaded(data, true)
        }
    }

    // Match DivImageBinder.applyImage: a replacement preview is not high priority if the previous
    // image was loaded. Capture that state before DisposableEffect resets it on a URL change.
    val canShowHighPriorityPreview = remember(imageUrl) { mutableStateOf(!isStoredImageLoaded) }
    val previewModel = if (isImageLoaded) null else preview()
    val previewRequest = if (previewModel == null) {
        null
    } else {
        // Keep this preview's priority when SideEffect enables high priority for later previews.
        val useHighPriority = remember(imageUrl, previewModel) { canShowHighPriorityPreview.value }
        val synchronous = useHighPriority && highPriorityPreviewShow.observedValue(false)
        rememberImageRequest(
            ImageRequestParams(
                data = previewModel,
                transformations = transformations,
                synchronous = synchronous
            )
        )
    }
    if (!isImageLoaded) {
        SideEffect {
            // Later preview changes while loading can use high priority, as in
            // DivImageBinder.observePlaceholders.
            canShowHighPriorityPreview.value = true
        }
    }

    val backgroundModifier = if (!isImageLoaded && previewRequest == null) {
        modifier.background(placeholderColor)
    } else {
        modifier
    }

    Box(modifier = backgroundModifier) {
        if (!isImageLoaded && previewRequest != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(
                    model = previewRequest,
                    imageLoader = imageLoader,
                    onState = painterStateListener
                ),
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter
            )
        }

        if (imagePainter != null) {
            imagePainter.observeNetworkRestoration()
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = imagePainter,
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter
            )
            DisposableEffect(imageStateStorage, data, imageRequestParams) {
                onDispose {
                    imageStateStorage.setIsLoaded(data, false)
                }
            }
        }
    }
}
