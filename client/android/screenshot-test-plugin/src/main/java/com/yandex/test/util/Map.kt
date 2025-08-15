package com.yandex.test.util

fun <K, V> Map<K, V>.filterKeysIn(vararg keys: K): Collection<V> {
    return filterKeys { it in keys }.values
}

fun <K, V> Map<K, V>.filterKeysNotIn(vararg keys: K): Collection<V> {
    return filterKeys { it !in keys }.values
}
