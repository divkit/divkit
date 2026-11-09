package com.yandex.div.evaluable

/**
 * Interface for providing variable values.
 */
public fun interface VariableProvider {
    /**
     * @return variable value or null if it is missing.
     */
    public fun get(name: String): Any?
}
