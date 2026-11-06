package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.font.LineMetrics
import com.yandex.div.compose.utils.toPx
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivText
import com.yandex.div2.DivTextAlignmentVertical
import kotlin.math.roundToInt

@Immutable
internal data class RangeMetricsInput(
    val start: Int,
    val end: Int,
    val spanStyle: SpanStyle,
    val lineHeightPx: Int?,
    val topOffsetPx: Int?,
    val baselineOffsetPx: Int,
    val alignment: DivTextAlignmentVertical?,
) {
    val hasMetrics: Boolean
        get() = lineHeightPx != null || topOffsetPx != null || baselineOffsetPx != 0 ||
            alignment != null
}

@Immutable
internal data class TextRangeMetrics(
    val start: Int,
    val end: Int,
    val lineHeightPx: Int?,
    val topOffsetPx: Int,
    val baselineOffsetPx: Int,
    val alignment: DivTextAlignmentVertical?,
    val font: LineMetrics?,
) {
    val needsLayout: Boolean get() = lineHeightPx != null || topOffsetPx > 0 || alignment != null
}

@Composable
internal fun rememberRangeMetrics(
    ranges: List<RangeMetricsInput>,
    textStyle: TextStyle,
): List<TextRangeMetrics> {
    val cache = divContext.component.lineMetricsCache
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val density = LocalDensity.current
    return remember(ranges, textStyle, cache, fontFamilyResolver, density) {
        resolveRangeMetrics(ranges) { spanStyle ->
            cache.getOrCreate(fontFamilyResolver, density, textStyle.merge(spanStyle))
        }
    }
}

private fun resolveRangeMetrics(
    ranges: List<RangeMetricsInput>,
    fontMetrics: (SpanStyle) -> LineMetrics,
): List<TextRangeMetrics> {
    if (ranges.none { it.hasMetrics }) return emptyList()
    val bounds = ranges.flatMap { listOf(it.start, it.end) }.distinct().sorted()
    return bounds.zipWithNext().mapNotNull { (start, end) ->
        val covering = ranges.filter { it.start <= start && end <= it.end }
        val lineHeight = covering.lastOrNull { it.lineHeightPx != null }?.lineHeightPx
        val topOffset = covering.lastOrNull { it.topOffsetPx != null }
            ?.takeIf { it.start == start }?.topOffsetPx ?: 0
        val baselineOffset = covering.lastOrNull { it.baselineOffsetPx != 0 }
            ?.baselineOffsetPx ?: 0
        val alignment = covering.lastOrNull { it.alignment != null }?.alignment
            ?.takeIf { baselineOffset == 0 && it != DivTextAlignmentVertical.BASELINE }
        val hasShift = baselineOffset != 0 || alignment != null
        if (lineHeight == null && topOffset <= 0 && !hasShift) {
            return@mapNotNull null
        }
        val font = if (hasShift) {
            fontMetrics(covering.fold(SpanStyle()) { style, range -> style.merge(range.spanStyle) })
        } else {
            null
        }
        TextRangeMetrics(
            start = start,
            end = end,
            lineHeightPx = lineHeight,
            topOffsetPx = topOffset,
            baselineOffsetPx = baselineOffset,
            alignment = alignment,
            font = font,
        )
    }
}

@Composable
internal fun DivText.Range.observeFontSizeUnit(component: DivLocalComponent): DivSizeUnit {
    val usesUnit = fontSize != null || lineHeight != null || topOffset != null ||
        (baselineOffset as? Expression.ConstantExpression)?.value != 0.0
    return if (usesUnit) fontSizeUnit.observedValue(component) else DivSizeUnit.SP
}

@Composable
internal fun DivText.Range.observeMetrics(
    start: Int,
    end: Int,
    spanStyle: SpanStyle,
    fontSizeUnit: DivSizeUnit,
    component: DivLocalComponent,
): RangeMetricsInput {
    val baselineOffsetPx = baselineOffset.observedValue(component).toFloat().toPx(fontSizeUnit).roundToInt()
    return RangeMetricsInput(
        start = start,
        end = end,
        spanStyle = spanStyle,
        lineHeightPx = lineHeight?.observedValue(component)?.toPx(fontSizeUnit)?.roundToInt()?.takeIf { it > 0 },
        topOffsetPx = topOffset?.observedValue(component)?.toPx(fontSizeUnit)?.roundToInt(),
        baselineOffsetPx = baselineOffsetPx,
        alignment = if (baselineOffsetPx == 0) alignmentVertical?.observedValue(component) else null,
    )
}
