package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.CharacterStyle

internal class ShadowSpan(private val shadow: ShadowParams) : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint?) {
        with(shadow) {
            tp?.setShadowLayer(radius, offsetX, offsetY, color)
        }
    }

    data class ShadowParams(
        val offsetX: Float,
        val offsetY: Float,
        val radius: Float,
        val color: Int
    )
}
