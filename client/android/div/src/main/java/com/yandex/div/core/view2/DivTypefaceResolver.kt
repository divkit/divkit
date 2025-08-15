package com.yandex.div.core.view2

import android.graphics.Typeface
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.font.DivTypefaceProvider.Weight
import com.yandex.div2.DivFontWeight
import javax.inject.Inject

@Mockable
@DivScope
internal class DivTypefaceResolver @Inject constructor(
    private val typefaceProviders: Map<String, DivTypefaceProvider>,
    private val defaultTypeface: DivTypefaceProvider,
) {
    fun getTypefaceProvider(fontFamily: String?) = fontFamily?.let { typefaceProviders[it] } ?: defaultTypeface
}

internal fun getTypeface(fontWeight: Int, typefaceProvider: DivTypefaceProvider) =
    typefaceProvider.getTypefaceFor(fontWeight) ?: Typeface.DEFAULT

internal fun getTypeface(fontWeight: DivFontWeight?, fontWeightValue: Int?, typefaceProvider: DivTypefaceProvider) =
    getTypeface(getTypefaceValue(fontWeight, fontWeightValue), typefaceProvider)

internal fun getTypefaceValue(fontWeight: DivFontWeight?, fontWeightValue: Int?): Int {
    return fontWeightValue ?: when (fontWeight) {
        DivFontWeight.LIGHT -> Weight.LIGHT
        DivFontWeight.REGULAR -> Weight.REGULAR
        DivFontWeight.MEDIUM -> Weight.MEDIUM
        DivFontWeight.BOLD -> Weight.BOLD
        else -> Weight.REGULAR
    }
}
