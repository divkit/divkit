package com.yandex.div.compose.context

import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivViewScope
internal class CompositionKeyStorage @Inject constructor() {
    private val keys = mutableMapOf<DivBase, Long>()
    private var nextKey = 0L

    fun get(data: DivBase): Long = keys.getOrPut(data) { nextKey++ }

    fun alias(original: DivBase, updated: DivBase) {
        keys[updated] = get(original)
    }

    fun retain(divs: Set<DivBase>) {
        keys.keys.retainAll(divs)
    }
}
