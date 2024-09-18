// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithStringEnumPropertyWithDefaultValue(
    @JvmField final val value: Expression<Value> = VALUE_DEFAULT_VALUE, // default value: second
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            value.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithStringEnumPropertyWithDefaultValue?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return value.evaluate(resolver) == other.value.evaluate(otherResolver)
    }

    fun copy(
        value: Expression<Value> = this.value,
    ) = EntityWithStringEnumPropertyWithDefaultValue(
        value = value,
    )

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

            fun fromString(value: String): Value? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }

            val TO_STRING = { value: Value -> toString(value) }
            val FROM_STRING = { value: String -> fromString(value) }
        }
    }
}
