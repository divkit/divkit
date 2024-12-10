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

internal class EntityWithRequiredPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithRequiredProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithRequiredProperty {
            return EntityWithRequiredProperty(
                property = JsonExpressionParser.readExpression(context, data, "property", TYPE_HELPER_STRING, PROPERTY_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithRequiredProperty): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "property", value = value.property)
            data.write(key = "type", value = EntityWithRequiredProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithRequiredPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithRequiredPropertyTemplate?, data: JSONObject): EntityWithRequiredPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithRequiredPropertyTemplate(
                property = JsonFieldParser.readFieldWithExpression(context, data, "property", TYPE_HELPER_STRING, allowOverride, parent?.property, PROPERTY_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithRequiredPropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "property", field = value.property)
            data.write(key = "type", value = EntityWithRequiredProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithRequiredPropertyTemplate, EntityWithRequiredProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithRequiredPropertyTemplate, data: JSONObject): EntityWithRequiredProperty {
            return EntityWithRequiredProperty(
                property = JsonFieldResolver.resolveExpression(context, template.property, data, "property", TYPE_HELPER_STRING, PROPERTY_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
    }
}
