package com.yandex.div.compose.extensions.pinchtozoom

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun ZoomHost(
    state: MutableState<PinchToZoomState?>,
    onReady: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val zoom = state.value ?: return
    val density = LocalDensity.current

    LaunchedEffect(zoom.isFinishing) {
        if (!zoom.isFinishing) {
            return@LaunchedEffect
        }

        val startScale = zoom.scale
        val startTranslation = zoom.translation
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(RETURN_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        ) { progress, _ ->
            state.value = zoom.copy(
                scale = 1f + (startScale - 1f) * (1f - progress),
                translation = startTranslation * (1f - progress)
            )
        }
        state.value = null
    }

    Popup(
        popupPositionProvider = WindowPositionProvider,
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            clippingEnabled = false
        )
    ) {
        val width = with(density) { zoom.bounds.width.toDp() }
        val height = with(density) { zoom.bounds.height.toDp() }
        val panSensitivity = (zoom.scale - 1f).coerceIn(0f, 1f)
        val dimAlpha = (zoom.scale - 1f).coerceIn(0f, 1f)

        Box(
            Modifier
                .fillMaxSize()
                .background(zoom.dimColor.copy(alpha = zoom.dimColor.alpha * dimAlpha))
                .onPlaced { onReady() }
        ) {
            Box(
                Modifier
                    .graphicsLayer {
                        translationX = zoom.bounds.left + zoom.translation.x * panSensitivity
                        translationY = zoom.bounds.top + zoom.translation.y * panSensitivity
                        scaleX = zoom.scale
                        scaleY = zoom.scale
                        transformOrigin = TransformOrigin(
                            pivotFractionX = (zoom.pivot.x / zoom.bounds.width).coerceIn(0f, 1f),
                            pivotFractionY = (zoom.pivot.y / zoom.bounds.height).coerceIn(0f, 1f)
                        )
                    }
                    .requiredSize(width, height)
            ) {
                content(Modifier.fillMaxSize())
            }
        }
    }
}

private object WindowPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset = IntOffset.Zero
}

private const val RETURN_ANIMATION_DURATION = 200
