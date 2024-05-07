package com.yandex.div.internal.spannable

import android.graphics.Paint
import android.os.Build
import android.text.style.ReplacementSpan

internal abstract class PositionAwareReplacementSpan : ReplacementSpan() {

    final override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        updateFontMetrics(start, fm)
        return adjustSize(paint, text, start, end, fm)
    }

    abstract fun adjustSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int

    private fun updateFontMetrics(
        start: Int,
        fm: Paint.FontMetricsInt?
    ) {
        if (fm != null) {
            // Workaround for API 27 and less: for unknown reason,
            // font metrics have incorrect value if ReplacementSpan is placed
            // on first char. Assuming that text is measured from start to end,
            // it's safe to reset all metrics during first char measurement.
            if (start == 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                fm.top = 0
                fm.ascent = 0
                fm.bottom = 0
                fm.descent = 0
                fm.leading = 0
            }
        }
    }
}
