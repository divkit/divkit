package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div2.DivBase
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.appearance(data: DivBase): Modifier {
    val alpha = if (data.visibility.observedValue() == DivVisibility.VISIBLE) {
        data.alpha.observedFloatValue()
    } else {
        0f
    }
    // The stroke must be applied before the border clip so that the anti-aliased clip doesn't
    // attenuate it at the outline where it has to cover the clipped background edge.
    return applyIfNotNull(data.border) { borderShadow(it, alpha) }
        .applyIf(alpha < 1f) { alpha(alpha) }
        .applyIfNotNull(data.border) { borderStroke(it) }
        .applyIfNotNull(data.border) { borderClip(it) }
}
