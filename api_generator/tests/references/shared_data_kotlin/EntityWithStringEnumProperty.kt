// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithStringEnumProperty(
    @JvmField final val property: Expression<Property>,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            property.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithStringEnumProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.evaluate(resolver) == other.property.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<Property> = this.property,
    ) = EntityWithStringEnumProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_string_enum_property"
    }


    enum class Property(private val value: String) {
        FIRST("first"),
        SECOND("second");

        companion object Converter {
            fun toString(obj: Property): String {
                return obj.value
            }

            fun fromString(string: String): Property? {
                return when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            val FROM_STRING = { string: String ->
                when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }
        }
    }
}
