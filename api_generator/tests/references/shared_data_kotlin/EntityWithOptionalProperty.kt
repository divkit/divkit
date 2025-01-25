// Generated code. Do not modify.

package com.yandex.div.reference

import org.json.JSONArray
import org.json.JSONObject

class EntityWithOptionalProperty(
    @JvmField val property: Expression<String>? = null,
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

    fun equals(other: EntityWithOptionalProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property?.evaluate(resolver) == other.property?.evaluate(otherResolver)
    }

    fun copy(
        property: Expression<String>? = this.property,
    ) = EntityWithOptionalProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_optional_property"
    }
}
