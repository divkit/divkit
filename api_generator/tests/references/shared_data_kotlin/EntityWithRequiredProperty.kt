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
            property.hashCode()
        _hash = hash
        return hash
    }

    companion object {
        const val TYPE = "entity_with_required_property"

        private val PROPERTY_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
    }

}
