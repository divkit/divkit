package com.yandex.div.core.view2

import android.graphics.Typeface
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.view2.divs.getTypeface
import com.yandex.div2.DivFontFamily
import com.yandex.div2.DivFontWeight
import javax.inject.Inject
import javax.inject.Named

@Mockable
@DivScope
internal class DivTypefaceResolver @Inject constructor(
        private val regularTypefaceProvider: DivTypefaceProvider,
        @Named(Names.TYPEFACE_DISPLAY) private val displayTypefaceProvider: DivTypefaceProvider
) {
    internal fun getTypeface(fontFamily: DivFontFamily, fontWeight: DivFontWeight): Typeface {
        val typefaceProvider = when (fontFamily) {
            DivFontFamily.DISPLAY -> displayTypefaceProvider
            else -> regularTypefaceProvider
        }
        return getTypeface(fontWeight, typefaceProvider)
    }
}
