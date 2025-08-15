// Generated code. Do not modify.

package com.yandex.div.reference

import org.json.JSONArray
import org.json.JSONObject

class EntityWithOptionalComplexProperty(
    @JvmField val property: Property? = null,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            (property?.hash() ?: 0)
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithOptionalComplexProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return (property?.equals(other.property, resolver, otherResolver) ?: (other.property == null))
    }

    fun copy(
        property: Property? = this.property,
    ) = EntityWithOptionalComplexProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_optional_complex_property"
    }

    class Property(
        @JvmField val value: Expression<Uri>,
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

        fun equals(other: Property?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
            other ?: return false
            return value.evaluate(resolver) == other.value.evaluate(otherResolver)
        }

        fun copy(
            value: Expression<Uri> = this.value,
        ) = Property(
            value = value,
        )
    }
}
