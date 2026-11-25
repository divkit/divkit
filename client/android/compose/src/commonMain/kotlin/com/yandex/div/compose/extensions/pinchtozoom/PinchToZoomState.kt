package com.yandex.div.compose.extensions.pinchtozoom

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

@Immutable
internal data class PinchToZoomState(
    val bounds: Rect,
    val pivot: Offset,
    val dimColor: Color,
    val animationsEnabled: Boolean,
    val scale: Float = 1f,
    val translation: Offset = Offset.Zero,
    val isFinishing: Boolean = false
) {
    fun updated(zoom: Float, pan: Offset): PinchToZoomState {
        if (isFinishing) {
            return this
        }
        return copy(
            scale = (scale * zoom).coerceIn(1f, MAX_SCALE),
            translation = translation + pan
        )
    }

    fun finishing(): PinchToZoomState? =
        if (animationsEnabled) copy(isFinishing = true) else null
}

private const val MAX_SCALE = 4f
