// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*
import org.json.JSONArray

@Mockable
class EntityWithJsonPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithJsonProperty> {
    @JvmField final val jsonProperty: Field<JSONObject> // default value: { "key": "value", "items": [ "value" ] }

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithJsonPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        var jsonProperty: Field<JSONObject>? = null
        for (jsonKey in json.keys()) {
            when (jsonKey) {
                "json_property" -> jsonProperty = JsonTemplateParser.readOptionalField(json, "json_property", topLevel, parent?.jsonProperty, logger, env)
                "\$json_property" -> jsonProperty = jsonProperty ?: JsonTemplateParser.readOptionalReferenceField(json, "json_property", topLevel, logger, env)
            }
        }
        this.jsonProperty = jsonProperty ?: JsonTemplateParser.readOptionalFallbackField(topLevel, parent?.jsonProperty)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithJsonProperty {
        return EntityWithJsonProperty(
            jsonProperty = jsonProperty.resolveOptional(env = env, key = "json_property", data = rawData, reader = JSON_PROPERTY_READER) ?: JSON_PROPERTY_DEFAULT_VALUE
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "json_property", field = jsonProperty)
        json.write(key = "type", value = TYPE)
        return json
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

        val JSON_PROPERTY_READER: Reader<JSONObject> = { key, json, env -> JsonParser.readOptional(json, key, env.logger, env) ?: JSON_PROPERTY_DEFAULT_VALUE }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithJsonPropertyTemplate(env, json = it) }
    }

}
