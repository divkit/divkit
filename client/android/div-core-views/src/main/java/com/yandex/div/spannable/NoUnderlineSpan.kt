package com.yandex.div.spannable

import android.text.TextPaint
import android.text.style.UnderlineSpan

class NoUnderlineSpan: UnderlineSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}
