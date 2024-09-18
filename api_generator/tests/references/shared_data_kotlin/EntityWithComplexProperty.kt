// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithComplexProperty(
    @JvmField final val property: Property,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            property.hash()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithComplexProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return property.equals(other.property, resolver, otherResolver)
    }

    fun copy(
        property: Property = this.property,
    ) = EntityWithComplexProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_complex_property"
    }

    class Property(
        @JvmField final val value: Expression<Uri>,
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
