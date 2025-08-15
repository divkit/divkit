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

internal class EntityWithPropertyWithDefaultValueJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithPropertyWithDefaultValue {
            return EntityWithPropertyWithDefaultValue(
                int = JsonExpressionParser.readOptionalExpression(context, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nested = JsonPropertyParser.readOptional(context, data, "nested", component.entityWithPropertyWithDefaultValueNestedJsonEntityParser),
                url = JsonExpressionParser.readOptionalExpression(context, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValue): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "int", value.int)
            JsonPropertyParser.write(context, data, "nested", value.nested, component.entityWithPropertyWithDefaultValueNestedJsonEntityParser)
            JsonPropertyParser.write(context, data, "type", EntityWithPropertyWithDefaultValue.TYPE)
            JsonExpressionParser.writeExpression(context, data, "url", value.url, URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithPropertyWithDefaultValueTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithPropertyWithDefaultValueTemplate?, data: JSONObject): EntityWithPropertyWithDefaultValueTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithPropertyWithDefaultValueTemplate(
                int = JsonFieldParser.readOptionalFieldWithExpression(context, data, "int", TYPE_HELPER_INT, allowOverride, parent?.int, NUMBER_TO_INT, INT_VALIDATOR),
                nested = JsonFieldParser.readOptionalField(context, data, "nested", allowOverride, parent?.nested, component.entityWithPropertyWithDefaultValueNestedJsonTemplateParser),
                url = JsonFieldParser.readOptionalFieldWithExpression(context, data, "url", TYPE_HELPER_URI, allowOverride, parent?.url, ANY_TO_URI, URL_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValueTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "int", value.int)
            JsonFieldParser.writeField(context, data, "nested", value.nested, component.entityWithPropertyWithDefaultValueNestedJsonTemplateParser)
            JsonPropertyParser.write(context, data, "type", EntityWithPropertyWithDefaultValue.TYPE)
            JsonFieldParser.writeExpressionField(context, data, "url", value.url, URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithPropertyWithDefaultValueTemplate, EntityWithPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithPropertyWithDefaultValueTemplate, data: JSONObject): EntityWithPropertyWithDefaultValue {
            return EntityWithPropertyWithDefaultValue(
                int = JsonFieldResolver.resolveOptionalExpression(context, template.int, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nested = JsonFieldResolver.resolveOptional(context, template.nested, data, "nested", component.entityWithPropertyWithDefaultValueNestedJsonTemplateResolver, component.entityWithPropertyWithDefaultValueNestedJsonEntityParser),
                url = JsonFieldResolver.resolveOptionalExpression(context, template.url, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
            )
        }
    }

    private companion object {

        @JvmField val INT_DEFAULT_VALUE = Expression.constant(0L)
        @JvmField val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        @JvmField val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        @JvmField val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }
    }
}

internal class EntityWithPropertyWithDefaultValueNestedJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithPropertyWithDefaultValue.Nested> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithPropertyWithDefaultValue.Nested {
            return EntityWithPropertyWithDefaultValue.Nested(
                int = JsonExpressionParser.readOptionalExpression(context, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nonOptional = JsonExpressionParser.readExpression(context, data, "non_optional", TYPE_HELPER_STRING),
                url = JsonExpressionParser.readOptionalExpression(context, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValue.Nested): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "int", value.int)
            JsonExpressionParser.writeExpression(context, data, "non_optional", value.nonOptional)
            JsonExpressionParser.writeExpression(context, data, "url", value.url, URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithPropertyWithDefaultValueTemplate.NestedTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate?, data: JSONObject): EntityWithPropertyWithDefaultValueTemplate.NestedTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithPropertyWithDefaultValueTemplate.NestedTemplate(
                int = JsonFieldParser.readOptionalFieldWithExpression(context, data, "int", TYPE_HELPER_INT, allowOverride, parent?.int, NUMBER_TO_INT, INT_VALIDATOR),
                nonOptional = JsonFieldParser.readFieldWithExpression(context, data, "non_optional", TYPE_HELPER_STRING, allowOverride, parent?.nonOptional),
                url = JsonFieldParser.readOptionalFieldWithExpression(context, data, "url", TYPE_HELPER_URI, allowOverride, parent?.url, ANY_TO_URI, URL_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "int", value.int)
            JsonFieldParser.writeExpressionField(context, data, "non_optional", value.nonOptional)
            JsonFieldParser.writeExpressionField(context, data, "url", value.url, URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithPropertyWithDefaultValueTemplate.NestedTemplate, EntityWithPropertyWithDefaultValue.Nested> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate, data: JSONObject): EntityWithPropertyWithDefaultValue.Nested {
            return EntityWithPropertyWithDefaultValue.Nested(
                int = JsonFieldResolver.resolveOptionalExpression(context, template.int, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nonOptional = JsonFieldResolver.resolveExpression(context, template.nonOptional, data, "non_optional", TYPE_HELPER_STRING),
                url = JsonFieldResolver.resolveOptionalExpression(context, template.url, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
            )
        }
    }

    private companion object {

        @JvmField val INT_DEFAULT_VALUE = Expression.constant(0L)
        @JvmField val URL_DEFAULT_VALUE = Expression.constant(Uri.parse("https://yandex.ru"))

        @JvmField val INT_VALIDATOR = ValueValidator<Long> { it: Long -> it >= 0 }
        @JvmField val URL_VALIDATOR = ValueValidator<Uri> { it.hasScheme(listOf("https")) }
    }
}
