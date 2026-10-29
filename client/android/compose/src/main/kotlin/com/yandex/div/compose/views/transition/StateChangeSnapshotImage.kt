package com.yandex.div.compose.views.transition

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

internal suspend fun StateChangeSnapshot.freezeImage(
    captureLayer: GraphicsLayer,
    frozenLayer: GraphicsLayer,
    captureArea: StateChangeCaptureArea,
): ImageBitmap {
    val alpha = layer.alpha
    val scaleX = layer.scaleX
    val scaleY = layer.scaleY
    val translationX = layer.translationX
    val translationY = layer.translationY
    val topLeft = layer.topLeft
    layer.alpha = 1f
    layer.scaleX = 1f
    layer.scaleY = 1f
    layer.translationX = 0f
    layer.translationY = 0f
    layer.topLeft = IntOffset.Zero
    return try {
        frozenLayer.record(layer, captureArea)
        captureLayer.record(layer, captureArea)
        captureLayer.toImageBitmap()
    } finally {
        layer.alpha = alpha
        layer.scaleX = scaleX
        layer.scaleY = scaleY
        layer.translationX = translationX
        layer.translationY = translationY
        layer.topLeft = topLeft
    }
}

internal fun StateChangeSnapshot.captureArea(
    layerSize: IntSize,
    layout: StateChangeLayoutState?,
): StateChangeCaptureArea {
    if (!capturesScene || layout == null || layout.sceneSize == IntSize.Zero) {
        return StateChangeCaptureArea(IntOffset.Zero, layerSize)
    }
    return StateChangeCaptureArea(
        offset = IntOffset(
            x = -layout.bounds.left.roundToInt(),
            y = -layout.bounds.top.roundToInt(),
        ),
        size = layout.sceneSize,
    )
}

internal fun GraphicsLayer.record(image: ImageBitmap) {
    record(Density(1f), LayoutDirection.Ltr, IntSize(image.width, image.height)) {
        drawImage(image)
    }
}

private fun GraphicsLayer.record(source: GraphicsLayer, captureArea: StateChangeCaptureArea) {
    record(Density(1f), LayoutDirection.Ltr, captureArea.size) {
        translate(
            left = -captureArea.offset.x.toFloat(),
            top = -captureArea.offset.y.toFloat(),
        ) {
            drawLayer(source)
        }
    }
}

internal data class StateChangeCaptureArea(
    val offset: IntOffset,
    val size: IntSize,
)
