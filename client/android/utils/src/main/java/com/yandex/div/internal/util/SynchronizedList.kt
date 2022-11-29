package com.yandex.div.internal.util

class SynchronizedList<T> {

    @PublishedApi internal val list = mutableListOf<T>()

    fun add(value: T) {
        synchronized(list) {
            list.add(value)
        }
    }

    fun remove(value: T) {
        synchronized(list) {
            list.remove(value)
        }
    }

    fun clear() {
        synchronized(list) {
            list.clear()
        }
    }

    inline fun find(predicate: (T) -> Boolean): T? {
        val listCopy = mutableListOf<T>()

        synchronized(list) {
            listCopy.addAll(list)
        }

        return listCopy.find(predicate)
    }

    inline fun forEach(callback: (T) -> Unit) = forEachAnd(callback = callback)

    inline fun forEachAndClear(callback: (T) -> Unit) = forEachAnd(this::clear, callback)

    @PublishedApi
    internal inline fun forEachAnd(listCallback: () -> Unit = {}, callback: (T) -> Unit) {
        val listCopy: List<T>?

        synchronized(list) {
            listCopy = list.toList()
            listCallback()
        }

        listCopy?.forEach { callback(it) }
    }
}
