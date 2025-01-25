package com.yandex.div.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject

sealed class StoredValue {
    abstract val name: String

    enum class Type(private val value: String) {
        STRING("string"),
        INTEGER("integer"),
        BOOLEAN("boolean"),
        NUMBER("number"),
        COLOR("color"),
        URL("url"),
        ARRAY("array"),
        DICT("dict");

        companion object Converter {
            fun toString(obj: Type): String {
                return obj.value
            }

            fun fromString(string: String): Type? {
                return when (string) {
                    STRING.value -> STRING
                    INTEGER.value -> INTEGER
                    BOOLEAN.value -> BOOLEAN
                    NUMBER.value -> NUMBER
                    COLOR.value -> COLOR
                    URL.value -> URL
                    ARRAY.value -> ARRAY
                    DICT.value -> DICT
                    else -> null
                }
            }
        }
    }

    data class StringStoredValue(
        override val name: String,
        val value: String,
    ) : StoredValue()

    data class IntegerStoredValue(
        override val name: String,
        val value: Long,
    ) : StoredValue()

    data class BooleanStoredValue(
        override val name: String,
        val value: Boolean,
    ) : StoredValue()

    data class DoubleStoredValue(
        override val name: String,
        val value: Double,
    ) : StoredValue()

    data class ColorStoredValue(
        override val name: String,
        val value: Color,
    ) : StoredValue()

    data class UrlStoredValue(
        override val name: String,
        val value: Url,
    ) : StoredValue()

    data class ArrayStoredValue(
        override val name: String,
        val value: JSONArray
    ) : StoredValue()

    data class DictStoredValue(
        override val name: String,
        val value: JSONObject
    ) : StoredValue()

    fun getValue(): Any = when (this) {
        is StringStoredValue -> value
        is IntegerStoredValue -> value
        is BooleanStoredValue -> value
        is DoubleStoredValue -> value
        is ColorStoredValue -> value
        is UrlStoredValue -> value
        is ArrayStoredValue -> value
        is DictStoredValue -> value
    }

    fun getType(): Type = when (this) {
        is StringStoredValue -> Type.STRING
        is IntegerStoredValue -> Type.INTEGER
        is BooleanStoredValue -> Type.BOOLEAN
        is DoubleStoredValue -> Type.NUMBER
        is ColorStoredValue -> Type.COLOR
        is UrlStoredValue -> Type.URL
        is ArrayStoredValue -> Type.ARRAY
        is DictStoredValue -> Type.DICT
    }

}
