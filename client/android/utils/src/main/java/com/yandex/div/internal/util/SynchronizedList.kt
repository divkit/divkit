package com.yandex.div.internal.util

import java.lang.Thread
import java.lang.ref.WeakReference

class SynchronizedList<T> @JvmOverloads constructor (
    ownerThread: Thread? = null
) {

    @PublishedApi
    internal val ownerThreadRef = WeakReference(ownerThread)

    @PublishedApi
    internal val list = mutableListOf<T>()

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

    @PublishedApi
    internal inline fun <R> doWithList(action: List<T>.() -> R): R {
        val listCopy = synchronized(list) {
            if (ownerThreadRef.get() == Thread.currentThread()) {
                return action(list)
            }
            list.toList()
        }

        return action(listCopy)
    }

    inline fun find(predicate: (T) -> Boolean): T? {
        return doWithList { find(predicate) }
    }

    inline fun forEach(action: (T) -> Unit) {
        doWithList { forEach(action) }
    }
}
