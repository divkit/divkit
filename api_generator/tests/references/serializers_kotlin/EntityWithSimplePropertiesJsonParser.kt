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
            data.writeExpression(key = "boolean", value = value.boolean)
            data.writeExpression(key = "boolean_int", value = value.booleanInt)
            data.writeExpression(key = "color", value = value.color, converter = COLOR_INT_TO_STRING)
            data.writeExpression(key = "double", value = value.double)
            data.write(key = "id", value = value.id)
            data.writeExpression(key = "integer", value = value.integer)
            data.writeExpression(key = "positive_integer", value = value.positiveInteger)
            data.writeExpression(key = "string", value = value.string)
            data.write(key = "type", value = EntityWithSimpleProperties.TYPE)
            data.writeExpression(key = "url", value = value.url, converter = URI_TO_STRING)
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
            data.writeFieldWithExpression(key = "boolean", field = value.boolean)
            data.writeFieldWithExpression(key = "boolean_int", field = value.booleanInt)
            data.writeFieldWithExpression(key = "color", field = value.color, converter = COLOR_INT_TO_STRING)
            data.writeFieldWithExpression(key = "double", field = value.double)
            data.writeField(key = "id", field = value.id)
            data.writeFieldWithExpression(key = "integer", field = value.integer)
            data.writeFieldWithExpression(key = "positive_integer", field = value.positiveInteger)
            data.writeFieldWithExpression(key = "string", field = value.string)
            data.write(key = "type", value = EntityWithSimpleProperties.TYPE)
            data.writeFieldWithExpression(key = "url", field = value.url, converter = URI_TO_STRING)
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
