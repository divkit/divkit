package com.yandex.div.compose.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.context.divContext
import com.yandex.div2.DivFontWeight
import org.json.JSONObject

@Composable
internal fun rememberFontFamily(
    fontFamily: String?,
    fontWeight: DivFontWeight?,
    fontWeightValue: Int?,
    fontVariationSettings: JSONObject? = null,
): FontFamily {
    val provider = divContext.component.fontSourceProvider
    val cache = divContext.component.fontFamilyCache
    return remember(fontFamily, fontWeight, fontWeightValue, fontVariationSettings) {
        val weight = fontWeight.toFontWeight(fontWeightValue) ?: FontWeight.Normal
        val variations = buildFontVariationSettings(fontWeight, fontWeightValue, fontVariationSettings)
        val source = provider.getFontSource(fontFamily, weight)
        cache.getOrCreate(source, weight, variations)
    }
}

private fun buildFontVariationSettings(
    fontWeight: DivFontWeight?,
    fontWeightValue: Int?,
    fontVariations: JSONObject?,
): FontVariation.Settings? {
    val hasVariations = fontVariations != null && fontVariations.length() > 0
    val hasWeight = fontWeight != null || fontWeightValue != null
    if (!hasVariations && !hasWeight) return null

    val settings = mutableListOf<FontVariation.Setting>()
    if (hasWeight && fontVariations?.has(WEIGHT_AXIS) != true) {
        settings += FontVariation.weight(getTypefaceWeight(fontWeight, fontWeightValue))
    }

    if (fontVariations != null) {
        for (axis in fontVariations.keys()) {
            val raw = fontVariations.opt(axis) as? Number ?: continue
            
            settings += if (axis == WEIGHT_AXIS) {
                FontVariation.weight(raw.toInt().coerceIn(1, 1000))
            } else {
                FontVariation.Setting(axis, raw.toFloat())
            }
        }
    }

    if (settings.isEmpty()) return null
    return FontVariation.Settings(*settings.toTypedArray())
}

private fun getTypefaceWeight(fontWeight: DivFontWeight?, fontWeightValue: Int?): Int {
    fontWeightValue?.let { return it.coerceIn(1, 1000) }
    return when (fontWeight) {
        DivFontWeight.LIGHT -> 300
        DivFontWeight.MEDIUM -> 500
        DivFontWeight.BOLD -> 700
        DivFontWeight.REGULAR, null -> 400
    }
}

private const val WEIGHT_AXIS = "wght"
