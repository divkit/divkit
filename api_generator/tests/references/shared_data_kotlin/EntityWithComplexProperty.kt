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
            property.hash()
        _hash = hash
        return hash
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
                value.hashCode()
            _hash = hash
            return hash
        }

        fun copy(
            value: Expression<Uri> = this.value,
        ) = Property(
            value = value,
        )
    }
}
