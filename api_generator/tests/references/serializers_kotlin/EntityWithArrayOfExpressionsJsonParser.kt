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

internal class EntityWithArrayOfExpressionsJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithArrayOfExpressions> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithArrayOfExpressions {
            return EntityWithArrayOfExpressions(
                items = JsonExpressionParser.readExpressionList(context, data, "items", TYPE_HELPER_STRING, ITEMS_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfExpressions): JSONObject {
            val data = JSONObject()
            data.writeExpressionList(key = "items", value = value.items)
            data.write(key = "type", value = EntityWithArrayOfExpressions.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayOfExpressionsTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayOfExpressionsTemplate?, data: JSONObject): EntityWithArrayOfExpressionsTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayOfExpressionsTemplate(
                items = JsonFieldParser.readExpressionListField(context, data, "items", TYPE_HELPER_STRING, allowOverride, parent?.items, ITEMS_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfExpressionsTemplate): JSONObject {
            val data = JSONObject()
            data.writeExpressionListField(key = "items", field = value.items)
            data.write(key = "type", value = EntityWithArrayOfExpressions.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayOfExpressionsTemplate, EntityWithArrayOfExpressions> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayOfExpressionsTemplate, data: JSONObject): EntityWithArrayOfExpressions {
            return EntityWithArrayOfExpressions(
                items = JsonFieldResolver.resolveExpressionList(context, template.items, data, "items", TYPE_HELPER_STRING, ITEMS_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ITEMS_VALIDATOR = ListValidator<String> { it: List<*> -> it.size >= 1 }
    }
}
