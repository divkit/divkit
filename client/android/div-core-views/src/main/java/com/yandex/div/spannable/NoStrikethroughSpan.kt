package com.yandex.div.spannable

import android.text.TextPaint
import android.text.style.StrikethroughSpan

class NoStrikethroughSpan: StrikethroughSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isStrikeThruText = false
    }
}
