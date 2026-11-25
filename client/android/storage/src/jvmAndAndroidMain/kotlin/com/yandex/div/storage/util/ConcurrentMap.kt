package com.yandex.div.storage.util

import java.util.concurrent.ConcurrentHashMap

internal actual class ConcurrentMap<K : Any, V : Any> actual constructor() {
    private val map = ConcurrentHashMap<K, V>()

    actual val values: Collection<V>
        get() = map.values

    actual operator fun get(key: K): V? = map[key]

    actual operator fun set(key: K, value: V) {
        map[key] = value
    }

    actual fun remove(key: K) {
        map.remove(key)
    }
}
