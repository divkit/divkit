package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import com.yandex.div2.DivShadow

@Composable
internal fun DivShadow.observeShadow(alpha: Float): Shadow {
    val shadowColor = color.observedColorValue()
    val shadowAlpha = this.alpha.observedFloatValue()
    val blur = blur.observedValue().toDp().toPx()
    val offsetX = offset.x.observedValue().toPx()
    val offsetY = offset.y.observedValue().toPx()

    return Shadow(
        color = shadowColor.copy(alpha = shadowAlpha * alpha),
        offset = Offset(offsetX, offsetY),
        blurRadius = blur,
    )
}
