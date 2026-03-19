package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reporter
import com.yandex.div.compose.utils.toColor
import com.yandex.div2.DivBackground
import com.yandex.div2.DivImageBackground
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivNinePatchBackground
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivSolidBackground

@Composable
internal fun Modifier.backgrounds(value: List<DivBackground>): Modifier {
    var modifier = this
    value.forEach { background ->
        when (background) {
            is DivBackground.Solid ->
                modifier = modifier.solidBackground(background.value)

            is DivBackground.Image ->
                reporter.reportError("Background not supported: ${DivImageBackground.TYPE}")

            is DivBackground.LinearGradient ->
                reporter.reportError("Background not supported: ${DivLinearGradient.TYPE}")

            is DivBackground.NinePatch ->
                reporter.reportError("Background not supported: ${DivNinePatchBackground.TYPE}")

            is DivBackground.RadialGradient ->
                reporter.reportError("Background not supported: ${DivRadialGradient.TYPE}")
        }
    }
    return modifier
}

@Composable
private fun Modifier.solidBackground(value: DivSolidBackground): Modifier {
    return background(
        color = value.color.observedValue().toColor()
    )
}
