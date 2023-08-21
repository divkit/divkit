package com.yandex.div.core.view2

import android.graphics.Typeface
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.view2.divs.getTypeface
import com.yandex.div2.DivFontWeight
import javax.inject.Inject

@Mockable
@DivScope
internal class DivTypefaceResolver @Inject constructor(
    private val typefaceProviders: Map<String, DivTypefaceProvider>,
    private val defaultTypeface: DivTypefaceProvider,
) {
    internal fun getTypeface(fontFamily: String?, fontWeight: DivFontWeight): Typeface {
        val typefaceProvider = if (fontFamily == null) {
            defaultTypeface
        } else {
            typefaceProviders[fontFamily] ?: defaultTypeface
        }
        return getTypeface(fontWeight, typefaceProvider)
    }
}
