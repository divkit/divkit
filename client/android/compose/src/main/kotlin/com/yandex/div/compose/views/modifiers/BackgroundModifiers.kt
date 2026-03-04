package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.evaluate
import com.yandex.div.compose.views.toColor
import com.yandex.div2.DivBackground
import com.yandex.div2.DivSolidBackground

@Composable
internal fun Modifier.backgrounds(value: List<DivBackground>): Modifier {
    var modifier = this
    value.forEach { background ->
        when (background) {
            is DivBackground.Solid ->
                modifier = modifier.solidBackground(background.value)

            is DivBackground.Image -> TODO()
            is DivBackground.LinearGradient -> TODO()
            is DivBackground.NinePatch -> TODO()
            is DivBackground.RadialGradient -> TODO()
        }
    }
    return modifier
}

@Composable
private fun Modifier.solidBackground(value: DivSolidBackground): Modifier {
    return background(
        color = value.color.evaluate().toColor()
    )
}
