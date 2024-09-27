package com.yandex.div.core.expression.local

internal object ChildPathUnitCache {
    private var cache = ArrayList<String>()

    private fun ensureGenerated(index: Int) {
        if (cache.size > index) return

        cache.ensureCapacity(index + 1)
        for (i in cache.size..index) {
            cache.add(i, "child#$i")
        }
    }

    internal fun getValue(index: Int): String {
        ensureGenerated(index)
        return cache[index]
    }
}
