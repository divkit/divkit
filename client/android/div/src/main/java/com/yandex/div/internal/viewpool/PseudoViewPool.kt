package com.yandex.div.internal.viewpool

import android.view.View
import com.yandex.div.internal.util.getOrThrow
import java.util.concurrent.ConcurrentHashMap

internal class PseudoViewPool : ViewPool {

    private val factoryMap = ConcurrentHashMap<String, ViewFactory<out View>>()

    override fun <T : View> register(tag: String, factory: ViewFactory<T>, capacity: Int) {
        factoryMap[tag] = factory
    }

    override fun unregister(tag: String) {
        factoryMap.remove(tag)
    }

    override fun <T : View> obtain(tag: String): T {
        @Suppress("UNCHECKED_CAST")
        return factoryMap.getOrThrow(tag).createView() as T
    }

    override fun changeCapacity(tag: String, newCapacity: Int) = Unit
}
