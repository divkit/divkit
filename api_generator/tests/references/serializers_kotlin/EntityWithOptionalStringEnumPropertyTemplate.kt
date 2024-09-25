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
    ) : this(
        property = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithOptionalStringEnumProperty {
        return builtInParserComponent.entityWithOptionalStringEnumPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithOptionalStringEnumPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalStringEnumPropertyTemplate(env, json = it) }
    }
}
