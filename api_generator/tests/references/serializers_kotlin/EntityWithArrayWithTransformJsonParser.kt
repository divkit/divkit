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

internal class EntityWithArrayWithTransformJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithArrayWithTransform> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithArrayWithTransform {
            return EntityWithArrayWithTransform(
                array = JsonExpressionParser.readExpressionList(context, data, "array", TYPE_HELPER_COLOR, STRING_TO_COLOR_INT, ARRAY_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayWithTransform): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpressionList(context, data, "array", value.array, COLOR_INT_TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithArrayWithTransform.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayWithTransformTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayWithTransformTemplate?, data: JSONObject): EntityWithArrayWithTransformTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayWithTransformTemplate(
                array = JsonFieldParser.readExpressionListField(context, data, "array", TYPE_HELPER_COLOR, allowOverride, parent?.array, STRING_TO_COLOR_INT, ARRAY_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayWithTransformTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionListField(context, data, "array", value.array, COLOR_INT_TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithArrayWithTransform.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayWithTransformTemplate, EntityWithArrayWithTransform> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayWithTransformTemplate, data: JSONObject): EntityWithArrayWithTransform {
            return EntityWithArrayWithTransform(
                array = JsonFieldResolver.resolveExpressionList(context, template.array, data, "array", TYPE_HELPER_COLOR, STRING_TO_COLOR_INT, ARRAY_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ARRAY_VALIDATOR = ListValidator<Int> { it: List<*> -> it.size >= 1 }
    }
}
