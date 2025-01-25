package com.yandex.div.core.state

import com.yandex.div.core.dagger.DivScope
import javax.inject.Inject

@DivScope
internal class TabsStateCache @Inject constructor() {
    private val temporaryCache = mutableMapOf<String, MutableMap<String, Int>>()

    fun getSelectedTab(cardId: String, path: String) = temporaryCache[cardId]?.get(path)

    fun putSelectedTab(cardId: String, path: String, index: Int) {
        temporaryCache.getOrPut(cardId) { mutableMapOf() } [path] = index
    }
}
