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

internal class EntityWithSimplePropertiesJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithSimpleProperties> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithSimpleProperties {
            return EntityWithSimpleProperties(
                boolean = JsonExpressionParser.readOptionalExpression(context, data, "boolean", TYPE_HELPER_BOOLEAN, ANY_TO_BOOLEAN),
                booleanInt = JsonExpressionParser.readOptionalExpression(context, data, "boolean_int", TYPE_HELPER_BOOLEAN, ANY_TO_BOOLEAN),
                color = JsonExpressionParser.readOptionalExpression(context, data, "color", TYPE_HELPER_COLOR, STRING_TO_COLOR_INT),
                double = JsonExpressionParser.readOptionalExpression(context, data, "double", TYPE_HELPER_DOUBLE, NUMBER_TO_DOUBLE),
                id = JsonPropertyParser.readOptional(context, data, "id", NUMBER_TO_INT) ?: ID_DEFAULT_VALUE,
                integer = JsonExpressionParser.readOptionalExpression(context, data, "integer", TYPE_HELPER_INT, NUMBER_TO_INT, INTEGER_DEFAULT_VALUE) ?: INTEGER_DEFAULT_VALUE,
                positiveInteger = JsonExpressionParser.readOptionalExpression(context, data, "positive_integer", TYPE_HELPER_INT, NUMBER_TO_INT, POSITIVE_INTEGER_VALIDATOR),
                string = JsonExpressionParser.readOptionalExpression(context, data, "string", TYPE_HELPER_STRING),
                url = JsonExpressionParser.readOptionalExpression(context, data, "url", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithSimpleProperties): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "boolean", value.boolean)
            JsonExpressionParser.writeExpression(context, data, "boolean_int", value.booleanInt)
            JsonExpressionParser.writeExpression(context, data, "color", value.color, COLOR_INT_TO_STRING)
            JsonExpressionParser.writeExpression(context, data, "double", value.double)
            JsonPropertyParser.write(context, data, "id", value.id)
            JsonExpressionParser.writeExpression(context, data, "integer", value.integer)
            JsonExpressionParser.writeExpression(context, data, "positive_integer", value.positiveInteger)
            JsonExpressionParser.writeExpression(context, data, "string", value.string)
            JsonPropertyParser.write(context, data, "type", EntityWithSimpleProperties.TYPE)
            JsonExpressionParser.writeExpression(context, data, "url", value.url, URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithSimplePropertiesTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithSimplePropertiesTemplate?, data: JSONObject): EntityWithSimplePropertiesTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithSimplePropertiesTemplate(
                boolean = JsonFieldParser.readOptionalFieldWithExpression(context, data, "boolean", TYPE_HELPER_BOOLEAN, allowOverride, parent?.boolean, ANY_TO_BOOLEAN),
                booleanInt = JsonFieldParser.readOptionalFieldWithExpression(context, data, "boolean_int", TYPE_HELPER_BOOLEAN, allowOverride, parent?.booleanInt, ANY_TO_BOOLEAN),
                color = JsonFieldParser.readOptionalFieldWithExpression(context, data, "color", TYPE_HELPER_COLOR, allowOverride, parent?.color, STRING_TO_COLOR_INT),
                double = JsonFieldParser.readOptionalFieldWithExpression(context, data, "double", TYPE_HELPER_DOUBLE, allowOverride, parent?.double, NUMBER_TO_DOUBLE),
                id = JsonFieldParser.readOptionalField(context, data, "id", allowOverride, parent?.id, NUMBER_TO_INT),
                integer = JsonFieldParser.readOptionalFieldWithExpression(context, data, "integer", TYPE_HELPER_INT, allowOverride, parent?.integer, NUMBER_TO_INT),
                positiveInteger = JsonFieldParser.readOptionalFieldWithExpression(context, data, "positive_integer", TYPE_HELPER_INT, allowOverride, parent?.positiveInteger, NUMBER_TO_INT, POSITIVE_INTEGER_VALIDATOR),
                string = JsonFieldParser.readOptionalFieldWithExpression(context, data, "string", TYPE_HELPER_STRING, allowOverride, parent?.string),
                url = JsonFieldParser.readOptionalFieldWithExpression(context, data, "url", TYPE_HELPER_URI, allowOverride, parent?.url, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithSimplePropertiesTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "boolean", value.boolean)
            JsonFieldParser.writeExpressionField(context, data, "boolean_int", value.booleanInt)
            JsonFieldParser.writeExpressionField(context, data, "color", value.color, COLOR_INT_TO_STRING)
            JsonFieldParser.writeExpressionField(context, data, "double", value.double)
            JsonFieldParser.writeField(context, data, "id", value.id)
            JsonFieldParser.writeExpressionField(context, data, "integer", value.integer)
            JsonFieldParser.writeExpressionField(context, data, "positive_integer", value.positiveInteger)
            JsonFieldParser.writeExpressionField(context, data, "string", value.string)
            JsonPropertyParser.write(context, data, "type", EntityWithSimpleProperties.TYPE)
            JsonFieldParser.writeExpressionField(context, data, "url", value.url, URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithSimplePropertiesTemplate, EntityWithSimpleProperties> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithSimplePropertiesTemplate, data: JSONObject): EntityWithSimpleProperties {
            return EntityWithSimpleProperties(
                boolean = JsonFieldResolver.resolveOptionalExpression(context, template.boolean, data, "boolean", TYPE_HELPER_BOOLEAN, ANY_TO_BOOLEAN),
                booleanInt = JsonFieldResolver.resolveOptionalExpression(context, template.booleanInt, data, "boolean_int", TYPE_HELPER_BOOLEAN, ANY_TO_BOOLEAN),
                color = JsonFieldResolver.resolveOptionalExpression(context, template.color, data, "color", TYPE_HELPER_COLOR, STRING_TO_COLOR_INT),
                double = JsonFieldResolver.resolveOptionalExpression(context, template.double, data, "double", TYPE_HELPER_DOUBLE, NUMBER_TO_DOUBLE),
                id = JsonFieldResolver.resolveOptional(context, template.id, data, "id", NUMBER_TO_INT) ?: ID_DEFAULT_VALUE,
                integer = JsonFieldResolver.resolveOptionalExpression(context, template.integer, data, "integer", TYPE_HELPER_INT, NUMBER_TO_INT, INTEGER_DEFAULT_VALUE) ?: INTEGER_DEFAULT_VALUE,
                positiveInteger = JsonFieldResolver.resolveOptionalExpression(context, template.positiveInteger, data, "positive_integer", TYPE_HELPER_INT, NUMBER_TO_INT, POSITIVE_INTEGER_VALIDATOR),
                string = JsonFieldResolver.resolveOptionalExpression(context, template.string, data, "string", TYPE_HELPER_STRING),
                url = JsonFieldResolver.resolveOptionalExpression(context, template.url, data, "url", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }
    }

    private companion object {

        @JvmField val ID_DEFAULT_VALUE = 0L
        @JvmField val INTEGER_DEFAULT_VALUE = Expression.constant(0L)

        @JvmField val POSITIVE_INTEGER_VALIDATOR = ValueValidator<Long> { it: Long -> it > 0 }
    }
}
