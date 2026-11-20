// Generated code. Do not modify.

package com.yandex.div.reference

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.type.Color
import com.yandex.div.core.type.Url
import com.yandex.div.model.entity.*
import com.yandex.div.model.expression.*
import com.yandex.div.model.entity.EntityTemplate as Template

public class EntityWithStringEnumPropertyWithDefaultValue @InternalApi constructor (
    @JvmField public val value: Expression<Value> = VALUE_DEFAULT_VALUE, // default value: second
) {

    @InternalApi
    public fun copy(
        value: Expression<Value> = this.value,
    ): EntityWithStringEnumPropertyWithDefaultValue = EntityWithStringEnumPropertyWithDefaultValue(
        value = value,
    )

    public companion object {
        public const val TYPE: String = "entity_with_string_enum_property_with_default_value"

        private val VALUE_DEFAULT_VALUE: Expression<Value> = Expression.constant(Value.SECOND)
    }

    public enum class Value(private val value: String) {
        FIRST("first"),
        SECOND("second"),
        THIRD("third");

        @InternalApi
        public companion object Converter {

            public fun toString(obj: Value): String {
                return obj.value
            }

            public fun fromString(value: String): Value? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    THIRD.value -> THIRD
                    else -> null
                }
            }

            @JvmField
            public val TO_STRING: (Value) -> String = { value: Value -> toString(value) }

            @JvmField
            public val FROM_STRING: (String) -> Value? = { value: String -> fromString(value) }
        }
    }
}
