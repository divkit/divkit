package com.yandex.div.compose.context

import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivViewScope
internal class DivLocalComponentStorage @Inject constructor() {
    private val items = mutableMapOf<DivBase, Item>()

    private class Item(var component: DivLocalComponent? = null)

    fun get(div: DivBase): DivLocalComponent? {
        return items[div]?.component
    }

    fun put(div: DivBase, component: DivLocalComponent) {
        items.getOrPut(div) { Item() }.component = component
    }

    fun alias(original: DivBase, updated: DivBase) {
        items[updated] = items.getOrPut(original) { Item() }
    }

    fun retain(divs: Set<DivBase>) {
        val removedItems = items.values.toMutableSet()
        items.keys.retainAll(divs)
        removedItems.removeAll(items.values.toSet())
        removedItems.forEach { item ->
            item.component?.let { component ->
                component.triggerStorage.stopObserving()
                component.expressionResolver.clearObservers()
            }
        }
    }
}
