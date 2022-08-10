package com.yandex.div.json

import android.annotation.SuppressLint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal object JsonTopologicalSorting {

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
    fun sort(json: JSONObject, logger: ParsingErrorLogger, env: ParsingEnvironment): Map<String, Set<String>> {
        val types = parseTypeDependencies(json, logger, env)
        val visited = LinkedHashSet<String>()
        val processed = LinkedHashSet<String>()
        val sorted = LinkedHashMap<String, Set<String>>()

        for (type in types.keys) {
            processType(type, types, visited, processed, sorted)
        }
        return sorted
    }

    private fun parseTypeDependencies(json: JSONObject, logger: ParsingErrorLogger, env: ParsingEnvironment): MutableMap<String, List<String>> {
        val result = LinkedHashMap<String, List<String>>(json.length())
        json.forEach<JSONObject> { key, entry ->
            val dependencies = mutableListOf<String>()
            readObjectDependencies(
                json = entry,
                requireParent = true,
                dependencies = dependencies,
                logger = TemplateParsingErrorLogger(logger = logger, templateId = key),
                env = env,
            )
            result[key] = dependencies
        }
        return result
    }

    private fun readObjectDependencies(
        json: JSONObject,
        requireParent: Boolean,
        dependencies: MutableList<String>,
        logger: ParsingErrorLogger,
        env: ParsingEnvironment,
    ) {
        val parent = if (requireParent) readParent(json, logger, env) else readOptionalParent(json, logger, env)
        parent?.let { dependencies.add(it) }

        json.forEach<JSONObject> { _, jsonObject ->
            readObjectDependencies(
                json = jsonObject,
                requireParent = false,
                dependencies = dependencies,
                logger = logger,
                env = env,
            )
        }
        json.forEach<JSONArray> { _, jsonArray ->
            jsonArray.forEach<JSONObject> { _, item ->
                readObjectDependencies(
                    json = item,
                    requireParent = false,
                    dependencies = dependencies,
                    logger = logger,
                    env = env,
                )
            }
        }
    }

    private fun readParent(json: JSONObject, logger: ParsingErrorLogger, env: ParsingEnvironment): String? {
        return json.read(key = "type", validator = { it.isNotEmpty() }, logger = logger, env = env)
    }

    private fun readOptionalParent(json: JSONObject, logger: ParsingErrorLogger, env: ParsingEnvironment): String? {
        return json.readOptional(key = "type", validator = { it.isNotEmpty() }, logger = logger, env = env)
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
