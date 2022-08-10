package com.yandex.div.core.widget

internal inline fun IntArray.update(indices: IntRange = 0 until size, action: (Int) -> Int) {
    for (index in indices) {
        val element = get(index)
        set(index, action(element))
    }
}

internal inline fun IntArray.update(offset: Int, length: Int, action: (Int) -> Int) {
    for (index in offset until offset + length) {
        val element = get(index)
        set(index, action(element))
    }
}

/**
 * 100% iterator-free list traversal
 */
internal inline fun <T> List<T>.iterate(action: (T) -> Unit) {
    for (index in 0 until size) {
        val element = get(index)
        action(element)
    }
}
