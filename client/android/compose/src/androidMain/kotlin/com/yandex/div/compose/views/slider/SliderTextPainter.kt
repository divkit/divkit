package com.yandex.div.compose.views.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.DrawScope

internal interface SliderTextPainterFactory {
    @Composable
    fun rememberPainter(style: SliderTextStyle): SliderTextPainter
}

internal fun interface SliderTextPainter {
    fun draw(scope: DrawScope, text: String, centerX: Float)
}
