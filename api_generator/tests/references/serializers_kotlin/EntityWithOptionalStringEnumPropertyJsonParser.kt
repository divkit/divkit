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

internal class EntityWithOptionalStringEnumPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithOptionalStringEnumProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithOptionalStringEnumProperty {
            return EntityWithOptionalStringEnumProperty(
                property = JsonExpressionParser.readOptionalExpression(context, data, "property", TYPE_HELPER_PROPERTY, EntityWithOptionalStringEnumProperty.Property.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalStringEnumProperty): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "property", value.property, EntityWithOptionalStringEnumProperty.Property.TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithOptionalStringEnumProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithOptionalStringEnumPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithOptionalStringEnumPropertyTemplate?, data: JSONObject): EntityWithOptionalStringEnumPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithOptionalStringEnumPropertyTemplate(
                property = JsonFieldParser.readOptionalFieldWithExpression(context, data, "property", TYPE_HELPER_PROPERTY, allowOverride, parent?.property, EntityWithOptionalStringEnumProperty.Property.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalStringEnumPropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "property", value.property, EntityWithOptionalStringEnumProperty.Property.TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithOptionalStringEnumProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithOptionalStringEnumPropertyTemplate, EntityWithOptionalStringEnumProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithOptionalStringEnumPropertyTemplate, data: JSONObject): EntityWithOptionalStringEnumProperty {
            return EntityWithOptionalStringEnumProperty(
                property = JsonFieldResolver.resolveOptionalExpression(context, template.property, data, "property", TYPE_HELPER_PROPERTY, EntityWithOptionalStringEnumProperty.Property.FROM_STRING),
            )
        }
    }

    private companion object {

        @JvmField val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithOptionalStringEnumProperty.Property.values().first()) { it is EntityWithOptionalStringEnumProperty.Property }
    }
}
