package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.gradient.observeLinearGradient
import com.yandex.div.compose.utils.gradient.observeRadialGradient
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivBackground
import com.yandex.div2.DivBase
import com.yandex.div2.DivNinePatchBackground

@Composable
internal fun Modifier.background(data: DivBase): Modifier {
    var modifier = this
    data.background.orEmpty().forEach {
        modifier = modifier.background(it)
    }
    return modifier
}

@Composable
private fun Modifier.background(background: DivBackground): Modifier {
    when (background) {
        is DivBackground.Solid ->
            return background(background.value.color.observedColorValue())

        is DivBackground.Image ->
            return imageBackground(background.value)

        is DivBackground.LinearGradient -> {
            background.value.observeLinearGradient()?.let {
                return background(it)
            }
        }

        is DivBackground.RadialGradient -> {
            background.value.observeRadialGradient()?.let {
                return background(it)
            }
        }

        is DivBackground.NinePatch ->
            reportError("Background not supported: ${DivNinePatchBackground.TYPE}")
    }

    return this
}
