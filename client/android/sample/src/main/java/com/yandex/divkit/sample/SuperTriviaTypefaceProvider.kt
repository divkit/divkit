package com.yandex.divkit.sample

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.yandex.div.core.font.DivTypefaceProvider

class SuperTriviaTypefaceProvider(
    private val context: Context,
) : DivTypefaceProvider {
    override fun getRegular(): Typeface {
        return ResourcesCompat.getFont(context, R.font.super_trivia) ?: Typeface.DEFAULT
    }

    override fun getMedium(): Typeface {
        return ResourcesCompat.getFont(context, R.font.super_trivia) ?: Typeface.DEFAULT
    }

    override fun getLight(): Typeface {
        return ResourcesCompat.getFont(context, R.font.super_trivia) ?: Typeface.DEFAULT
    }

    override fun getBold(): Typeface {
        return ResourcesCompat.getFont(context, R.font.super_trivia) ?: Typeface.DEFAULT
    }
}
