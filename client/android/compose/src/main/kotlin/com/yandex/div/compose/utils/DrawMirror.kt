package com.yandex.div.compose.utils

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale

internal inline fun DrawScope.mirrorHorizontallyIfRtl(isRtl: Boolean, block: DrawScope.() -> Unit) {
    if (isRtl) {
        scale(scaleX = -1f, scaleY = 1f) { block() }
    } else {
        block()
    }
}
