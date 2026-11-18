package com.yandex.div.compose.views.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
internal fun RegisterOverlayLayer(
    scope: StateChangeTransitionScope,
    id: String,
    animation: StateChangeAnimation,
    layer: GraphicsLayer?,
    layerOffset: IntOffset = IntOffset.Zero,
) {
    val translation = animation.appearance.resolveTranslation(
        animation.visualState,
        animation.slideGeometry,
        animation.isExiting,
    )
    SideEffect {
        layer?.apply {
            val layout = requireNotNull(animation.exitLayout)
            alpha = animation.visualState.alpha
            scaleX = animation.visualState.scale
            scaleY = animation.visualState.scale
            translationX = translation.x
            translationY = translation.y
            pivotOffset = Offset(
                x = -layerOffset.x + layout.bounds.width * animation.visualState.pivotX,
                y = -layerOffset.y + layout.bounds.height * animation.visualState.pivotY,
            )
            val scenePosition = scope.scene.positionInRoot
            topLeft = IntOffset(
                x = (layout.positionInRoot.x - scenePosition.x + layerOffset.x).roundToInt(),
                y = (layout.positionInRoot.y - scenePosition.y + layerOffset.y).roundToInt(),
            )
            scope.transitionState.registerOverlay(id, this, animation.visualState)
        }
    }
}

internal fun Modifier.cacheStateChangeContent(
    layer: GraphicsLayer?,
    visualState: StateChangeVisualState? = null,
): Modifier {
    if (layer == null) return this
    return drawWithContent {
        layer.record(
            size = IntSize(
                this@drawWithContent.size.width.roundToInt(),
                this@drawWithContent.size.height.roundToInt(),
            )
        ) {
            this@drawWithContent.drawContent()
        }
        visualState?.let {
            layer.alpha = it.alpha
            layer.scaleX = it.scale
            layer.scaleY = it.scale
            layer.translationX = it.translationX
            layer.translationY = it.translationY
            layer.pivotOffset = Offset(size.width * it.pivotX, size.height * it.pivotY)
        }
        drawLayer(layer)
    }
}
