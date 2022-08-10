package com.yandex.div.core.view.tabs

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.font.DivTypefaceProvider
import javax.inject.Inject

@DivScope
class TabTextStyleProvider @Inject constructor(
    private val typefaceProvider: DivTypefaceProvider
) {
    fun getTypefaceProvider() = typefaceProvider
}
