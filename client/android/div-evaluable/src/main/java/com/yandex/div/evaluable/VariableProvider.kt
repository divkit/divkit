package com.yandex.div.evaluable

/**
 * Interface for providing variable values.
 */
fun interface VariableProvider {
    /**
     * @return variable value or null if it is missing.
     */
    fun get(name: String): Any?
}