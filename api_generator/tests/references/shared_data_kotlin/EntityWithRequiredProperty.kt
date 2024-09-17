// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithRequiredProperty(
    @JvmField final val property: Expression<String>, // at least 1 char
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

    fun equals(other: EntityWithRequiredProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.evaluate(resolver) == other.property.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<String> = this.property,
    ) = EntityWithRequiredProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_required_property"

        private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
    }
}
