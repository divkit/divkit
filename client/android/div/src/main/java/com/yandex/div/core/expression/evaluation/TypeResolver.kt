package com.yandex.div.core.expression.evaluation

import com.yandex.div2.DivEvaluableType

/**
 * Helps to answer question: what type of value should be in field 'A'
 * after evaluation of expression?
 */
interface TypeResolver {
    /**
     * @param valueProvider allows to read values of container with given key. Could be helpful
     * if type of property cannot be resolved just by looking at key name.
     */
    fun resolveExpressionType(key: String, valueProvider: (key: String) -> Any?): DivEvaluableType

    companion object {
        val ALWAYS_STRING = object : TypeResolver {
            override fun resolveExpressionType(
                key: String,
                valueProvider: (key: String) -> Any?,
            ) = DivEvaluableType.STRING
        }
    }
}
