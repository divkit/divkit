// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayOfExpressions(
    @JvmField final val items: ExpressionList<String>, // at least 1 elements
) : Hashable {

    private var _propertiesHash: Int? = null 
    private var _hash: Int? = null 

    override fun propertiesHash(): Int {
        _propertiesHash?.let {
            return it
        }
        val propertiesHash = javaClass.hashCode()
        _propertiesHash = propertiesHash
        return propertiesHash
    }

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            propertiesHash() +
            items.hashCode()
        _hash = hash
        return hash
    }

    fun copy(
        items: ExpressionList<String> = this.items,
    ) = EntityWithArrayOfExpressions(
        items = items,
    )

    companion object {
        const val TYPE = "entity_with_array_of_expressions"

        private val ITEMS_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
    }

}
