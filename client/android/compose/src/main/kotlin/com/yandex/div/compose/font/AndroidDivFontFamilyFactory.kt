package com.yandex.div.compose.font

import android.content.res.AssetManager
import android.graphics.Typeface
import android.os.Build
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
internal class AndroidDivFontFamilyFactory @Inject constructor(
    private val assets: AssetManager,
    private val reporter: DivReporter,
) : DivFontFamilyFactory {

    @OptIn(ExperimentalTextApi::class)
    override fun create(
        source: DivFontSource,
        weight: FontWeight,
        variations: FontVariation.Settings?,
    ): FontFamily {
        val settings = variations ?: FontVariation.Settings()
        return when (source) {
            is DivFontSource.Resource ->
                FontFamily(Font(source.resId, weight, variationSettings = settings))
            is DivFontSource.Asset ->
                FontFamily(Font(source.path, assets, weight, variationSettings = settings))
            is DivFontSource.Typeface -> {
                if (variations != null) {
                    reporter.reportWarning(
                        "font_variation_settings cannot be applied to a Typeface. " +
                                "Use DivFontSource.Resource or DivFontSource.Asset " +
                                "for variable fonts."
                    )
                }
                FontFamily(source.typeface.withWeight(weight))
            }
        }
    }
}

private fun Typeface.withWeight(weight: FontWeight): Typeface {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return this
    return Typeface.create(this, weight.weight, isItalic)
}
