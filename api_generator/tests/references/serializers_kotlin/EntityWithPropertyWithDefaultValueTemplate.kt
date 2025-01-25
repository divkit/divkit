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

class EntityWithPropertyWithDefaultValueTemplate : JSONSerializable, JsonTemplate<EntityWithPropertyWithDefaultValue> {
    @JvmField val int: Field<Expression<Long>>
    @JvmField val nested: Field<NestedTemplate>
    @JvmField val url: Field<Expression<Uri>>

    constructor(
        int: Field<Expression<Long>>,
        nested: Field<NestedTemplate>,
        url: Field<Expression<Uri>>,
    ) {
        this.int = int
        this.nested = nested
        this.url = url
    }

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithPropertyWithDefaultValueTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        int = Field.nullField(false),
        nested = Field.nullField(false),
        url = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithPropertyWithDefaultValue {
        return builtInParserComponent.entityWithPropertyWithDefaultValueJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithPropertyWithDefaultValueJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_property_with_default_value"

        private val INT_DEFAULT_VALUE = Expression.constant(0L)
        private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithPropertyWithDefaultValueTemplate(env, json = it) }
    }

    class NestedTemplate : JSONSerializable, JsonTemplate<EntityWithPropertyWithDefaultValue.Nested> {
        @JvmField val int: Field<Expression<Long>>
        @JvmField val nonOptional: Field<Expression<String>>
        @JvmField val url: Field<Expression<Uri>>

        constructor(
            int: Field<Expression<Long>>,
            nonOptional: Field<Expression<String>>,
            url: Field<Expression<Uri>>,
        ) {
            this.int = int
            this.nonOptional = nonOptional
            this.url = url
        }

        constructor(
            env: ParsingEnvironment,
            parent: NestedTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) : this(
            int = Field.nullField(false),
            nonOptional = Field.nullField(false),
            url = Field.nullField(false),
        ) {
            throw UnsupportedOperationException("Do not use this constructor directly.")
        }

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithPropertyWithDefaultValue.Nested {
            return builtInParserComponent.entityWithPropertyWithDefaultValueNestedJsonTemplateResolver
                .value
                .resolve(context = env, template = this, data = data)
        }

        override fun writeToJSON(): JSONObject {
            return builtInParserComponent.entityWithPropertyWithDefaultValueNestedJsonTemplateParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            private val INT_DEFAULT_VALUE = Expression.constant(0L)
            private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> NestedTemplate(env, json = it) }
        }
    }
}
