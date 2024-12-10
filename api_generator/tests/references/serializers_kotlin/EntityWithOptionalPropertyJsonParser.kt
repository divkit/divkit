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

internal class EntityWithOptionalPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithOptionalProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithOptionalProperty {
            return EntityWithOptionalProperty(
                property = JsonExpressionParser.readOptionalExpression(context, data, "property", TYPE_HELPER_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalProperty): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "property", value = value.property)
            data.write(key = "type", value = EntityWithOptionalProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithOptionalPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithOptionalPropertyTemplate?, data: JSONObject): EntityWithOptionalPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithOptionalPropertyTemplate(
                property = JsonFieldParser.readOptionalFieldWithExpression(context, data, "property", TYPE_HELPER_STRING, allowOverride, parent?.property),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalPropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "property", field = value.property)
            data.write(key = "type", value = EntityWithOptionalProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithOptionalPropertyTemplate, EntityWithOptionalProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithOptionalPropertyTemplate, data: JSONObject): EntityWithOptionalProperty {
            return EntityWithOptionalProperty(
                property = JsonFieldResolver.resolveOptionalExpression(context, template.property, data, "property", TYPE_HELPER_STRING),
            )
        }
    }
}
