package com.yandex.div.internal.actions

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.Variable
import org.json.JSONArray
import org.json.JSONObject

@InternalApi
class UpdateStructureHelper(
    private val reportError: (message: String) -> Unit
) {

    fun updateArrayStructure(
        variable: Variable.ArrayVariable,
        path: String,
        newValue: Any
    ) {
        val segments = getPathSegments(path) ?: return
        val variableValue = variable.getValue() as JSONArray
        val target = findStructureElement(Structure.Array(variableValue), segments.dropLast(1))
        if (target != null && setValue(target, segments.last(), newValue)) {
            variable.set(variableValue)
        }
    }

    fun updateDictStructure(
        variable: Variable.DictVariable,
        path: String,
        newValue: Any
    ) {
        val segments = getPathSegments(path) ?: return
        val variableValue = variable.getValue() as JSONObject
        val target = findStructureElement(
            Structure.Dictionary(variableValue),
            segments.dropLast(1)
        )
        if (target != null && setValue(target, segments.last(), newValue)) {
            variable.set(variableValue)
        }
    }

    private fun getPathSegments(path: String): List<String>? {
        val segments = path.split("/").filter { it.isNotEmpty() }
        if (path.isNotEmpty() && segments.isEmpty()) {
            reportError("Malformed path '$path': all path segments are empty")
            return null
        }
        return segments
    }

    private fun findStructureElement(root: Structure, pathSegments: List<String>): Structure? {
        var target: Structure = root
        pathSegments.forEachIndexed { index, pathElement ->
            target = try {
                when (val structureElement = target.get(pathElement)) {
                    Structure.NonStructure -> {
                        val errorPath = pathSegments.take(index + 1).joinToString(separator = "/")
                        reportError("Element with path '$errorPath' is not a structure")
                        return null
                    }

                    null -> {
                        val errorPath = pathSegments.take(index + 1).joinToString(separator = "/")
                        reportError("Element with path '$errorPath' is not found")
                        return null
                    }

                    else -> structureElement
                }
            } catch (_: NumberFormatException) {
                reportError("Unable to use '$pathElement' as array index")
                return null
            }
        }
        return target
    }

    private fun setValue(
        target: Structure,
        segment: String,
        newValue: Any
    ): Boolean {
        try {
            target.set(segment, newValue)
            return true
        } catch (_: NumberFormatException) {
            reportError("Unable to use '$segment' as array index")
            return false
        } catch (_: IndexOutOfBoundsException) {
            reportError("Position '$segment' is out of array bounds")
            return false
        }
    }
}

private sealed interface Structure {

    val size: Int

    fun get(key: String): Structure?

    fun set(key: String, value: Any)

    class Array(private val array: JSONArray) : Structure {

        override val size: Int
            get() = array.length()

        override fun get(key: String): Structure? {
            val index = key.toInt()
            return when (val value = array.opt(index)) {
                is JSONArray -> Array(value)
                is JSONObject -> Dictionary(value)
                null, JSONObject.NULL -> null
                else -> NonStructure
            }
        }

        override fun set(key: String, value: Any) {
            val index = key.toInt()
            if (index !in 0..size) {
                throw IndexOutOfBoundsException()
            }
            runCatching {
                array.put(index, value)
            }
        }
    }

    class Dictionary(private val dictionary: JSONObject) : Structure {

        override val size: Int
            get() = dictionary.length()

        override fun get(key: String): Structure? {
            return when (val value = dictionary.opt(key)) {
                is JSONArray -> Array(value)
                is JSONObject -> Dictionary(value)
                null, JSONObject.NULL -> null
                else -> NonStructure
            }
        }

        override fun set(key: String, value: Any) {
            runCatching {
                dictionary.put(key, value)
            }
        }
    }

    object NonStructure : Structure {

        override val size: Int
            get() = throw UnsupportedOperationException()

        override fun get(key: String): Structure? {
            throw UnsupportedOperationException()
        }

        override fun set(key: String, value: Any) {
            throw UnsupportedOperationException()
        }
    }
}
