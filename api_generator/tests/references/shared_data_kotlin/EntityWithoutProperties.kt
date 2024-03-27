// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithoutProperties() : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = javaClass.hashCode()
        _hash = hash
        return hash
    }

    fun copy() = EntityWithoutProperties()

    companion object {
        const val TYPE = "entity_without_properties"
    }

}
