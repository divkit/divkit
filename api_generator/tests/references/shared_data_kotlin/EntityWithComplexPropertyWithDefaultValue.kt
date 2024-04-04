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
            property.hash()
        _hash = hash
        return hash
    }

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
                value.hashCode()
            _hash = hash
            return hash
        }
    }
}
