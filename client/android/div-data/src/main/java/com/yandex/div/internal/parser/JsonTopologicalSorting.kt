package com.yandex.div.internal.parser

import android.annotation.SuppressLint
import com.yandex.div.internal.util.forEach
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.serialization.ParsingContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal object JsonTopologicalSorting {

    private val TYPE_VALIDATOR = ValueValidator<String> { it.isNotEmpty() }

    /**
     * Performs sorting of json objects that describes any of following relationship:
     *
     * + inheritance
     * ```
     * "parent": {
     *   "type": "base"
     * },
     * "child": {
     *   "type": "parent"
     * }
     * ```
     *
     * + composition
     * ```
     * "collection": {
     *   "type": "base"
     *   items: [
     *     {
     *       "type": "item"
     *     },
     *     ...
     *   ]
     * },
     * "item": {
     *   "type": "base"
     * }
     * ```
     *
     * @return linked map of names of a given json sorted in topological order along with their dependencies.
     * @throws CyclicDependencyException if object dependencies forms cycle.
     * @throws ParsingException if top level object has no parent reference or it is empty.
     * @throws JSONException if json is malformed.
     */
    @SuppressLint("NewApi")
    @Throws(JSONException::class, ParsingException::class, CyclicDependencyException::class)
    fun sort(context: ParsingContext, json: JSONObject): Map<String, Set<String>> {
        val types = parseTypeDependencies(context, json)
        val visited = LinkedHashSet<String>()
        val processed = LinkedHashSet<String>()
        val sorted = LinkedHashMap<String, Set<String>>()

        for (type in types.keys) {
            processType(type, types, visited, processed, sorted)
        }
        return sorted
    }

    private fun parseTypeDependencies(context: ParsingContext, json: JSONObject, ): MutableMap<String, List<String>> {
        val result = LinkedHashMap<String, List<String>>(json.length())
        json.forEach<JSONObject> { key, entry ->
            val dependencies = mutableListOf<String>()
            readObjectDependencies(
                context = context,
                logger = TemplateParsingErrorLogger(logger = context.logger, templateId = key),
                json = entry,
                requireParent = true,
                dependencies = dependencies
            )
            result[key] = dependencies
        }
        return result
    }

    private fun readObjectDependencies(
        context: ParsingContext,
        logger: ParsingErrorLogger,
        json: JSONObject,
        requireParent: Boolean,
        dependencies: MutableList<String>
    ) {
        val parent = if (requireParent) {
            readParent(context, json)
        } else {
            readOptionalParent(context, json)
        }
        parent?.let { dependencies.add(it) }

        json.forEach<JSONObject> { _, jsonObject ->
            readObjectDependencies(
                context = context,
                logger = logger,
                json = jsonObject,
                requireParent = false,
                dependencies = dependencies
            )
        }
        json.forEach<JSONArray> { _, jsonArray ->
            jsonArray.forEach<JSONObject> { _, item ->
                readObjectDependencies(
                    context = context,
                    logger = logger,
                    json = item,
                    requireParent = false,
                    dependencies = dependencies
                )
            }
        }
    }

    private fun readParent(context: ParsingContext, json: JSONObject): String {
        return JsonPropertyParser.read(context, json, "type", TYPE_VALIDATOR)
    }

    private fun readOptionalParent(context: ParsingContext, json: JSONObject): String? {
        return JsonPropertyParser.readOptional(context, json, "type", TYPE_VALIDATOR)
    }

    private fun processType(
        type: String,
        types: MutableMap<String, List<String>>,
        visited: MutableSet<String>,
        processed: MutableSet<String>,
        sorted: LinkedHashMap<String, Set<String>>
    ) {
        if (type in visited) throwCyclicDependency(visited.toList(), type)
        if (type in processed) return

        val dependencies = types[type]?.filter { types.contains(it) }
        if (!dependencies.isNullOrEmpty()) {
            visited.add(type)
            for (dependency in dependencies) {
                processType(dependency, types, visited, processed, sorted)
            }
            visited.remove(type)
        }
        processed.add(type)
        sorted[type] = dependencies.orEmpty().toSet()
    }

    private fun throwCyclicDependency(visited: List<String>, type: String): Nothing {
        val cycleStart = visited.indexOf(type)
        val output = StringBuilder()
        for (i in cycleStart until visited.size) {
            output.append(visited[i]).append(" -> ")
        }
        output.append(type)
        throw CyclicDependencyException(output.toString())
    }
}

internal class CyclicDependencyException(message: String) : Exception(message)
