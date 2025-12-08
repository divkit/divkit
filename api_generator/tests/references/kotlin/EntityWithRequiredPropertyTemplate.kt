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

class EntityWithRequiredPropertyTemplate(
    @JvmField val property: Field<Expression<String>>,
) : JSONSerializable, JsonTemplate<EntityWithRequiredProperty> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithRequiredPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        property = JsonTemplateParser.readFieldWithExpression(json, "property", topLevel, parent?.property, PROPERTY_TEMPLATE_VALIDATOR, env.logger, env, TYPE_HELPER_STRING)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithRequiredProperty {
        return EntityWithRequiredProperty(
            property = this.property.resolve(env = env, key = "property", data = data, reader = PROPERTY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "property", field = property)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_required_property"

        private val PROPERTY_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }

        val PROPERTY_READER: Reader<Expression<String>> = { key, json, env -> JsonParser.readExpression(json, key, PROPERTY_VALIDATOR, env.logger, env, TYPE_HELPER_STRING) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRequiredPropertyTemplate(env, json = it) }
    }
}
