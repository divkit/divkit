package com.yandex.div.util

class SynchronizedList<T> {

    private val list = mutableListOf<T>()

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

    fun forEach(callback: (T) -> Unit) {
        val listCopy = ArrayList<T>(list.size)

        synchronized(list) {
            listCopy.addAll(list)
        }

        listCopy.forEach { callback(it) }
    }
}
