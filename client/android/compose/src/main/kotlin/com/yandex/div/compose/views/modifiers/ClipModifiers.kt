package com.yandex.div.compose.views.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.yandex.div2.DivBase

internal fun Modifier.clipToBounds(data: DivBase): Modifier {
    // borderStrokeAndClip already installs a clipping layer when either radius is specified,
    // including zero and expression values. No layout or transform separates that clip from
    // the container's bounds. Extensions may change this, and focus may replace the border.
    val border = data.border ?: return clipToBounds()
    val hasBorderClip = border.cornerRadius != null || border.cornersRadius != null
    return if (hasBorderClip && data.focus?.border == null && data.extensions.isNullOrEmpty()) {
        this
    } else {
        clipToBounds()
    }
}
