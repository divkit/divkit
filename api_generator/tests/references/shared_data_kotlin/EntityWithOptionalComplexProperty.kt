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
            (property?.hash() ?: 0)
        _hash = hash
        return hash
    }

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
                value.hashCode()
            _hash = hash
            return hash
        }
    }
}
