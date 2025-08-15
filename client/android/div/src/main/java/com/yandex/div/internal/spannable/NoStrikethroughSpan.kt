package com.yandex.div.internal.spannable

import android.text.TextPaint
import android.text.style.StrikethroughSpan

internal class NoStrikethroughSpan: StrikethroughSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isStrikeThruText = false
    }
}
