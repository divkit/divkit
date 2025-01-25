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
            val logger = context.logger
            return EntityWithPropertyWithDefaultValue(
                int = JsonExpressionParser.readOptionalExpression(context, logger, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nested = JsonPropertyParser.readOptional(context, logger, data, "nested", component.entityWithPropertyWithDefaultValueNestedJsonEntityParser),
                url = JsonExpressionParser.readOptionalExpression(context, logger, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValue): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "int", value = value.int)
            data.write(key = "nested", value = component.entityWithPropertyWithDefaultValueNestedJsonEntityParser.value.serialize(context, value.nested))
            data.write(key = "type", value = EntityWithPropertyWithDefaultValue.TYPE)
            data.writeExpression(key = "url", value = value.url, converter = URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithPropertyWithDefaultValueTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithPropertyWithDefaultValueTemplate?, data: JSONObject): EntityWithPropertyWithDefaultValueTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithPropertyWithDefaultValueTemplate(
                int = JsonFieldParser.readOptionalFieldWithExpression(context, logger, data, "int", TYPE_HELPER_INT, allowOverride, parent?.int, NUMBER_TO_INT, INT_VALIDATOR),
                nested = JsonFieldParser.readOptionalField(context, logger, data, "nested", allowOverride, parent?.nested, component.entityWithPropertyWithDefaultValueNestedJsonTemplateParser),
                url = JsonFieldParser.readOptionalFieldWithExpression(context, logger, data, "url", TYPE_HELPER_URI, allowOverride, parent?.url, ANY_TO_URI, URL_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValueTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "int", field = value.int)
            data.writeField(key = "nested", field = value.nested, converter = component.entityWithPropertyWithDefaultValueNestedJsonTemplateParser.value.asConverter(context))
            data.write(key = "type", value = EntityWithPropertyWithDefaultValue.TYPE)
            data.writeFieldWithExpression(key = "url", field = value.url, converter = URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithPropertyWithDefaultValueTemplate, EntityWithPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithPropertyWithDefaultValueTemplate, data: JSONObject): EntityWithPropertyWithDefaultValue {
            val logger = context.logger
            return EntityWithPropertyWithDefaultValue(
                int = JsonFieldResolver.resolveOptionalExpression(context, logger, template.int, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nested = JsonFieldResolver.resolveOptional(context, logger, template.nested, data, "nested", component.entityWithPropertyWithDefaultValueNestedJsonTemplateResolver, component.entityWithPropertyWithDefaultValueNestedJsonEntityParser),
                url = JsonFieldResolver.resolveOptionalExpression(context, logger, template.url, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
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
            val logger = context.logger
            return EntityWithPropertyWithDefaultValue.Nested(
                int = JsonExpressionParser.readOptionalExpression(context, logger, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nonOptional = JsonExpressionParser.readExpression(context, logger, data, "non_optional", TYPE_HELPER_STRING),
                url = JsonExpressionParser.readOptionalExpression(context, logger, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValue.Nested): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "int", value = value.int)
            data.writeExpression(key = "non_optional", value = value.nonOptional)
            data.writeExpression(key = "url", value = value.url, converter = URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithPropertyWithDefaultValueTemplate.NestedTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate?, data: JSONObject): EntityWithPropertyWithDefaultValueTemplate.NestedTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithPropertyWithDefaultValueTemplate.NestedTemplate(
                int = JsonFieldParser.readOptionalFieldWithExpression(context, logger, data, "int", TYPE_HELPER_INT, allowOverride, parent?.int, NUMBER_TO_INT, INT_VALIDATOR),
                nonOptional = JsonFieldParser.readFieldWithExpression(context, logger, data, "non_optional", TYPE_HELPER_STRING, allowOverride, parent?.nonOptional),
                url = JsonFieldParser.readOptionalFieldWithExpression(context, logger, data, "url", TYPE_HELPER_URI, allowOverride, parent?.url, ANY_TO_URI, URL_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "int", field = value.int)
            data.writeFieldWithExpression(key = "non_optional", field = value.nonOptional)
            data.writeFieldWithExpression(key = "url", field = value.url, converter = URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithPropertyWithDefaultValueTemplate.NestedTemplate, EntityWithPropertyWithDefaultValue.Nested> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithPropertyWithDefaultValueTemplate.NestedTemplate, data: JSONObject): EntityWithPropertyWithDefaultValue.Nested {
            val logger = context.logger
            return EntityWithPropertyWithDefaultValue.Nested(
                int = JsonFieldResolver.resolveOptionalExpression(context, logger, template.int, data, "int", TYPE_HELPER_INT, NUMBER_TO_INT, INT_VALIDATOR, INT_DEFAULT_VALUE) ?: INT_DEFAULT_VALUE,
                nonOptional = JsonFieldResolver.resolveExpression(context, logger, template.nonOptional, data, "non_optional", TYPE_HELPER_STRING),
                url = JsonFieldResolver.resolveOptionalExpression(context, logger, template.url, data, "url", TYPE_HELPER_URI, ANY_TO_URI, URL_VALIDATOR, URL_DEFAULT_VALUE) ?: URL_DEFAULT_VALUE,
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
