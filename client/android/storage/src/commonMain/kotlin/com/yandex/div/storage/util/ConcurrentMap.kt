package com.yandex.div.storage.util

internal expect class ConcurrentMap<K : Any, V : Any>() {
    val values: Collection<V>

    operator fun get(key: K): V?
    operator fun set(key: K, value: V)
    fun remove(key: K)
}
