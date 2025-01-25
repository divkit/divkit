package com.yandex.div.internal.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Helper class that prints JSON to string.
 *
 * @param indentSpaces the number of spaces to indent for each level of nesting.
 * @param nestingLimit the limit of nesting for [JSONObject]s and [JSONArray]s.
 *     These elements will be replaced with "..." string if the nesting level exceeds a limit.
 */
internal class JsonPrinter(
    private val indentSpaces: Int,
    private val nestingLimit: Int
) {

    /**
     * Presents given [JSONObject] as a string.
     */
    @Throws(JSONException::class)
    fun print(json: JSONObject): String {
        val copy = json.deepCopy(nestingLimit)
        return if (indentSpaces == 0) copy.toString() else copy.toString(indentSpaces)
    }

    /**
     * Presents given [JSONArray] as a string.
     */
    @Throws(JSONException::class)
    fun print(json: JSONArray): String {
        val copy = json.deepCopy(nestingLimit)
        return if (indentSpaces == 0) copy.toString() else copy.toString(indentSpaces)
    }

    private fun JSONObject.deepCopy(nestingLevel: Int): JSONObject {
        val result = JSONObject()
        forEachNullable { key: String, item: Any? ->
            val element = when (item) {
                is JSONObject -> if (nestingLevel == 0) ELLIPSIS else item.deepCopy(nestingLevel - 1)
                is JSONArray -> if (nestingLevel == 0) ELLIPSIS else item.deepCopy(nestingLevel - 1)
                else -> item
            }
            result.put(key, element)
        }
        return result
    }

    private fun JSONArray.deepCopy(nestingLevel: Int): JSONArray {
        val result = JSONArray()
        forEachNullable { _, item: Any? ->
            val element = when (item) {
                is JSONObject -> if (nestingLevel == 0) ELLIPSIS else item.deepCopy(nestingLevel - 1)
                is JSONArray -> if (nestingLevel == 0) ELLIPSIS else item.deepCopy(nestingLevel - 1)
                else -> item
            }
            result.put(element)
        }
        return result
    }

    private companion object {

        private const val ELLIPSIS = "..."
    }
}
