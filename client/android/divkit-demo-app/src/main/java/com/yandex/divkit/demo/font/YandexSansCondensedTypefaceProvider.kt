package com.yandex.divkit.demo.font

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.divkit.demo.R

class YandexSansCondensedTypefaceProvider(
    private val context: Context
) : DivTypefaceProvider {

    private val lightTypeface by lazy {
        ResourcesCompat.getFont(context, R.font.ys_text_cond_light) ?: Typeface.DEFAULT
    }

    private val regularTypeface by lazy {
        ResourcesCompat.getFont(context, R.font.ys_text_cond_regular) ?: Typeface.DEFAULT
    }

    private val mediumTypeface by lazy {
        ResourcesCompat.getFont(context, R.font.ys_text_cond_medium) ?: Typeface.DEFAULT
    }

    private val boldTypeface by lazy {
        ResourcesCompat.getFont(context, R.font.ys_text_cond_bold) ?: Typeface.DEFAULT
    }

    override fun getLight(): Typeface = lightTypeface

    override fun getRegular(): Typeface = regularTypeface

    override fun getMedium(): Typeface = mediumTypeface

    override fun getBold(): Typeface = boldTypeface
}
