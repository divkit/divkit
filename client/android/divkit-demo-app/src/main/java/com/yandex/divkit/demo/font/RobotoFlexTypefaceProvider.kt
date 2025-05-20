package com.yandex.divkit.demo.font

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.yandex.div.core.font.DivVariableTypefaceProvider
import com.yandex.divkit.demo.R

class RobotoFlexTypefaceProvider(context: Context) : DivVariableTypefaceProvider(context) {
    override val typeface = ResourcesCompat.getFont(context, R.font.roboto_flex)
}
