// Generated code. Do not modify.

package com.yandex.div.reference

import org.json.JSONArray
import org.json.JSONObject

class EntityWithJsonProperty(
    @JvmField val jsonProperty: Expression<JSONObject> = JSON_PROPERTY_DEFAULT_VALUE, // default value: { "key": "value", "items": [ "value" ] }
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            this::class.hashCode() +
            jsonProperty.hashCode()
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithJsonProperty?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return jsonProperty.evaluate(resolver) == other.jsonProperty.evaluate(otherResolver)
    }

    fun copy(
        jsonProperty: Expression<JSONObject> = this.jsonProperty,
    ) = EntityWithJsonProperty(
        jsonProperty = jsonProperty,
    )

    companion object {
        const val TYPE = "entity_with_json_property"

        private val JSON_PROPERTY_DEFAULT_VALUE = Expression.constant(JSONObject("""
        {
            "key": "value",
            "items": [
                "value"
            ]
        }
        """))
    }
}
