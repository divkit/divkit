// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithStringArrayProperty(
    @JvmField final val array: ExpressionList<String>, // at least 1 elements
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            array.hashCode()
        _hash = hash
        return hash
    }

    companion object {
        const val TYPE = "entity_with_string_array_property"

        private val ARRAY_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
    }

}
