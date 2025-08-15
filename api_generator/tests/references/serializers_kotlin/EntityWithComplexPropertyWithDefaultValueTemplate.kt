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

class EntityWithComplexPropertyWithDefaultValueTemplate : JSONSerializable, JsonTemplate<EntityWithComplexPropertyWithDefaultValue> {
    @JvmField val property: Field<PropertyTemplate>

    constructor(
        property: Field<PropertyTemplate>,
    ) {
        this.property = property
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithComplexPropertyWithDefaultValueTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        property = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexPropertyWithDefaultValue {
        return builtInParserComponent.entityWithComplexPropertyWithDefaultValueJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithComplexPropertyWithDefaultValueJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_complex_property_with_default_value"

        private val PROPERTY_DEFAULT_VALUE = EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithComplexPropertyWithDefaultValueTemplate(env, json = it) }
    }

    class PropertyTemplate : JSONSerializable, JsonTemplate<EntityWithComplexPropertyWithDefaultValue.Property> {
        @JvmField val value: Field<Expression<String>>

        constructor(
            value: Field<Expression<String>>,
        ) {
            this.value = value
        }

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

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithComplexPropertyWithDefaultValue.Property {
            return builtInParserComponent.entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateResolver
                .value
                .resolve(context = env, template = this, data = data)
        }

        override fun writeToJSON(): JSONObject {
            return builtInParserComponent.entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> PropertyTemplate(env, json = it) }
        }
    }
}
