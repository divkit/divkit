package com.yandex.div.evaluable

/**
 * Interface for providing stored values.
 */
@Deprecated("Use ScopedStoredValueProvider")
fun interface StoredValueProvider {
    /**
     * @return stored value or null if it is missing.
     */
    fun get(name: String): Any?
}
