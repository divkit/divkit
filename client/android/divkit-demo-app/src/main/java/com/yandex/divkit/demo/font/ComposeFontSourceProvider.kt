package com.yandex.divkit.demo.font

import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.divkit.demo.R

class ComposeFontSourceProvider : DivFontSourceProvider {

    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        if (fontFamilyName == ROBOTO_FLEX) {
            return DivFontSource.Resource(R.font.roboto_flex)
        }
        return DivFontSource.Resource(staticFontResource(fontFamilyName, weight))
    }

    private fun staticFontResource(fontFamilyName: String?, weight: FontWeight): Int = when (fontFamilyName) {
        DISPLAY -> displayFont(weight)
        CONDENSED -> condensedFont(weight)
        else -> textFont(weight)
    }

    private fun textFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> com.yandex.div.font.typeface.R.font.ys_text_light
        weight.weight <= FontWeight.Normal.weight -> com.yandex.div.font.typeface.R.font.ys_text_regular
        weight.weight <= FontWeight.Medium.weight -> com.yandex.div.font.typeface.R.font.ys_text_medium
        else -> com.yandex.div.font.typeface.R.font.ys_text_bold
    }

    private fun displayFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> com.yandex.div.font.typeface.R.font.ys_display_light
        weight.weight <= FontWeight.Normal.weight -> com.yandex.div.font.typeface.R.font.ys_display_regular
        weight.weight <= FontWeight.Medium.weight -> com.yandex.div.font.typeface.R.font.ys_display_medium
        else -> com.yandex.div.font.typeface.R.font.ys_display_bold
    }

    private fun condensedFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> R.font.ys_text_cond_light
        weight.weight <= FontWeight.Normal.weight -> R.font.ys_text_cond_regular
        weight.weight <= FontWeight.Medium.weight -> R.font.ys_text_cond_medium
        else -> R.font.ys_text_cond_bold
    }

    private companion object {
        const val DISPLAY = "display"
        const val CONDENSED = "condensed"
        const val ROBOTO_FLEX = "roboto_flex"
    }
}
