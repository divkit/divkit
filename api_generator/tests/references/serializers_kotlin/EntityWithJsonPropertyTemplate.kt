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

class EntityWithJsonPropertyTemplate(
    @JvmField val jsonProperty: Field<JSONObject>,
) : JSONSerializable, JsonTemplate<EntityWithJsonProperty> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithJsonPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        jsonProperty = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithJsonProperty {
        return builtInParserComponent.entityWithJsonPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithJsonPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
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

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithJsonPropertyTemplate(env, json = it) }
    }
}
