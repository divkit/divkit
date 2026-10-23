package com.yandex.div.test.data

import com.yandex.div.core.type.DataArray
import com.yandex.div.core.type.DataObject

/**
 * Creates a [DataObject] that is not backed by JSON, like the ones external providers and functions may return.
 */
fun dataObject(vararg entries: Pair<String, Any?>): DataObject = MapDataObject(linkedMapOf(*entries))

/**
 * Creates a [DataArray] that is not backed by JSON, like the ones external providers and functions may return.
 */
fun dataArray(vararg values: Any?): DataArray = ListDataArray(values.toList())

private class MapDataObject(private val map: Map<String, Any?>) : DataObject {

    override val size: Int get() = map.size
    override val keys: Iterable<String> get() = map.keys
    override val values: Collection<Any?> get() = map.values

    override fun isEmpty(): Boolean = map.isEmpty()
    override fun containsKey(key: String): Boolean = map.containsKey(key)
    override fun containsValue(value: Any?): Boolean = map.containsValue(value)
    override fun get(key: String): Any = map[key] ?: throw NoSuchElementException("No value for key '$key'")
    override fun opt(key: String): Any? = map[key]
    override fun iterator(): Iterator<Pair<String, Any?>> = map.entries.map { it.toPair() }.iterator()
}

private class ListDataArray(private val list: List<Any?>) : DataArray, Collection<Any?> by list {

    override fun get(index: Int): Any = list[index] ?: throw NoSuchElementException("No value at index $index")
    override fun opt(index: Int): Any? = list.getOrNull(index)
    override fun indexOf(element: Any?): Int = list.indexOf(element)
}
