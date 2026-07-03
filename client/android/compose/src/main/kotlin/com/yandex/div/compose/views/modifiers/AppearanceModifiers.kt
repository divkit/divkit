package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivBase
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.appearance(data: DivBase): Modifier {
    val alphaValue = if (data.visibility.observedValue() == DivVisibility.VISIBLE) {
        data.alpha.observedFloatValue()
    } else {
        0f
    }

    var modifier = this
    data.transform?.let {
        modifier = modifier.transform(it)
    }

    data.border?.let {
        modifier = modifier.borderShadow(it, alphaValue)
    }

    if (alphaValue < 1f) {
        modifier = modifier.alpha(alphaValue)
    }

    data.border?.let {
        modifier = modifier.borderClip(it)
    }

    return modifier
}
