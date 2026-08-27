package com.yandex.div.compose.screenshot

import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.font.typeface.R

class YandexSansFontSourceProvider : DivFontSourceProvider {

    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        val fontRes = when (fontFamilyName) {
            "display" -> displayFont(weight)
            else -> textFont(weight)
        }
        return DivFontSource.Resource(fontRes)
    }

    private fun textFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> R.font.ys_text_light
        weight.weight <= FontWeight.Normal.weight -> R.font.ys_text_regular
        weight.weight <= FontWeight.Medium.weight -> R.font.ys_text_medium
        else -> R.font.ys_text_bold
    }

    private fun displayFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> R.font.ys_display_light
        weight.weight <= FontWeight.Normal.weight -> R.font.ys_display_regular
        weight.weight <= FontWeight.Medium.weight -> R.font.ys_display_medium
        else -> R.font.ys_display_bold
    }
}
