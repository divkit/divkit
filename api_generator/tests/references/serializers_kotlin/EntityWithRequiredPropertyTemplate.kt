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
        property = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithRequiredProperty {
        return builtInParserComponent.entityWithRequiredPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithRequiredPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_required_property"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithRequiredPropertyTemplate(env, json = it) }
    }
}
