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
            return DataObjectImpl(emptyMap())
        }

        /**
         * Creates a [DataObject] with the given source. Nested dicts and arrays are kept as is, without copying.
         *
         * Values may be strings, booleans, finite numbers, [DataObject]s, [DataArray]s or `null`.
         * Numbers are read back with the same types as from JSON, e.g. a [Long] that fits [Int] becomes [Int].
         * The created object is equal only to itself.
         *
         * @throws IllegalArgumentException if a value has an unsupported type or is a non-finite number.
         */
        @JvmStatic
        public fun of(source: Map<String, Any?>): DataObject {
            return DataObjectImpl(source.mapValues { (_, value) -> value.toDataValue() })
        }
    }
}
