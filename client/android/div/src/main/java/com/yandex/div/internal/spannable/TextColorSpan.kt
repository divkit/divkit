package com.yandex.div.internal.spannable

import android.text.TextPaint
import android.text.style.ForegroundColorSpan

class TextColorSpan(color: Int) : ForegroundColorSpan(color) {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        if (textPaint.shader != null) textPaint.shader = null
    }
}
