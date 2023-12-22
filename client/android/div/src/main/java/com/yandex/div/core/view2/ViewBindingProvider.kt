package com.yandex.div.core.view2

import com.yandex.div.DivDataTag
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div2.DivData
import javax.inject.Inject

/**
 * Allows observe data that is bound with [Div2View].
 */
@DivViewScope
internal class ViewBindingProvider @Inject constructor() {
    private var current = Binding(DivDataTag.INVALID, null)
    private val observers = mutableListOf<(Binding) -> Unit>()

    fun update(tag: DivDataTag, data: DivData?) {
        if (tag == current.tag && current.data === data) {
            return
        }

        current = Binding(tag, data)
        observers.forEach { it(current) }
    }

    fun observeAndGet(observer: (Binding) -> Unit) {
        observer(current)
        observers.add(observer)
    }
}

internal class Binding(
    val tag: DivDataTag,
    val data: DivData?,
)
