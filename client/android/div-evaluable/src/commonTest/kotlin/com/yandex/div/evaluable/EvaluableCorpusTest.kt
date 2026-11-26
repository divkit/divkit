package com.yandex.div.evaluable

import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.type.DataArray
import com.yandex.div.core.type.DataObject
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.evaluable.internal.LiteralsEscaper
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal expect fun loadExpressionTestData(): Map<String, String>

class EvaluableCorpusTest {

    @Test
    fun evaluatesSharedExpressionCases() {
        val files = loadExpressionTestData()
        val failures = mutableListOf<String>()
        var executed = 0
        var platformSpecific = 0
        var needsRuntime = 0

        for (path in files.keys.sorted()) {
            val cases = JsonObject(files.getValue(path)).optArray("cases") ?: continue
            for (index in 0 until cases.length()) {
                val case = cases.getObject(index)
                val platforms = case.getArray("platforms")
                if ("android" !in platforms || "ios" !in platforms) {
                    platformSpecific++
                    continue
                }
                val variables = case.optArray("variables") ?: JsonArray()
                if (case.requiresRuntime(variables)) {
                    needsRuntime++
                    continue
                }

                executed++
                runCatching { checkCase(case, variables) }.onFailure { failure ->
                    val name = case.optString("name") ?: case.getString("expression")
                    failures += "$path:${index + 1} $name\n${failure.message}"
                }
            }
        }

        assertTrue(executed > 0, "No shared expression cases in ${files.size} files")
        println(
            "Expression corpus: $executed executed, $platformSpecific platform-specific, " +
                "$needsRuntime require DivKit runtime",
        )
        assertTrue(failures.isEmpty(), failures.take(50).joinToString("\n\n", "${failures.size} failures:\n"))
    }

    private fun JsonObject.requiresRuntime(variables: JsonArray): Boolean {
        // The Android integration runner still covers custom functions and expression-backed variables.
        return (optArray("functions")?.length() ?: 0) > 0 || (0 until variables.length()).any { index ->
            val variable = variables.getObject(index)
            variable.getString("type") == "property" ||
                (variable.opt("value") as? String)?.contains("@{") == true
        }
    }

    private fun checkCase(case: JsonObject, variables: JsonArray) {
        val warnings = mutableListOf<String>()
        val values = (0 until variables.length()).associate { index ->
            val variable = variables.getObject(index)
            variable.getString("name") to variable.variableValue()
        }
        val evaluator = Evaluator(
            EvaluationContext(
                variableProvider = { name -> values[name] },
                storedValueProvider = EmptyStoredValues,
                functionProvider = GeneratedBuiltinFunctionProvider,
                warningSender = { _, message -> warnings += message },
            )
        )
        val expression = case.getString("expression")
        val expected = case.getObject("expected")
        val type = expected.getString("type")
        if (type == "error") {
            val failure = assertFailsWith<EvaluableException> {
                evaluator.eval<Any>(Evaluable.prepare(expression))
            }
            val expectedMessage = expected.optString("value")
            if (!expectedMessage.isNullOrEmpty()) {
                val messages = generateSequence<Throwable>(failure) { it.cause }.mapNotNull { it.message }.toList()
                assertTrue(expectedMessage in messages, "Expected <$expectedMessage>, received $messages")
            }
            return
        }

        // An empty value reaches the renderer as a literal; there is no expression to prepare.
        val result = if (expression.isEmpty()) "" else evaluator.eval<Any>(Evaluable.prepare(expression))
        when (type) {
            "number" -> assertEquals(expected.getDouble("value"), assertIs<Number>(result).toDouble(), 1e-10)
            "datetime" -> assertEquals(expected.getString("value"), assertIs<DateTime>(result).toString())
            "array" -> assertEquals(normalize(expected.getArray("value")), normalize(assertIs<DataArray>(result)))
            "dict" -> assertEquals(normalize(expected.getObject("value")), normalize(assertIs<DataObject>(result)))
            "unordered_array" -> {
                val expectedCounts = expected.getArray("value").groupingBy(::normalize).eachCount()
                val actualCounts = assertIs<DataArray>(result).groupingBy(::normalize).eachCount()
                assertEquals(expectedCounts, actualCounts)
            }
            else -> assertEquals(expected.literalValue(), result)
        }
        val expectedWarnings = case.optArray("expected_warnings") ?: JsonArray()
        assertEquals(expectedWarnings.map { it as String }.sorted(), warnings.sorted())
    }

    private fun JsonObject.variableValue(): Any = when (getString("type")) {
        "string" -> LiteralsEscaper.process(getString("value"))
        else -> literalValue()
    }

    private fun JsonObject.literalValue(): Any = when (val type = getString("type")) {
        "string" -> getString("value")
        "integer" -> getLong("value")
        "number" -> getDouble("value")
        "boolean" -> when (val value = get("value")) {
            is Boolean -> value
            is Number -> value.toDouble() != 0.0
            is String -> value.toBooleanStrict()
            else -> error("Unsupported boolean fixture value: $value")
        }
        "color" -> Color.parse(getString("value"))
        "url" -> Url(getString("value"))
        "array" -> getArray("value")
        "dict" -> getObject("value")
        else -> error("Unsupported expression fixture type: $type")
    }

    private fun normalize(value: Any?): Any? = when (value) {
        is DataArray -> value.map(::normalize)
        is DataObject -> value.associate { (key, item) -> key to normalize(item) }
        is Int -> value.toLong()
        else -> value
    }

    private object EmptyStoredValues : ScopedStoredValueProvider {
        override fun get(name: String, scope: String): Any? = null
    }
}
