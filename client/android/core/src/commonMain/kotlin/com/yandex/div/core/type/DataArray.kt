package com.yandex.div.core.type

import com.yandex.div.core.annotations.ExperimentalApi
import kotlin.jvm.JvmStatic

@ExperimentalApi
public interface DataArray : Collection<Any?> {

    public fun get(index: Int): Any
    public fun opt(index: Int): Any?
    public fun indexOf(element: Any?): Int

    public companion object {

        @JvmStatic
        public fun empty(): DataArray {
            return DataArrayImpl(emptyList())
        }

        /**
         * Creates a [DataArray] with the given source. Nested dicts and arrays are kept as is, without copying.
         *
         * Values may be strings, booleans, finite numbers, [DataObject]s, [DataArray]s or `null`.
         * Numbers are read back with the same types as from JSON, e.g. a [Long] that fits [Int] becomes [Int].
         * The created array is equal only to itself.
         *
         * @throws IllegalArgumentException if a value has an unsupported type or is a non-finite number.
         */
        @JvmStatic
        public fun of(source: List<Any?>): DataArray {
            return DataArrayImpl(source.map { it.toDataValue() })
        }
    }
}
