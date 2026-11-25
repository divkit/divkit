package com.yandex.div.storage.util

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

internal actual class ConcurrentMap<K : Any, V : Any> actual constructor() {
    private val lock = reentrantLock()
    private val map = mutableMapOf<K, V>()

    actual val values: Collection<V>
        get() = lock.withLock { map.values.toList() }

    actual operator fun get(key: K): V? = lock.withLock { map[key] }

    actual operator fun set(key: K, value: V) {
        lock.withLock { map[key] = value }
    }

    actual fun remove(key: K) {
        lock.withLock { map.remove(key) }
    }
}
