package com.yandex.div.spannable

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class LetterSpacingSpan(val letterSpacing: Float) : MetricAffectingSpan() {

    override fun updateDrawState(paint: TextPaint) {
        apply(paint)
    }

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    private fun apply(paint: TextPaint) {
        paint.letterSpacing = letterSpacing
    }

}
