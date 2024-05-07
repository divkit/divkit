package com.yandex.div.internal.util

import androidx.collection.ArrayMap
import com.yandex.div.core.annotations.InternalApi
import java.util.Collections

@InternalApi
public fun <K, V> arrayMap(): MutableMap<K, V> = ArrayMap<K, V>()

@InternalApi
public fun <K, V> arrayMap(initialCapacity: Int): MutableMap<K, V> = ArrayMap<K, V>(initialCapacity)

@InternalApi
public fun <K, V> arrayMap(source: Map<K, V>): MutableMap<K, V> {
    return ArrayMap<K, V>(source.size).apply { putAll(source) }
}

@InternalApi
public inline fun <T> List<T>?.whenNotEmpty(action: (List<T>) -> Unit) {
    if (this != null && isNotEmpty()) {
        action(this)
    }
}

@InternalApi
public fun <T> List<T>.immutableCopy(): List<T> {
    return if (this is MutableList<T>) {
        Collections.unmodifiableList(ArrayList(this))
    } else {
        this
    }
}

@InternalApi
public fun allIsNullOrEmpty(vararg items: List<*>?): Boolean = items.all { it.isNullOrEmpty() }

@InternalApi
public fun <K, V> Map<out K, V>.getOrThrow(key: K, message: String? = null): V {
    return get(key) ?: throw NoSuchElementException(message)
}

@InternalApi
public inline fun <T> List<T>.compareWith(other: List<T>, comparator: (T, T) -> Boolean): Boolean {
    if (this.size != other.size) {
        return false
    }

    this.forEachIndexed { index, item ->
        if (!comparator(item, other[index])) return false
    }
    return true
}
