package com.yandex.div.internal.util

import androidx.collection.ArrayMap
import java.util.Collections

fun <K, V> arrayMap(): MutableMap<K, V> = ArrayMap<K, V>()

fun <K, V> arrayMap(initialCapacity: Int): MutableMap<K, V> = ArrayMap<K, V>(initialCapacity)

fun <K, V> arrayMap(source: Map<K, V>): MutableMap<K, V> {
    return ArrayMap<K, V>(source.size).apply { putAll(source) }
}

inline fun <T> List<T>?.whenNotEmpty(action: (List<T>) -> Unit) {
    if (this != null && isNotEmpty()) {
        action(this)
    }
}

fun <T> List<T>.immutableCopy(): List<T> {
    return if (this is MutableList<T>) {
        Collections.unmodifiableList(ArrayList(this))
    } else {
        this
    }
}

fun allIsNullOrEmpty(vararg items: List<*>?) = items.all { it.isNullOrEmpty() }

fun <K, V> Map<out K, V>.getOrThrow(key: K, message: String? = null): V {
    return get(key) ?: throw NoSuchElementException(message)
}
