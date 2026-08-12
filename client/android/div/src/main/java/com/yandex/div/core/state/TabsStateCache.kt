package com.yandex.div.core.state

import com.yandex.div.core.dagger.DivDataScope
import javax.inject.Inject

@DivDataScope
internal class TabsStateCache @Inject constructor() {
    private val temporaryCache = mutableMapOf<String, Int>()

    fun getSelectedTab(path: String) = temporaryCache[path]

    fun putSelectedTab(path: String, index: Int) {
        temporaryCache[path] = index
    }
}
