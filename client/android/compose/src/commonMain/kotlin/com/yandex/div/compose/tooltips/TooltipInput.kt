package com.yandex.div.compose.tooltips

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

internal fun interface TooltipInput {
    fun applyTo(modifier: Modifier, onTap: () -> Unit): Modifier
}

internal val LocalTooltipInput = staticCompositionLocalOf<TooltipInput?> { null }

@Composable
internal fun Modifier.tooltipInput(onTap: () -> Unit = {}): Modifier {
    val touchInput = LocalTooltipInput.current
    return touchInput?.applyTo(this, onTap) ?: pointerInput(onTap) {
        detectTapGestures(onTap = { onTap() })
    }
}
