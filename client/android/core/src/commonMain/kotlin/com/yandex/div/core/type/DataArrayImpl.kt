package com.yandex.div.core.type

import com.yandex.div.core.json.missingValue

/**
 * [DataArray] backed by a list of normalized values, see [DataArray.of].
 *
 * Instances are equal only to themselves.
 */
internal class DataArrayImpl(
    private val source: List<Any?>
) : DataArray {

    override val size: Int
        get() = source.size

    override fun isEmpty(): Boolean = source.isEmpty()

    override fun contains(element: Any?): Boolean = source.contains(element)

    override fun containsAll(elements: Collection<Any?>): Boolean = source.containsAll(elements)

    override fun indexOf(element: Any?): Int = source.indexOf(element)

    override fun iterator(): Iterator<Any?> = source.iterator()

    override fun get(index: Int): Any = opt(index) ?: throw missingValue(index)

    override fun opt(index: Int): Any? = source.getOrNull(index)

    override fun toString(): String = toJsonString()
}
