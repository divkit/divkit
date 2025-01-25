package com.yandex.div.core.view2.spannable

import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivTextAlignmentVertical

internal data class SpanData(
    val start: Int,
    val end: Int,
    val alignmentVertical: DivTextAlignmentVertical?,
    @Px val baselineOffset: Int,
    val fontFamily: String?,
    val fontFeatureSettings: String?,
    @Px val fontSize: Int?,
    val fontSizeUnit: DivSizeUnit,
    val fontWeight: DivFontWeight?,
    val fontWeightValue: Int?,
    val letterSpacing: Double?,
    @Px val lineHeight: Int?,
    val strike: DivLineStyle?,
    @ColorInt val textColor: Int?,
    val textShadow: ShadowData?,
    @Px val topOffset: Int?,
    val topOffsetStart: Int?,
    val topOffsetEnd: Int?,
    val underline: DivLineStyle?
) : Comparable<SpanData> {

    fun mergeWith(span: SpanData, start: Int = span.start, end: Int = span.end): SpanData {
        return SpanData(
            start = start,
            end = end,
            alignmentVertical = span.alignmentVertical ?: alignmentVertical,
            baselineOffset = if (span.baselineOffset == DEFAULT_BASELINE_OFFSET) baselineOffset else span.baselineOffset,
            fontFamily = span.fontFamily ?: fontFamily,
            fontFeatureSettings = span.fontFeatureSettings ?: fontFeatureSettings,
            fontSize = span.fontSize ?: fontSize,
            fontSizeUnit = if (span.fontSizeUnit == DEFAULT_FONT_SIZE_UNIT) fontSizeUnit else span.fontSizeUnit,
            fontWeight = span.fontWeight ?: fontWeight,
            fontWeightValue = span.fontWeightValue ?: fontWeightValue,
            letterSpacing = span.letterSpacing ?: letterSpacing,
            lineHeight = span.lineHeight ?: lineHeight,
            strike = span.strike ?: strike,
            textColor = span.textColor ?: textColor,
            textShadow = span.textShadow ?: textShadow,
            topOffset = span.topOffset ?: topOffset,
            topOffsetStart = if (span.topOffset != null) span.topOffsetStart else topOffsetStart,
            topOffsetEnd = if (span.topOffset != null) span.topOffsetEnd else topOffsetEnd,
            underline = span.underline ?: underline
        )
    }

    override fun compareTo(other: SpanData): Int {
        return start - other.start
    }

    companion object {

        private const val DEFAULT_BASELINE_OFFSET: Int = 0
        private val DEFAULT_FONT_SIZE_UNIT: DivSizeUnit = DivSizeUnit.SP

        internal fun empty(start: Int, end: Int): SpanData {
            return SpanData(
                start = start,
                end = end,
                alignmentVertical = null,
                baselineOffset = DEFAULT_BASELINE_OFFSET,
                fontFamily = null,
                fontFeatureSettings = null,
                fontSize = null,
                fontSizeUnit = DEFAULT_FONT_SIZE_UNIT,
                fontWeight = null,
                fontWeightValue = null,
                letterSpacing = null,
                lineHeight = null,
                strike = null,
                textColor = null,
                textShadow = null,
                topOffset = null,
                topOffsetStart = null,
                topOffsetEnd = null,
                underline = null,
            )
        }
    }
}
