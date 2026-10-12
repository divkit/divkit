package com.yandex.div.core.type

import com.yandex.div.core.annotations.ExperimentalApi

@ExperimentalApi
public interface DataObject : Iterable<Pair<String, Any?>> {

    public val size: Int
    public val keys: Iterable<String>
    public val values: Collection<Any?>

    public fun isEmpty(): Boolean
    public fun containsKey(key: String): Boolean
    public fun containsValue(value: Any?): Boolean
    public fun get(key: String): Any
    public fun opt(key: String): Any?

    public companion object {

        @JvmStatic
        public fun empty(): DataObject {
            return EmptyDataObject
        }
    }
}

private object EmptyDataObject : DataObject {

    override val size: Int = 0
    override val keys: Set<String> = emptySet()
    override val values: Collection<Any?> = emptyList()

    override fun isEmpty(): Boolean = true
    override fun containsKey(key: String): Boolean = false
    override fun containsValue(value: Any?): Boolean = false
    override fun get(key: String): Any = throw NoSuchElementException()
    override fun opt(key: String): Any? = null
    override fun iterator(): Iterator<Pair<String, Any?>> = emptyList<Pair<String, Any?>>().iterator()
}
