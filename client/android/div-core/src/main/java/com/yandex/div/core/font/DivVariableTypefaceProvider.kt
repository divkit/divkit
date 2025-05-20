package com.yandex.div.core.font

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.core.graphics.TypefaceCompat
import com.yandex.div.core.font.DivTypefaceProvider.Weight

public abstract class DivVariableTypefaceProvider(private val context: Context) : DivTypefaceProvider {

    private val supportFontVariations = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    protected abstract val typeface: Typeface?

    override fun isVariable(): Boolean = true

    override fun getRegular(): Typeface? = createTypefaceFor(Weight.REGULAR)

    override fun getMedium(): Typeface? = createTypefaceFor(Weight.MEDIUM)

    override fun getLight(): Typeface? = createTypefaceFor(Weight.LIGHT)

    override fun getBold(): Typeface? = createTypefaceFor(Weight.BOLD)

    override fun getTypefaceFor(weight: Int): Typeface? =
        if (supportFontVariations) typeface else createTypefaceFor(weight)

    private fun createTypefaceFor(weight: Int) = TypefaceCompat.create(context, typeface, weight, false)
}
