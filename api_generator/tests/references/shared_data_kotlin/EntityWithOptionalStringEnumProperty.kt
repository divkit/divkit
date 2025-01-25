// Generated code. Do not modify.

package com.yandex.div.reference

import org.json.JSONArray
import org.json.JSONObject

class EntityWithOptionalStringEnumProperty(
    @JvmField val property: Expression<Property>? = null,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            (property?.hashCode() ?: 0)
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithOptionalStringEnumProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property?.evaluate(resolver) == other.property?.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<Property>? = this.property,
    ) = EntityWithOptionalStringEnumProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"
    }

    enum class Property(private val value: String) {
        FIRST("first"),
        SECOND("second");

        companion object Converter {

            fun toString(obj: Property): String {
                return obj.value
            }

            fun fromString(value: String): Property? {
                return when (value) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            @JvmField
            val TO_STRING = { value: Property -> toString(value) }

            @JvmField
            val FROM_STRING = { value: String -> fromString(value) }
        }
    }
}
