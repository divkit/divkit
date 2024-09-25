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

class EntityWithStringEnumPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithStringEnumProperty> {
    @JvmField val property: Field<Expression<EntityWithStringEnumProperty.Property>>

    constructor(
        property: Field<Expression<EntityWithStringEnumProperty.Property>>,
    ) {
        this.property = property
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithStringEnumPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        property = JsonTemplateParser.readFieldWithExpression(json, "property", topLevel, parent?.property, EntityWithStringEnumProperty.Property.FROM_STRING, logger, env, TYPE_HELPER_PROPERTY)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithStringEnumProperty {
        return EntityWithStringEnumProperty(
            property = this.property.resolve(env = env, key = "property", data = data, reader = PROPERTY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "property", field = property, converter = EntityWithStringEnumProperty.Property.TO_STRING)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_string_enum_property"

        private val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithStringEnumProperty.Property.values().first()) { it is EntityWithStringEnumProperty.Property }

        val PROPERTY_READER: Reader<Expression<EntityWithStringEnumProperty.Property>> = { key, json, env -> JsonParser.readExpression(json, key, EntityWithStringEnumProperty.Property.FROM_STRING, env.logger, env, TYPE_HELPER_PROPERTY) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithStringEnumPropertyTemplate(env, json = it) }
    }
}
