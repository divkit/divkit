package com.yandex.div.compose.context

import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivBase
import javax.inject.Inject

@DivViewScope
internal class DivLocalComponentStorage @Inject constructor() {
    private val items = mutableMapOf<DivBase, DivLocalComponent>()

    fun get(div: DivBase): DivLocalComponent? {
        return items[div]
    }

    fun put(div: DivBase, component: DivLocalComponent) {
        items[div] = component
    }
}
