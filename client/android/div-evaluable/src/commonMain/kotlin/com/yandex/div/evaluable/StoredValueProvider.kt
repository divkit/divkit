package com.yandex.div.evaluable

/**
 * Interface for providing stored values.
 */
@Deprecated("Use ScopedStoredValueProvider")
public fun interface StoredValueProvider {
    /**
     * @return stored value or null if it is missing.
     */
    public fun get(name: String): Any?
}
