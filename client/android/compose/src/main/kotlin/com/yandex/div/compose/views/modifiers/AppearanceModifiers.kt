package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div2.DivBase
import com.yandex.div2.DivBorder
import com.yandex.div2.DivImage
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.appearance(
    data: DivBase,
    visibility: DivVisibility,
    border: DivBorder? = data.border,
): Modifier {
    val alpha = if (visibility == DivVisibility.VISIBLE) {
        data.alpha.observedFloatValue()
    } else {
        0f
    }
    if (data is DivImage && data.appearanceAnimation != null && visibility == DivVisibility.VISIBLE) {
        val animation = LocalDivViewContext.current.component.imageStateStorage.getAppearanceAnimation(data)
        return applyIfNotNull(border) { borderShadow(it) { animation.alpha(alpha) } }
            .graphicsLayer {
                this.alpha = animation.alpha(alpha)
                clip = this.alpha < 1f
            }
            .applyIfNotNull(border) { borderStrokeAndClip(it) }
    }
    return applyIfNotNull(border) { borderShadow(it, alpha) }
        .applyIf(alpha < 1f) { alpha(alpha) }
        .applyIfNotNull(border) { borderStrokeAndClip(it) }
}
