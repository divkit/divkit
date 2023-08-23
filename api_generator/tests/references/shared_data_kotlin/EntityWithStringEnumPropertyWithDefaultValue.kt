// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithStringEnumPropertyWithDefaultValue(
    @JvmField final val value: Expression<Value> = VALUE_DEFAULT_VALUE, // default value: second
) {

    companion object {
        const val TYPE = "entity_with_string_enum_property_with_default_value"

        private val VALUE_DEFAULT_VALUE = Expression.constant(Value.SECOND)
    }


    enum class Value(private val value: String) {
        FIRST("first"),
        SECOND("second"),
        THIRD("third");

        companion object Converter {
            fun toString(obj: Value): String {
                return obj.value
            }

            fun fromString(string: String): Value? {
                return when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }

            val FROM_STRING = { string: String ->
                when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }
        }
    }
}
