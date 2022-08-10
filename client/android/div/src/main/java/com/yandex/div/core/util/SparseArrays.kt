package com.yandex.div.core.util

import androidx.collection.SparseArrayCompat

private class SparseArrayIterator<T>(
    private val array: SparseArrayCompat<T>
) : Iterator<T> {

    private var index = 0

    override fun hasNext(): Boolean {
        return array.size() > index
    }

    override fun next(): T {
        return array.valueAt(index++)
    }
}

internal class SparseArrayIterable<T>(
    private val array: SparseArrayCompat<T>
) : Iterable<T> {

    override fun iterator(): Iterator<T> = SparseArrayIterator(array)
}

internal fun <T> SparseArrayCompat<T>.toIterable() : Iterable<T> {
    return SparseArrayIterable(this)
}
