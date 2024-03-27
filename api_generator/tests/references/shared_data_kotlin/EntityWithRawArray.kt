// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithRawArray(
    @JvmField final val array: Expression<JSONArray>,
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

    fun copy(
        array: Expression<JSONArray> = this.array,
    ) = EntityWithRawArray(
        array = array,
    )

    companion object {
        const val TYPE = "entity_with_raw_array"
    }

}
