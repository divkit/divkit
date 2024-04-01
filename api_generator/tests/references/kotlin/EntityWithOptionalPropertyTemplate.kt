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
class EntityWithOptionalPropertyTemplate : JSONSerializable, JsonTemplate<EntityWithOptionalProperty> {
    @JvmField final val property: Field<Expression<String>>

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithOptionalPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        property = JsonTemplateParser.readOptionalFieldWithExpression(json, "property", topLevel, parent?.property, logger, env, TYPE_HELPER_STRING)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithOptionalProperty {
        return EntityWithOptionalProperty(
            property = property.resolveOptional(env = env, key = "property", data = rawData, reader = PROPERTY_READER)
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
