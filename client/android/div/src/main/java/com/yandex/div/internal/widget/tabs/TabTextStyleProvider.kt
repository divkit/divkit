package com.yandex.div.internal.widget.tabs

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.font.DivTypefaceProvider
import javax.inject.Inject

@DivScope
internal class TabTextStyleProvider @Inject constructor(
    private val typefaceProvider: DivTypefaceProvider
) {
    fun getTypefaceProvider() = typefaceProvider
}
