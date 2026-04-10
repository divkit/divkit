package com.yandex.div.core.expression.evaluation

import com.yandex.div.internal.expressions.DivExpressionParser
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivEvaluableType
import org.json.JSONArray
import org.json.JSONObject

class JSONObjectEvaluator(
    private val input: JSONObject,
    private val resolver: ExpressionResolver,
    private val logger: ParsingErrorLogger,
) : DictEvaluator {
    override fun get(typeResolver: TypeResolver): Result<Map<String, Any?>> {
        return transformObject(input, typeResolver).onFailure {
            (it as? Exception)?.let { e -> logger.logError(e) }
        }
    }

    private fun transformObject(source: JSONObject, typeResolver: TypeResolver): Result<Map<String, Any?>> {
        val result = mutableMapOf<String, Any?>()
        val keys = source.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            when (val value = source.opt(key)) {
                null, JSONObject.NULL -> result[key] = null
                is JSONObject -> {
                    result[key] = transformObject(value, typeResolver).onFailure {
                        return Result.failure(RuntimeException(
                            "Nested JSON object evaluation failed (key: '$key', json: '$value')!", it))
                    }.getOrThrow()
                }
                is JSONArray -> result[key] = transformArray(value, typeResolver)
                is String -> {
                    if (Expression.mayBeExpression(value)) {

                        val divType = runCatching {
                            typeResolver.resolveExpressionType(key) { source.opt(it) }
                        }.onFailure {
                            return Result.failure(RuntimeException(
                                "Type resolving failed for key '$key' of object '$source'", it))
                        }.getOrThrow()

                        result[key] = evaluateToJSONObjectSupportedType(value, key, divType)
                    } else {
                        result[key] = value
                    }
                }
                else -> result[key] = value
            }
        }
        return Result.success(result)
    }

    private fun transformArray(source: JSONArray, typeResolver: TypeResolver): List<Any?> {
        val result = ArrayList<Any?>(source.length())
        source.forEach { i, value: Any? ->
            when (value) {
                null, JSONObject.NULL -> result.add(null)
                is JSONObject -> result.add(transformObject(value, typeResolver))
                is JSONArray -> result.add(transformArray(value, typeResolver))
                is String -> {
                    if (Expression.mayBeExpression(value)) {
                        result.add(
                            evaluateToJSONObjectSupportedType(value, "[$i]", DivEvaluableType.STRING)
                        )
                    } else {
                        result.add(value)
                    }
                }
                else -> result.add(value)
            }
        }
        return result
    }

    private fun evaluateToJSONObjectSupportedType(
        raw: String,
        expressionKey: String,
        divType: DivEvaluableType,
    ): Any {
        val expr: Expression<*> = DivExpressionParser.readTypedExpression(
            raw = raw,
            key = expressionKey,
            expectedType = divType,
            logger = logger,
        )

        return expr.evaluate(resolver)
    }
}
