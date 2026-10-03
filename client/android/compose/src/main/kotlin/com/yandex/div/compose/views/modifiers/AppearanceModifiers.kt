package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div2.DivBase
import com.yandex.div2.DivBorder
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
    return applyIfNotNull(border) { borderShadow(it, alpha) }
        .applyIf(alpha < 1f) { alpha(alpha) }
        .applyIfNotNull(border) { borderStrokeAndClip(it) }
}
