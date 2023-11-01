package com.yandex.div.evaluable

import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.DateTime
import com.yandex.div.evaluable.types.Url
import org.json.JSONArray
import org.json.JSONObject

enum class EvaluableType(internal val typeName: String) {
    INTEGER("Integer"),
    NUMBER("Number"),
    BOOLEAN("Boolean"),
    STRING("String"),
    DATETIME("DateTime"),
    COLOR("Color"),
    URL("Url"),
    DICT("Dict"),
    ARRAY("Array")
    ;

    override fun toString(): String {
        return typeName
    }

    companion object {

        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        @JvmStatic
        inline fun <reified T> of(value: T): EvaluableType {
            return when (value) {
                is Long -> INTEGER
                is Double -> NUMBER
                is Boolean -> BOOLEAN
                is String -> STRING
                is DateTime -> DATETIME
                is Color -> COLOR
                is Url -> URL
                is JSONObject -> DICT
                is JSONArray -> ARRAY
                null -> throw EvaluableException("Unable to find type for null")
                else -> throw EvaluableException("Unable to find type for ${value!!::class.java.name}")
            }
        }
    }
}
