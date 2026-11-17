package com.yandex.div.compose.text

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.resolveAsTypeface
import com.yandex.div.compose.LocalDivContext

@Composable
internal fun rememberFontFamily(font: FontParams): FontFamily {
    val component = LocalDivContext.current.component
    val provider = component.fontSourceProvider
    val cache = component.fontFamilyCache
    return remember(font) {
        val weight = font.weight ?: FontWeight.Normal
        cache.getOrCreate(
            source = provider.getFontSource(font.family, weight),
            weight = weight,
            variations = buildFontVariationSettings(font),
        )
    }
}

/**
 * The platform typeface of the font that [rememberFontFamily] resolves for the same inputs,
 * for text drawn with a native [android.graphics.Paint] instead of a Compose text composable.
 */
@Composable
internal fun rememberTypeface(font: FontParams): Typeface {
    val fontFamily = rememberFontFamily(font)
    val fontFamilyResolver = LocalFontFamilyResolver.current
    return remember(fontFamily, fontFamilyResolver) {
        fontFamilyResolver.resolveAsTypeface(
            fontFamily = fontFamily,
            fontWeight = font.weight ?: FontWeight.Normal,
        ).value
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
