package tech.divkit.flutter.div_expressions_resolver

import IntColor
import StringUrl
import com.yandex.div.evaluable.Evaluable
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject

class ExpressionResolver {
    private val variables = Variables();

    private val evaluator =
        Evaluator(
            EvaluationContext(
                variableProvider = { toNative(variables.getValue(it)) },
                storedValueProvider = {},
                functionProvider = GeneratedBuiltinFunctionProvider,
                warningSender = { _, _ -> },
            ),
        )

    fun resolve(
        expression: String,
        context: Map<String, Any>,
    ): Any? {
        variables.update(context)
        return fromNative(evaluator.eval(Evaluable.lazy(expression)))
    }

    fun clear() = variables.clear()

    private class Variables {
        private var context = mutableMapOf<String, Any>()

        fun getValue(name: String) = context[name]

        fun update(context: Map<String, Any>) {
            this.context = context.toMutableMap();
        }

        fun clear() {
            context = mutableMapOf()
        }
    }
}

private fun toNative(value: Any?): Any? {
    return when (value) {
        is Long -> value
        is Int -> value.toLong()
        is IntColor -> Color(value.value)
        is Boolean -> value
        is Double -> value
        is String -> value
        is StringUrl -> Url.from(value.value)
        is List<*> -> JSONArray(value)
        is Map<*, *> -> JSONObject(value)
        null -> null
        else -> throw Exception("Unsupported type: ${value::class.java.name}")
    }
}

private fun fromNative(value: Any?): Any? {
    return when (value) {
        is Long -> value
        is Int -> value.toLong()
        is Color -> IntColor(value.value)
        is Boolean -> value
        is Double -> value
        is String -> value
        is Url -> StringUrl(value.value)
        is DateTime -> value.toString()
        is JSONArray -> value.toList()
        is JSONObject -> value.toMap()
        null -> null
        else -> throw Exception("Unsupported type: ${value::class.java.name}")
    }
}

private fun JSONArray.toList(): List<Any?> {
    val list = mutableListOf<Any?>()
    for (i in 0 until this.length()) {
        when (val item = this.get(i)) {
            is JSONObject -> list.add(item.toMap())
            is JSONArray -> list.add(item.toList())
            else -> list.add(item)
        }
    }
    return list
}

private fun JSONObject.toMap(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    val keys = this.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        when (val value = this.get(key)) {
            is JSONObject -> map[key] = value.toMap()
            is JSONArray -> map[key] = value.toList()
            else -> map[key] = value
        }
    }
    return map
}
