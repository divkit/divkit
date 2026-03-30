package com.yandex.div.compose.context

import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div2.DivData
import javax.inject.Inject

@DivContextScope
internal class DivViewContextStorage @Inject constructor() {
    private val items = mutableMapOf<DivData, DivViewContext>()

    fun get(data: DivData): DivViewContext? {
        return items[data]
    }

    fun put(data: DivData, context: DivViewContext) {
        items[data] = context
    }

    fun remove(data: DivData) {
        items.remove(data)
    }
}
