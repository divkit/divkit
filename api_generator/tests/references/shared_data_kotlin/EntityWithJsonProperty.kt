// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithJsonProperty(
    @JvmField final val jsonProperty: JSONObject = JSON_PROPERTY_DEFAULT_VALUE, // default value: { "key": "value", "items": [ "value" ] }
) {

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
