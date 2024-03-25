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
class EntityWithRequiredPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithRequiredProperty> {
    @JvmField final val property: Field<Expression<String>> // at least 1 char

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithRequiredPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        var property: Field<Expression<String>>? = null
        this.property = property ?: JsonTemplateParser.readFieldWithExpression(json, "property", topLevel, parent?.property, PROPERTY_TEMPLATE_VALIDATOR, logger, env, TYPE_HELPER_STRING)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithRequiredProperty {
        return EntityWithRequiredProperty(
            property = property.resolve(env = env, key = "property", data = rawData, reader = PROPERTY_READER)
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
