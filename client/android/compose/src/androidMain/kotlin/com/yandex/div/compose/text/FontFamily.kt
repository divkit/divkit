package com.yandex.div.compose.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.LocalDivContext

@Composable
internal fun rememberFontFamily(font: FontParams): FontFamily {
    val provider = LocalDivContext.current.component.fontFamilyProvider
    return remember(provider, font) {
        provider.getFontFamily(
            name = font.family,
            weight = font.weight ?: FontWeight.Normal,
            variations = buildFontVariationSettings(font),
        )
    }
}

private fun buildFontVariationSettings(font: FontParams): FontVariation.Settings? {
    val weight = font.weight
    val hasWeight = weight != null
    val variations = font.variationSettings
    val hasVariations = variations != null && variations.length() > 0
    if (!hasWeight && !hasVariations) {
        return null
    }

    val settings = mutableListOf<FontVariation.Setting>()
    if (hasWeight && variations?.has(WEIGHT_AXIS) != true) {
        settings += FontVariation.weight(weight.weight)
    }

    if (variations != null) {
        for (axis in variations.keys()) {
            val raw = variations.opt(axis) as? Number ?: continue
            settings += if (axis == WEIGHT_AXIS) {
                FontVariation.weight(raw.toInt().coerceIn(1, 1000))
            } else {
                FontVariation.Setting(axis, raw.toFloat())
            }
        }
    }

    return if (settings.isEmpty()) null else FontVariation.Settings(*settings.toTypedArray())
}

private const val WEIGHT_AXIS = "wght"
