package com.yandex.div.core.type

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
public interface DataArray : Collection<Any?> {

    public fun get(index: Int): Any
    public fun opt(index: Int): Any?
    public fun indexOf(element: Any?): Int

    public companion object {

        @JvmStatic
        public fun empty(): DataArray {
            return EmptyDataArray
        }
    }
}

private object EmptyDataArray : DataArray {

    override val size: Int = 0

    override fun isEmpty(): Boolean = true
    override fun contains(element: Any?): Boolean = false
    override fun containsAll(elements: Collection<Any?>): Boolean = elements.isEmpty()
    override fun get(index: Int): Any = throw NoSuchElementException()
    override fun opt(index: Int): Any? = null
    override fun indexOf(element: Any?): Int = -1
    override fun iterator(): Iterator<Any?> = emptyList<Any?>().iterator()
}
