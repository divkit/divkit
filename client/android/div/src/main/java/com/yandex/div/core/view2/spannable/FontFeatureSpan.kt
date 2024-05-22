package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

internal class FontFeatureSpan(
    private val settings: String
) : MetricAffectingSpan() {

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.fontFeatureSettings = settings
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.fontFeatureSettings = settings
    }
}
