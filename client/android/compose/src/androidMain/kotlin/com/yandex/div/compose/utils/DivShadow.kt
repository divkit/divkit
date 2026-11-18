package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivShadow

@Composable
internal fun DivShadow.observeShadow(
    alpha: Float,
    component: DivLocalComponent = LocalComponent.current,
): Shadow {
    val shadowColor = color.observedValue(component).toColor()
    val shadowAlpha = this.alpha.observedValue(component).toFloat()
    val blur = blur.observedValue(component).toDp().toPx()
    val offsetX = offset.x.observedPxValue(component)
    val offsetY = offset.y.observedPxValue(component)

    return Shadow(
        color = shadowColor.copy(alpha = shadowColor.alpha * shadowAlpha * alpha),
        offset = Offset(offsetX, offsetY),
        blurRadius = blur,
    )
}
