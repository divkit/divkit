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

class EntityWithComplexPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithComplexProperty> {
    @JvmField val property: Field<PropertyTemplate>

    constructor(
        property: Field<PropertyTemplate>,
    ) {
        this.property = property
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithComplexPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        property = JsonTemplateParser.readField(json, "property", topLevel, parent?.property, PropertyTemplate.CREATOR, logger, env)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexProperty {
        return EntityWithComplexProperty(
            property = this.property.resolveTemplate(env = env, key = "property", data = data, reader = PROPERTY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "property", field = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_complex_property"

        val PROPERTY_READER: Reader<EntityWithComplexProperty.Property> = { key, json, env -> JsonParser.read(json, key, EntityWithComplexProperty.Property.CREATOR, env.logger, env) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexPropertyTemplate(env, json = it) }
    }

    class PropertyTemplate : JSONSerializable, JsonTemplate<EntityWithComplexProperty.Property> {
        @JvmField val value: Field<Expression<Uri>>

        constructor(
            value: Field<Expression<Uri>>,
        ) {
            this.value = value
        }

        constructor(
            env: ParsingEnvironment,
            parent: PropertyTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) {
            val logger = env.logger
            value = JsonTemplateParser.readFieldWithExpression(json, "value", topLevel, parent?.value, ANY_TO_URI, logger, env, TYPE_HELPER_URI)
        }

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexProperty.Property {
            return EntityWithComplexProperty.Property(
                value = this.value.resolve(env = env, key = "value", data = data, reader = VALUE_READER)
            )
        }

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeFieldWithExpression(key = "value", field = value, converter = URI_TO_STRING)
            return json
        }

        companion object {
            val VALUE_READER: Reader<Expression<Uri>> = { key, json, env -> JsonParser.readExpression(json, key, ANY_TO_URI, env.logger, env, TYPE_HELPER_URI) }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> PropertyTemplate(env, json = it) }
        }
    }
}
