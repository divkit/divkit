package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.gradient.observeLinearGradient
import com.yandex.div.compose.utils.gradient.observeRadialGradient
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reporter
import com.yandex.div.compose.utils.toColor
import com.yandex.div2.DivBackground
import com.yandex.div2.DivImageBackground
import com.yandex.div2.DivNinePatchBackground

@Composable
internal fun Modifier.backgrounds(value: List<DivBackground>): Modifier {
    var modifier = this
    value.forEach { background ->
        when (background) {
            is DivBackground.Solid ->
                modifier = modifier.background(background.value.color.observedValue().toColor())

            is DivBackground.LinearGradient -> {
                background.value.observeLinearGradient()?.let {
                    modifier = modifier.background(it)
                }
            }

            is DivBackground.RadialGradient -> {
                background.value.observeRadialGradient()?.let {
                    modifier = modifier.background(it)
                }
            }

            is DivBackground.Image ->
                reporter.reportError("Background not supported: ${DivImageBackground.TYPE}")

            is DivBackground.NinePatch ->
                reporter.reportError("Background not supported: ${DivNinePatchBackground.TYPE}")
        }
    }
    return modifier
}
