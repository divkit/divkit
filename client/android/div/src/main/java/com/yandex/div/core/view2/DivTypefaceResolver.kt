package com.yandex.div.core.view2

import android.graphics.Typeface
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.divs.getTypeface
import com.yandex.div.core.view2.divs.getTypefaceValue
import com.yandex.div2.DivFontWeight
import javax.inject.Inject

@Mockable
@DivScope
internal class DivTypefaceResolver @Inject constructor(
    private val typefaceProviders: Map<String, DivTypefaceProvider>,
    private val defaultTypeface: DivTypefaceProvider,
) {
    internal fun getTypeface(fontFamily: String?, fontWeight: DivFontWeight?, fontWeightValue: Int?): Typeface {
        val typefaceProvider = if (fontFamily == null) {
            defaultTypeface
        } else {
            typefaceProviders[fontFamily] ?: defaultTypeface
        }
        return getTypeface(getTypefaceValue(fontWeight, fontWeightValue), typefaceProvider)
    }
}

internal fun DivTypefaceResolver.getTypeface(
    fontFamily: String?,
    fontWeight: DivFontWeight?,
    fontWeightValue: Long?
): Typeface {
    return getTypeface(fontFamily, fontWeight, fontWeightValue?.toIntSafely())
}
