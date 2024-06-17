package com.yandex.div.core.util

import java.util.WeakHashMap

internal class SynchronizedWeakHashMap<K: Any, N: Any>: WeakHashMap<K, N>() {
    private val lock = Object()

    override val entries: MutableSet<MutableMap.MutableEntry<K, N>>
        get() = synchronized(lock) { super.entries }

    override val keys: MutableSet<K>
        get() = synchronized(lock) { super.keys }

    override val values: MutableCollection<N>
        get() = synchronized(lock) { super.values }

    override fun clear() = synchronized(lock) { super.clear() }

    override fun put(key: K, value: N): N? = synchronized(lock) { return super.put(key, value) }

    override fun putAll(from: Map<out K, N>) = synchronized(lock) { super.putAll(from) }

    override fun remove(key: K?): N? = synchronized(lock) { return super.remove(key) }

    override fun remove(key: K, value: N): Boolean =
        synchronized(lock) { return super.remove(key, value) }

    override fun get(key: K?): N? = synchronized(lock) { super.get(key) }

    fun createMap(): Map<K, N> = synchronized(lock) {
        this.entries.associate {
            it.key to it.value
        }
    }
}
