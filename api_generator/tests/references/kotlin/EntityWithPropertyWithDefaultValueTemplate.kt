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

class EntityWithPropertyWithDefaultValueTemplate(
    @JvmField val int: Field<Expression<Long>>,
    @JvmField val nested: Field<NestedTemplate>,
    @JvmField val url: Field<Expression<Uri>>,
) : JSONSerializable, JsonTemplate<EntityWithPropertyWithDefaultValue> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithPropertyWithDefaultValueTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        int = JsonTemplateParser.readOptionalFieldWithExpression(json, "int", topLevel, parent?.int, NUMBER_TO_INT, INT_TEMPLATE_VALIDATOR, env.logger, env, TYPE_HELPER_INT),
        nested = JsonTemplateParser.readOptionalField(json, "nested", topLevel, parent?.nested, NestedTemplate.CREATOR, env.logger, env),
        url = JsonTemplateParser.readOptionalFieldWithExpression(json, "url", topLevel, parent?.url, ANY_TO_URI, URL_TEMPLATE_VALIDATOR, env.logger, env, TYPE_HELPER_URI)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithPropertyWithDefaultValue {
        return EntityWithPropertyWithDefaultValue(
            int = this.int.resolveOptional(env = env, key = "int", data = data, reader = INT_READER) ?: INT_DEFAULT_VALUE,
            nested = this.nested.resolveOptionalTemplate(env = env, key = "nested", data = data, reader = NESTED_READER),
            url = this.url.resolveOptional(env = env, key = "url", data = data, reader = URL_READER) ?: URL_DEFAULT_VALUE
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeFieldWithExpression(key = "int", field = int)
        json.writeField(key = "nested", field = nested)
        json.write(key = "type", value = TYPE)
        json.writeFieldWithExpression(key = "url", field = url, converter = URI_TO_STRING)
        return json
    }

    companion object {
        const val TYPE = "entity_with_property_with_default_value"

        private val INT_DEFAULT_VALUE = Expression.constant(0L)
        private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        private val INT_TEMPLATE_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        private val URL_TEMPLATE_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }
        private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }

        val INT_READER: Reader<Expression<Long>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_INT, INT_VALIDATOR, env.logger, env, INT_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INT_DEFAULT_VALUE }
        val NESTED_READER: Reader<EntityWithPropertyWithDefaultValue.Nested?> = { key, json, env -> JsonParser.readOptional(json, key, EntityWithPropertyWithDefaultValue.Nested.CREATOR, env.logger, env) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }
        val URL_READER: Reader<Expression<Uri>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, ANY_TO_URI, URL_VALIDATOR, env.logger, env, URL_DEFAULT_VALUE, TYPE_HELPER_URI) ?: URL_DEFAULT_VALUE }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithPropertyWithDefaultValueTemplate(env, json = it) }
    }

    class NestedTemplate(
        @JvmField val int: Field<Expression<Long>>,
        @JvmField val nonOptional: Field<Expression<String>>,
        @JvmField val url: Field<Expression<Uri>>,
    ) : JSONSerializable, JsonTemplate<EntityWithPropertyWithDefaultValue.Nested> {

        constructor(
            env: ParsingEnvironment,
            parent: NestedTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) : this(
            int = JsonTemplateParser.readOptionalFieldWithExpression(json, "int", topLevel, parent?.int, NUMBER_TO_INT, INT_TEMPLATE_VALIDATOR, env.logger, env, TYPE_HELPER_INT),
            nonOptional = JsonTemplateParser.readFieldWithExpression(json, "non_optional", topLevel, parent?.nonOptional, env.logger, env, TYPE_HELPER_STRING),
            url = JsonTemplateParser.readOptionalFieldWithExpression(json, "url", topLevel, parent?.url, ANY_TO_URI, URL_TEMPLATE_VALIDATOR, env.logger, env, TYPE_HELPER_URI)
        )

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithPropertyWithDefaultValue.Nested {
            return EntityWithPropertyWithDefaultValue.Nested(
                int = this.int.resolveOptional(env = env, key = "int", data = data, reader = INT_READER) ?: INT_DEFAULT_VALUE,
                nonOptional = this.nonOptional.resolve(env = env, key = "non_optional", data = data, reader = NON_OPTIONAL_READER),
                url = this.url.resolveOptional(env = env, key = "url", data = data, reader = URL_READER) ?: URL_DEFAULT_VALUE
            )
        }

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeFieldWithExpression(key = "int", field = int)
            json.writeFieldWithExpression(key = "non_optional", field = nonOptional)
            json.writeFieldWithExpression(key = "url", field = url, converter = URI_TO_STRING)
            return json
        }

        companion object {
            private val INT_DEFAULT_VALUE = Expression.constant(0L)
            private val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

            private val INT_TEMPLATE_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
            private val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
            private val URL_TEMPLATE_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }
            private val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }

            val INT_READER: Reader<Expression<Long>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, NUMBER_TO_INT, INT_VALIDATOR, env.logger, env, INT_DEFAULT_VALUE, TYPE_HELPER_INT) ?: INT_DEFAULT_VALUE }
            val NON_OPTIONAL_READER: Reader<Expression<String>> = { key, json, env -> JsonParser.readExpression(json, key, env.logger, env, TYPE_HELPER_STRING) }
            val URL_READER: Reader<Expression<Uri>> = { key, json, env -> JsonParser.readOptionalExpression(json, key, ANY_TO_URI, URL_VALIDATOR, env.logger, env, URL_DEFAULT_VALUE, TYPE_HELPER_URI) ?: URL_DEFAULT_VALUE }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> NestedTemplate(env, json = it) }
        }
    }
}
