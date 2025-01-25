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

class EntityWithOptionalPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithOptionalProperty> {
    @JvmField val property: Field<Expression<String>>

    constructor(
        property: Field<Expression<String>>,
    ) {
        this.property = property
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithOptionalPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        property = JsonTemplateParser.readOptionalFieldWithExpression(json, "property", topLevel, parent?.property, logger, env, TYPE_HELPER_STRING)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithOptionalProperty {
        return EntityWithOptionalProperty(
            property = this.property.resolveOptional(env = env, key = "property", data = data, reader = PROPERTY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "property", field = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_optional_property"

        val PROPERTY_READER: Reader<Expression<String>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, env.logger, env, TYPE_HELPER_STRING) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalPropertyTemplate(env, json = it) }
    }
}
