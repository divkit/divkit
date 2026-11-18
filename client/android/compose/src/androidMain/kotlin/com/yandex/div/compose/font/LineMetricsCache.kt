package com.yandex.div.compose.font

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@Immutable
internal data class LineMetrics(
    val ascent: TextUnit,
    val descent: TextUnit,
    val ascentPx: Int,
    val descentPx: Int,
    val fontAscentPx: Float,
) {
    val heightPx: Float get() = (ascentPx + descentPx).toFloat()
}

@DivContextScope
internal class LineMetricsCache @Inject constructor(
    private val measurer: LineMetricsMeasurer,
) {

    private val cache = HashMap<FontMetricsParams, LineMetrics>()

    private var resolver: FontFamily.Resolver? = null
    private var density = 0f
    private var fontScale = 0f

    @get:VisibleForTesting
    val size: Int get() = cache.size

    fun getOrCreate(
        fontFamilyResolver: FontFamily.Resolver,
        density: Density,
        style: TextStyle,
    ): LineMetrics {
        dropStaleEntries(fontFamilyResolver, density)
        val key = FontMetricsParams(style, density)
        return cache.getOrPut(key) { measure(fontFamilyResolver, density, key) }
    }

    private fun dropStaleEntries(fontFamilyResolver: FontFamily.Resolver, density: Density) {
        if (resolver === fontFamilyResolver &&
            this.density == density.density &&
            fontScale == density.fontScale
        ) {
            return
        }

        resolver = fontFamilyResolver
        this.density = density.density
        fontScale = density.fontScale
        cache.clear()
    }

    private fun measure(
        fontFamilyResolver: FontFamily.Resolver,
        density: Density,
        key: FontMetricsParams,
    ): LineMetrics {
        val metrics = measurer.measure(fontFamilyResolver, key)
        return with(density) {
            LineMetrics(
                ascent = metrics.ascentPx.toSp(),
                descent = metrics.descentPx.toSp(),
                ascentPx = metrics.ascentPx,
                descentPx = metrics.descentPx,
                fontAscentPx = metrics.fontAscentPx,
            )
        }
    }
}

internal fun interface LineMetricsMeasurer {
    fun measure(resolver: FontFamily.Resolver, params: FontMetricsParams): FontMetrics
}

internal data class FontMetrics(
    val ascentPx: Int,
    val descentPx: Int,
    val fontAscentPx: Float,
)

internal data class FontMetricsParams(
    val fontFamily: FontFamily?,
    val fontWeight: FontWeight,
    val fontStyle: FontStyle,
    val fontSynthesis: FontSynthesis,
    val fontSizePx: Float,
) {
    constructor(style: TextStyle, density: Density) : this(
        fontFamily = style.fontFamily,
        fontWeight = style.fontWeight ?: FontWeight.Normal,
        fontStyle = style.fontStyle ?: FontStyle.Normal,
        fontSynthesis = style.fontSynthesis ?: FontSynthesis.All,
        fontSizePx = with(density) {
            if (style.fontSize.isSpecified) style.fontSize.toPx() else DEFAULT_FONT_SIZE.toPx()
        },
    )
}

private val DEFAULT_FONT_SIZE = 14.sp
