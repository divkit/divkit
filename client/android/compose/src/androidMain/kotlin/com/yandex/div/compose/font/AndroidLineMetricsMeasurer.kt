package com.yandex.div.compose.font

import android.text.TextPaint
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.resolveAsTypeface
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
@Inject
internal class AndroidLineMetricsMeasurer : LineMetricsMeasurer {

    private val paint = TextPaint()

    override fun measure(resolver: FontFamily.Resolver, params: FontMetricsParams): FontMetrics {
        val typeface = resolver.resolveAsTypeface(
            fontFamily = params.fontFamily,
            fontWeight = params.fontWeight,
            fontStyle = params.fontStyle,
            fontSynthesis = params.fontSynthesis,
        ).value
        paint.reset()
        paint.typeface = typeface
        paint.textSize = params.fontSizePx
        val metrics = paint.fontMetricsInt
        return FontMetrics(
            ascentPx = -metrics.ascent,
            descentPx = metrics.descent,
            fontAscentPx = -paint.ascent(),
        )
    }
}
