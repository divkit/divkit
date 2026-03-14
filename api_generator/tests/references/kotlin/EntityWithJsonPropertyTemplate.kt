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

class EntityWithJsonPropertyTemplate(
    @JvmField val jsonProperty: Field<Expression<JSONObject>>,
) : JSONSerializable, JsonTemplate<EntityWithJsonProperty> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithJsonPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        jsonProperty = JsonTemplateParser.readOptionalFieldWithExpression(json, "json_property", topLevel, parent?.jsonProperty, env.logger, env, TYPE_HELPER_DICT)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithJsonProperty {
        return EntityWithJsonProperty(
            jsonProperty = this.jsonProperty.resolveOptional(env = env, key = "json_property", data = data, reader = JSON_PROPERTY_READER) ?: JSON_PROPERTY_DEFAULT_VALUE
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "json_property", field = jsonProperty)
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

        val JSON_PROPERTY_READER: Reader<Expression<JSONObject>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, env.logger, env, JSON_PROPERTY_DEFAULT_VALUE, TYPE_HELPER_DICT) ?: JSON_PROPERTY_DEFAULT_VALUE }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithJsonPropertyTemplate(env, json = it) }
    }
}
