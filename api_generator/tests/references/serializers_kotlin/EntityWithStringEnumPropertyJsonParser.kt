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

internal class EntityWithStringEnumPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithStringEnumProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithStringEnumProperty {
            return EntityWithStringEnumProperty(
                property = JsonExpressionParser.readExpression(context, data, "property", TYPE_HELPER_PROPERTY, EntityWithStringEnumProperty.Property.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringEnumProperty): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "property", value.property, EntityWithStringEnumProperty.Property.TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithStringEnumProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithStringEnumPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithStringEnumPropertyTemplate?, data: JSONObject): EntityWithStringEnumPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithStringEnumPropertyTemplate(
                property = JsonFieldParser.readFieldWithExpression(context, data, "property", TYPE_HELPER_PROPERTY, allowOverride, parent?.property, EntityWithStringEnumProperty.Property.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringEnumPropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "property", value.property, EntityWithStringEnumProperty.Property.TO_STRING)
            JsonPropertyParser.write(context, data, "type", EntityWithStringEnumProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithStringEnumPropertyTemplate, EntityWithStringEnumProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithStringEnumPropertyTemplate, data: JSONObject): EntityWithStringEnumProperty {
            return EntityWithStringEnumProperty(
                property = JsonFieldResolver.resolveExpression(context, template.property, data, "property", TYPE_HELPER_PROPERTY, EntityWithStringEnumProperty.Property.FROM_STRING),
            )
        }
    }

    private companion object {

        @JvmField val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithStringEnumProperty.Property.values().first()) { it is EntityWithStringEnumProperty.Property }
    }
}
