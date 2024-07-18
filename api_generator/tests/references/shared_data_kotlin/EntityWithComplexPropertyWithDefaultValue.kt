// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithComplexPropertyWithDefaultValue(
    @JvmField final val property: Property = PROPERTY_DEFAULT_VALUE, // default value: EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))
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

    fun copy(
        property: Property = this.property,
    ) = EntityWithComplexPropertyWithDefaultValue(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_complex_property_with_default_value"

        private val PROPERTY_DEFAULT_VALUE = EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))
    }


    class Property(
        @JvmField final val value: Expression<String>,
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

        fun copy(
            value: Expression<String> = this.value,
        ) = Property(
            value = value,
        )
    }
}
