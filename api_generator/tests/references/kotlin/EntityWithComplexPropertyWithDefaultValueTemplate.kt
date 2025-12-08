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

class EntityWithComplexPropertyWithDefaultValueTemplate(
    @JvmField val property: Field<PropertyTemplate>,
) : JSONSerializable, JsonTemplate<EntityWithComplexPropertyWithDefaultValue> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithComplexPropertyWithDefaultValueTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        property = JsonTemplateParser.readOptionalField(json, "property", topLevel, parent?.property, PropertyTemplate.CREATOR, env.logger, env)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexPropertyWithDefaultValue {
        return EntityWithComplexPropertyWithDefaultValue(
            property = this.property.resolveOptionalTemplate(env = env, key = "property", data = data, reader = PROPERTY_READER) ?: PROPERTY_DEFAULT_VALUE
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "property", field = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_complex_property_with_default_value"

        private val PROPERTY_DEFAULT_VALUE = EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))

        val PROPERTY_READER: Reader<EntityWithComplexPropertyWithDefaultValue.Property> = { key, json, env -> JsonParser.readOptional(json, key, EntityWithComplexPropertyWithDefaultValue.Property.CREATOR, env.logger, env) ?: PROPERTY_DEFAULT_VALUE }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexPropertyWithDefaultValueTemplate(env, json = it) }
    }

    class PropertyTemplate(
        @JvmField val value: Field<Expression<String>>,
    ) : JSONSerializable, JsonTemplate<EntityWithComplexPropertyWithDefaultValue.Property> {

        constructor(
            env: ParsingEnvironment,
            parent: PropertyTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) : this(
            value = JsonTemplateParser.readFieldWithExpression(json, "value", topLevel, parent?.value, env.logger, env, TYPE_HELPER_STRING)
        )

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexPropertyWithDefaultValue.Property {
            return EntityWithComplexPropertyWithDefaultValue.Property(
                value = this.value.resolve(env = env, key = "value", data = data, reader = VALUE_READER)
            )
        }

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeFieldWithExpression(key = "value", field = value)
            return json
        }

        companion object {
            val VALUE_READER: Reader<Expression<String>> = { key, json, env -> JsonParser.readExpression(json, key, env.logger, env, TYPE_HELPER_STRING) }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> PropertyTemplate(env, json = it) }
        }
    }
}
