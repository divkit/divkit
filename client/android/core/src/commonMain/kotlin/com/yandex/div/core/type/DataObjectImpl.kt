package com.yandex.div.core.type

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.json.missingValue

/**
 * [DataObject] backed by a map of normalized values, see [DataObject.of].
 *
 * Instances are equal only to themselves.
 */
@ExperimentalApi
internal class DataObjectImpl(
    private val source: Map<String, Any?>
) : DataObject {

    override val size: Int
        get() = source.size

    override val keys: Iterable<String>
        get() = source.keys

    override val values: Collection<Any?>
        get() = source.values

    override fun isEmpty(): Boolean = source.isEmpty()

    override fun containsKey(key: String): Boolean = source.containsKey(key)

    override fun containsValue(value: Any?): Boolean = source.containsValue(value)

    override fun get(key: String): Any = opt(key) ?: throw missingValue(key)

    override fun opt(key: String): Any? = source[key]

    override fun iterator(): Iterator<Pair<String, Any?>> {
        return source.entries.asSequence().map { (key, value) -> key to value }.iterator()
    }

    override fun toString(): String = toJsonString()
}
