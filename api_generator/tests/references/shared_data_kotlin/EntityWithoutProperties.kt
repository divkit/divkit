// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithoutProperties() : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = this::class.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithoutProperties?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        return other != null
    }

    fun copy() = EntityWithoutProperties()

    companion object {
        const val TYPE = "entity_without_properties"
    }

}
