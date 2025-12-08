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

class EntityWithOptionalComplexPropertyTemplate(
    @JvmField val property: Field<PropertyTemplate>,
) : JSONSerializable, JsonTemplate<EntityWithOptionalComplexProperty> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithOptionalComplexPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        property = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithOptionalComplexProperty {
        return builtInParserComponent.entityWithOptionalComplexPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithOptionalComplexPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_optional_complex_property"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithOptionalComplexPropertyTemplate(env, json = it) }
    }

    class PropertyTemplate(
        @JvmField val value: Field<Expression<Uri>>,
    ) : JSONSerializable, JsonTemplate<EntityWithOptionalComplexProperty.Property> {

        constructor(
            env: ParsingEnvironment,
            parent: PropertyTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) : this(
            value = Field.nullField(false),
        ) {
            throw UnsupportedOperationException("Do not use this constructor directly.")
        }

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithOptionalComplexProperty.Property {
            return builtInParserComponent.entityWithOptionalComplexPropertyPropertyJsonTemplateResolver
                .value
                .resolve(context = env, template = this, data = data)
        }

        override fun writeToJSON(): JSONObject {
            return builtInParserComponent.entityWithOptionalComplexPropertyPropertyJsonTemplateParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> PropertyTemplate(env, json = it) }
        }
    }
}
