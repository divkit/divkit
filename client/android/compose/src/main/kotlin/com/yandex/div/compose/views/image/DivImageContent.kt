package com.yandex.div.compose.views.image

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.transform.Transformation
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.isValidImageUri
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.images.rememberNetworkRestoringImagePainter
import com.yandex.div.compose.views.modifiers.image.imageContentSize
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivBase
import com.yandex.div2.DivImage

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
    val imageStateListener = rememberImageStateListener(data)
    val imageStateStorage = LocalDivViewContext.current.component.imageStateStorage
    val imageRequestParams = if (imageUrl?.isValidImageUri() == true) {
        ImageRequestParams(
            data = imageUrl,
            transformations = transformations
        )
    } else {
        null
    }
    val successfulRequestRef = remember { SuccessfulImageRequestReference() }
    val imageRequest = imageRequestParams?.let { rememberImageRequest(it) }
    val imagePainter = imageRequest?.let { request ->
        val onState: (AsyncImagePainter.State) -> Unit = remember(
            imageStateStorage,
            data,
            request,
            imageStateListener,
        ) {
            { state: AsyncImagePainter.State ->
                if (state is AsyncImagePainter.State.Success) {
                    successfulRequestRef.value = request
                    imageStateStorage.markLoaded(data, request)
                }
                imageStateListener?.invoke(state)
            }
        }
        rememberNetworkRestoringImagePainter(
            model = request,
            imageLoader = imageLoader,
            onState = onState,
        )
    }
    // Intentionally snapshot without subscribing: future successes invalidate through storage,
    // while this covers an already-successful painter retained for the same request.
    @SuppressLint("StateFlowValueCalledInComposition")
    val isPainterLoaded = imagePainter?.state?.value is AsyncImagePainter.State.Success &&
        successfulRequestRef.value === imageRequest
    val isStoredImageLoaded = imageStateStorage.isLoaded(data)
    val isCurrentRequestStoredLoaded = imageRequest != null &&
        imageStateStorage.isLoaded(data, imageRequest)
    val isImageLoaded = isPainterLoaded || isCurrentRequestStoredLoaded

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

    val hasPlaceholder = !isImageLoaded && previewRequest == null
    val backgroundModifier = if (hasPlaceholder) {
        modifier.background(placeholderColor)
    } else {
        modifier
    }

    val previewPainter = if (!isImageLoaded && previewRequest != null) {
        rememberAsyncImagePainter(
            model = previewRequest,
            imageLoader = imageLoader,
            onState = component.debugConfiguration.imagePainterStateListener
        )
    } else {
        null
    }

    Box(
        modifier = backgroundModifier.imageContentSize(
            data, imagePainter, previewPainter, contentScale, hasPlaceholder,
        )
    ) {
        if (previewPainter != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = previewPainter,
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter
            )
        }

        if (imagePainter != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = imagePainter,
                contentDescription = null,
                contentScale = contentScale,
                alignment = alignment,
                colorFilter = colorFilter
            )
            DisposableEffect(imageStateStorage, data, imageRequest, imagePainter) {
                if (isPainterLoaded && successfulRequestRef.value === imageRequest) {
                    imageStateStorage.markLoaded(data, imageRequest)
                }
                onDispose {
                    imageStateStorage.reset(data, imageRequest)
                }
            }
        }
    }
}

@Composable
private fun rememberImageStateListener(data: DivBase): ((AsyncImagePainter.State) -> Unit)? {
    val component = divContext.component
    val painterStateListener = component.debugConfiguration.imagePainterStateListener
    val animation = (data as? DivImage)?.appearanceAnimation ?: return painterStateListener
    val appearanceAnimation = LocalDivViewContext.current.component.imageStateStorage
        .getAppearanceAnimation(data)
    val resolver = expressionResolver
    val scope = rememberCoroutineScope()
    DisposableEffect(appearanceAnimation) {
        onDispose { appearanceAnimation.stop() }
    }
    return { state ->
        painterStateListener?.invoke(state)
        if (state is AsyncImagePainter.State.Success) {
            appearanceAnimation.onImageLoaded(
                dataSource = state.result.dataSource,
                animation = animation,
                resolver = resolver,
                animationsEnabled = component.animationConfiguration.isEnabled,
                scope = scope,
            )
        }
    }
}

private class SuccessfulImageRequestReference {
    var value: ImageRequest? = null
}
