package com.yandex.div.compose.font

import android.text.TextPaint
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.resolveAsTypeface
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

@Immutable
internal data class LineMetrics(
    val ascent: TextUnit,
    val descent: TextUnit,
    val heightPx: Float,
)

@DivContextScope
internal class LineMetricsCache @Inject constructor() {

    private val cache = HashMap<Key, LineMetrics>()
    private val paint = TextPaint()

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
        val key = Key(style, density)
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
        key: Key,
    ): LineMetrics {
        val typeface = fontFamilyResolver.resolveAsTypeface(
            fontFamily = key.fontFamily,
            fontWeight = key.fontWeight,
            fontStyle = key.fontStyle,
            fontSynthesis = key.fontSynthesis,
        ).value
        paint.reset()
        paint.typeface = typeface
        paint.textSize = key.fontSizePx
        val metrics = paint.fontMetricsInt
        return with(density) {
            LineMetrics(
                ascent = (-metrics.ascent).toSp(),
                descent = metrics.descent.toSp(),
                heightPx = (metrics.descent - metrics.ascent).toFloat(),
            )
        }
    }

    private data class Key(
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
}

private val DEFAULT_FONT_SIZE = 14.sp
