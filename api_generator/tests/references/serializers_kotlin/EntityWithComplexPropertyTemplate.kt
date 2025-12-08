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

class EntityWithComplexPropertyTemplate(
    @JvmField val property: Field<PropertyTemplate>,
) : JSONSerializable, JsonTemplate<EntityWithComplexProperty> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithComplexPropertyTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        property = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexProperty {
        return builtInParserComponent.entityWithComplexPropertyJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithComplexPropertyJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_complex_property"

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexPropertyTemplate(env, json = it) }
    }

    class PropertyTemplate(
        @JvmField val value: Field<Expression<Uri>>,
    ) : JSONSerializable, JsonTemplate<EntityWithComplexProperty.Property> {

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

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexProperty.Property {
            return builtInParserComponent.entityWithComplexPropertyPropertyJsonTemplateResolver
                .value
                .resolve(context = env, template = this, data = data)
        }

        override fun writeToJSON(): JSONObject {
            return builtInParserComponent.entityWithComplexPropertyPropertyJsonTemplateParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> PropertyTemplate(env, json = it) }
        }
    }
}
