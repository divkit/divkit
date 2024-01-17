// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithOptionalProperty(
    @JvmField final val property: Expression<String>? = null,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            (property?.hashCode() ?: 0)
        _hash = hash
        return hash
    }

    companion object {
        const val TYPE = "entity_with_optional_property"
    }

}
