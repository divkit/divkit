package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.CharacterStyle

internal class ShadowSpan(private val shadow: ShadowData) : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint?) {
        with(shadow) {
            tp?.setShadowLayer(radius, offsetX, offsetY, color)
        }
    }
}
