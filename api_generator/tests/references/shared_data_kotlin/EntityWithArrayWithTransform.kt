// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayWithTransform(
    @JvmField final val array: ExpressionList<Int>, // at least 1 elements
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
        const val TYPE = "entity_with_array_with_transform"

        private val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }
    }

}
