package com.yandex.div.compose.context

import com.yandex.div2.DivBase

internal class DivLocalContextStorage() {
    private val items = mutableMapOf<DivBase, DivLocalContext>()

    fun get(div: DivBase): DivLocalContext? {
        return items[div]
    }

    fun put(div: DivBase, context: DivLocalContext) {
        items[div] = context
    }
}
