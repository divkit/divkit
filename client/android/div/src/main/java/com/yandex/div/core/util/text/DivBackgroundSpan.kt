package com.yandex.div.core.util.text

import android.text.TextPaint
import android.text.style.UnderlineSpan
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder

internal class DivBackgroundSpan(
    val border: DivTextRangeBorder?,
    val background: DivTextRangeBackground?
) : UnderlineSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}
