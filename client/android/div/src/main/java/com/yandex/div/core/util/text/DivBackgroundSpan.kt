package com.yandex.div.core.util.text

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.UnderlineSpan
import androidx.annotation.Px
import com.yandex.div2.DivTextAlignmentVertical
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder

internal class DivBackgroundSpan(
    val border: DivTextRangeBorder?,
    val background: DivTextRangeBackground?,
    @Px val baselineOffset: Int,
    val alignmentVertical: DivTextAlignmentVertical?,
    @Px val lineHeight: Int?,
    @Px val fontSize: Int?,
    @Px val topOffset: Int?,
    val typeface: Typeface?,
    val fontFeatureSettings: String?,
    val fontVariationSettings: String?,
) : UnderlineSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}
