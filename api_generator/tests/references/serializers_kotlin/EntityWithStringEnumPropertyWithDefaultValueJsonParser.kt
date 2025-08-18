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

internal class EntityWithStringEnumPropertyWithDefaultValueJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithStringEnumPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithStringEnumPropertyWithDefaultValue {
            return EntityWithStringEnumPropertyWithDefaultValue(
                value = JsonExpressionParser.readOptionalExpression(context, data, "value", TYPE_HELPER_VALUE, EntityWithStringEnumPropertyWithDefaultValue.Value.FROM_STRING, VALUE_DEFAULT_VALUE) ?: VALUE_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringEnumPropertyWithDefaultValue): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "type", EntityWithStringEnumPropertyWithDefaultValue.TYPE)
            JsonExpressionParser.writeExpression(context, data, "value", value.value, EntityWithStringEnumPropertyWithDefaultValue.Value.TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithStringEnumPropertyWithDefaultValueTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithStringEnumPropertyWithDefaultValueTemplate?, data: JSONObject): EntityWithStringEnumPropertyWithDefaultValueTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithStringEnumPropertyWithDefaultValueTemplate(
                value = JsonFieldParser.readOptionalFieldWithExpression(context, data, "value", TYPE_HELPER_VALUE, allowOverride, parent?.value, EntityWithStringEnumPropertyWithDefaultValue.Value.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringEnumPropertyWithDefaultValueTemplate): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "type", EntityWithStringEnumPropertyWithDefaultValue.TYPE)
            JsonFieldParser.writeExpressionField(context, data, "value", value.value, EntityWithStringEnumPropertyWithDefaultValue.Value.TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithStringEnumPropertyWithDefaultValueTemplate, EntityWithStringEnumPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithStringEnumPropertyWithDefaultValueTemplate, data: JSONObject): EntityWithStringEnumPropertyWithDefaultValue {
            return EntityWithStringEnumPropertyWithDefaultValue(
                value = JsonFieldResolver.resolveOptionalExpression(context, template.value, data, "value", TYPE_HELPER_VALUE, EntityWithStringEnumPropertyWithDefaultValue.Value.FROM_STRING, VALUE_DEFAULT_VALUE) ?: VALUE_DEFAULT_VALUE,
            )
        }
    }

    private companion object {

        @JvmField val VALUE_DEFAULT_VALUE = Expression.constant(EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND)

        @JvmField val TYPE_HELPER_VALUE = TypeHelper.from(default = EntityWithStringEnumPropertyWithDefaultValue.Value.SECOND) { it is EntityWithStringEnumPropertyWithDefaultValue.Value }
    }
}
