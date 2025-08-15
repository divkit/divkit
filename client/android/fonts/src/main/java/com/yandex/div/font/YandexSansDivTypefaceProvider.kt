package com.yandex.div.font

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.font.typeface.R
import javax.inject.Inject

class YandexSansDivTypefaceProvider @Inject constructor(
    private val context: Context
) : DivTypefaceProvider {

    override fun getRegular(): Typeface {
        return ResourcesCompat.getFont(context, R.font.ys_text_regular) ?: Typeface.DEFAULT
    }

    override fun getMedium(): Typeface {
        return ResourcesCompat.getFont(context, R.font.ys_text_medium) ?: Typeface.DEFAULT
    }

    override fun getLight(): Typeface {
        return ResourcesCompat.getFont(context, R.font.ys_text_light) ?: Typeface.DEFAULT
    }

    override fun getBold(): Typeface {
        return ResourcesCompat.getFont(context, R.font.ys_text_bold) ?: Typeface.DEFAULT
    }
}
