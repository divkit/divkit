// Generated code. Do not modify.

package com.yandex.div.reference

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.data.*
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import org.json.JSONArray
import org.json.JSONObject

class EntityWithJsonProperty(
    @JvmField val jsonProperty: Expression<JSONObject> = JSON_PROPERTY_DEFAULT_VALUE, // default value: { "key": "value", "items": [ "value" ] }
) : JSONSerializable, Hashable {

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

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeExpression(key = "json_property", value = jsonProperty)
        json.write(key = "type", value = TYPE)
        return json
    }

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

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithJsonProperty {
            val logger = env.logger
            return EntityWithJsonProperty(
                jsonProperty = JsonParser.readOptionalExpression(json, "json_property", logger, env, JSON_PROPERTY_DEFAULT_VALUE, TYPE_HELPER_DICT) ?: JSON_PROPERTY_DEFAULT_VALUE
            )
        }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithJsonProperty(env, json = it) }
    }
}
