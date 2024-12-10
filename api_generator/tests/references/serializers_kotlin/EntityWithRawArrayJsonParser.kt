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

internal class EntityWithRawArrayJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithRawArray> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithRawArray {
            return EntityWithRawArray(
                array = JsonExpressionParser.readExpression(context, data, "array", TYPE_HELPER_JSON_ARRAY),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithRawArray): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "array", value = value.array)
            data.write(key = "type", value = EntityWithRawArray.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithRawArrayTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithRawArrayTemplate?, data: JSONObject): EntityWithRawArrayTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithRawArrayTemplate(
                array = JsonFieldParser.readFieldWithExpression(context, data, "array", TYPE_HELPER_JSON_ARRAY, allowOverride, parent?.array),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithRawArrayTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "array", field = value.array)
            data.write(key = "type", value = EntityWithRawArray.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithRawArrayTemplate, EntityWithRawArray> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithRawArrayTemplate, data: JSONObject): EntityWithRawArray {
            return EntityWithRawArray(
                array = JsonFieldResolver.resolveExpression(context, template.array, data, "array", TYPE_HELPER_JSON_ARRAY),
            )
        }
    }
}
