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
class EntityWithOptionalStringEnumPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithOptionalStringEnumProperty> {
    @JvmField final val property: Field<Expression<EntityWithOptionalStringEnumProperty.Property>>

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithOptionalStringEnumPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        property = JsonTemplateParser.readOptionalFieldWithExpression(json, "property", topLevel, parent?.property, EntityWithOptionalStringEnumProperty.Property.Converter.FROM_STRING, logger, env, TYPE_HELPER_PROPERTY)
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithOptionalStringEnumProperty {
        return EntityWithOptionalStringEnumProperty(
            property = property.resolveOptional(env = env, key = "property", data = data, reader = PROPERTY_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "property", field = property, converter = { v: EntityWithOptionalStringEnumProperty.Property -> EntityWithOptionalStringEnumProperty.Property.toString(v) })
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"

        private val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithOptionalStringEnumProperty.Property.values().first()) { it is EntityWithOptionalStringEnumProperty.Property }

        val PROPERTY_READER: Reader<Expression<EntityWithOptionalStringEnumProperty.Property>?> = { key, json, env -> JsonParser.readOptionalExpression(json, key, EntityWithOptionalStringEnumProperty.Property.Converter.FROM_STRING, env.logger, env, TYPE_HELPER_PROPERTY) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalStringEnumPropertyTemplate(env, json = it) }
    }

}
