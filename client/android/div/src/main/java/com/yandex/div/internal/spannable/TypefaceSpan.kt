package com.yandex.div.internal.spannable

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * Span for applying text typeface
 */
internal class TypefaceSpan(val typeface: Typeface) : MetricAffectingSpan() {

    override fun updateDrawState(ds: TextPaint) {
        apply(ds)
    }

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    private fun apply(paint: TextPaint) {
        paint.typeface = typeface
    }

}
