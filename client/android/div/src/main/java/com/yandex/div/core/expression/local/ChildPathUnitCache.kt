package com.yandex.div.core.expression.local

internal object ChildPathUnitCache {
    private val cache = ArrayList<String>()

    @Synchronized
    internal fun getValue(index: Int): String {
        while (cache.size <= index) {
            cache.add("child#${cache.size}")
        }
        return cache[index]
    }
}
