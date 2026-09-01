package com.yandex.divkit.demo.font

import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.font.DivFontSource
import com.yandex.div.compose.font.DivFontSourceProvider
import com.yandex.div.font.typeface.R as fontR
import com.yandex.div.test.R as testR

class ComposeFontSourceProvider : DivFontSourceProvider {

    override fun getFontSource(fontFamilyName: String?, weight: FontWeight): DivFontSource {
        val fontRes = when (fontFamilyName) {
            "condensed" -> condensedFont(weight)
            "roboto_flex" -> testR.font.roboto_flex
            "text", null -> defaultFont(weight)
            else -> throw RuntimeException("Invalid font_family: $fontFamilyName")
        }
        return DivFontSource.Resource(fontRes)
    }

    private fun condensedFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> testR.font.ys_text_cond_light
        weight.weight <= FontWeight.Normal.weight -> testR.font.ys_text_cond_regular
        weight.weight <= FontWeight.Medium.weight -> testR.font.ys_text_cond_medium
        else -> testR.font.ys_text_cond_bold
    }

    private fun defaultFont(weight: FontWeight): Int = when {
        weight.weight <= FontWeight.Light.weight -> fontR.font.ys_text_light
        weight.weight <= FontWeight.Normal.weight -> fontR.font.ys_text_regular
        weight.weight <= FontWeight.Medium.weight -> fontR.font.ys_text_medium
        else -> fontR.font.ys_text_bold
    }
}
