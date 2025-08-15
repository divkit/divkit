package com.yandex.div.core.view2.spannable

import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.ParagraphStyle
import androidx.annotation.IntRange
import androidx.annotation.Px

internal class FontSizeSpan(
    @Px val fontSize: Int,
    @Px @IntRange(from = 0) val lineHeight: Int = 0
) : MetricAffectingSpan(), ParagraphStyle {

    override fun updateDrawState(paint: TextPaint) {
        paint.textSize = fontSize.toFloat()
    }

    override fun updateMeasureState(paint: TextPaint) {
        if (lineHeight == 0) {
            paint.textSize = fontSize.toFloat()
        } else if (lineHeight < paint.textSize) {
            val scale = fontSize.toFloat() / lineHeight
            paint.textScaleX = scale
            paint.textSize = lineHeight.toFloat()
        } else {
            val scale = fontSize.toFloat() / paint.textSize
            paint.textScaleX = scale
        }
    }
}
