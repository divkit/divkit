package com.yandex.div.internal.spannable

import android.text.TextPaint
import android.text.style.UnderlineSpan

internal class NoUnderlineSpan: UnderlineSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}
