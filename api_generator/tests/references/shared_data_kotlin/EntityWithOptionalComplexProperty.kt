// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithOptionalComplexProperty(
    @JvmField final val property: Property? = null,
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

    fun copy(
        property: Property? = this.property,
    ) = EntityWithOptionalComplexProperty(
        property = property,
    )

    companion object {
        const val TYPE = "entity_with_optional_complex_property"
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

        fun copy(
            value: Expression<Uri> = this.value,
        ) = Property(
            value = value,
        )
    }
}
