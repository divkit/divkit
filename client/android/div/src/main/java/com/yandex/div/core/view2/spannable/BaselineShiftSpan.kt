package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.ParagraphStyle
import androidx.annotation.IntRange
import androidx.annotation.Px

internal class BaselineShiftSpan(
    @Px private val baselineShift: Int,
    @Px @IntRange(from = 0) val lineHeight: Int = 0
) : MetricAffectingSpan(), ParagraphStyle {

    override fun updateMeasureState(paint: TextPaint) {
        if (lineHeight == 0) {
            paint.baselineShift -= baselineShift
        }
    }

    override fun updateDrawState(paint: TextPaint) {
        paint.baselineShift -= baselineShift
    }
}
