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

public class EntityWithStringEnumProperty @InternalApi constructor (
    @JvmField public val property: Expression<Property>,
) {

    @InternalApi
    public fun copy(
        property: Expression<Property> = this.property,
    ): EntityWithStringEnumProperty = EntityWithStringEnumProperty(
        property = property,
    )

    public companion object {
        public const val TYPE: String = "entity_with_string_enum_property"
    }

    public enum class Property(private val value: String) {
        FIRST("first"),
        SECOND("second");

        @InternalApi
        public companion object Converter {

            public fun toString(obj: Property): String {
                return obj.value
            }

            public fun fromString(value: String): Property? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            @JvmField
            public val TO_STRING: (Property) -> String = { value: Property -> toString(value) }

            @JvmField
            public val FROM_STRING: (String) -> Property? = { value: String -> fromString(value) }
        }
    }
}
