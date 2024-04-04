// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithJsonProperty(
    @JvmField final val jsonProperty: JSONObject = JSON_PROPERTY_DEFAULT_VALUE, // default value: { "key": "value", "items": [ "value" ] }
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            jsonProperty.hashCode()
        _hash = hash
        return hash
    }

    companion object {
        const val TYPE = "entity_with_json_property"

        private val JSON_PROPERTY_DEFAULT_VALUE = JSONObject("""
        {
            "key": "value",
            "items": [
                "value"
            ]
        }
        """)
    }

}
