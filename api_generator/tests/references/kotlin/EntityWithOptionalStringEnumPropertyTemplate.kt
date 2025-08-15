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

class EntityWithOptionalStringEnumPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithOptionalStringEnumProperty> {
    @JvmField val property: Field<Expression<EntityWithOptionalStringEnumProperty.Property>>

    constructor(
        property: Field<Expression<EntityWithOptionalStringEnumProperty.Property>>,
    ) {
        this.property = property
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithOptionalStringEnumPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        property = JsonTemplateParser.readOptionalFieldWithExpression(json, "property", topLevel, parent?.property, EntityWithOptionalStringEnumProperty.Property.FROM_STRING, logger, env, TYPE_HELPER_PROPERTY)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithOptionalStringEnumProperty {
        return EntityWithOptionalStringEnumProperty(
            property = this.property.resolveOptional(env = env, key = "property", data = data, reader = PROPERTY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "property", field = property, converter = EntityWithOptionalStringEnumProperty.Property.TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"

        private val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithOptionalStringEnumProperty.Property.values().first()) { it is EntityWithOptionalStringEnumProperty.Property }

        val PROPERTY_READER: Reader<Expression<EntityWithOptionalStringEnumProperty.Property>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, EntityWithOptionalStringEnumProperty.Property.FROM_STRING, env.logger, env, TYPE_HELPER_PROPERTY) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalStringEnumPropertyTemplate(env, json = it) }
    }
}
