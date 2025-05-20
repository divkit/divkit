package com.yandex.div.core.view2.spannable

import android.os.Build
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
internal class FontVariationSpan(
    private val settings: String
) : MetricAffectingSpan() {

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.fontVariationSettings = settings
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.fontVariationSettings = settings
    }
}
