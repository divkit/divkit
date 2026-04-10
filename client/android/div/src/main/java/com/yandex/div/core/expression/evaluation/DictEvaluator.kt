package com.yandex.div.core.expression.evaluation

/**
 * Evaluates expressions used inside dictionary structure.
 */
interface DictEvaluator {
    fun get(typeResolver: TypeResolver = TypeResolver.ALWAYS_STRING): Result<Map<String, Any?>>
}

