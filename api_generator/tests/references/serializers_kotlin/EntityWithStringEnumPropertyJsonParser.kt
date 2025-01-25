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
            val logger = context.logger
            return EntityWithStringEnumProperty(
                property = JsonExpressionParser.readExpression(context, logger, data, "property", TYPE_HELPER_PROPERTY, EntityWithStringEnumProperty.Property.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringEnumProperty): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "property", value = value.property, converter = EntityWithStringEnumProperty.Property.TO_STRING)
            data.write(key = "type", value = EntityWithStringEnumProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithStringEnumPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithStringEnumPropertyTemplate?, data: JSONObject): EntityWithStringEnumPropertyTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithStringEnumPropertyTemplate(
                property = JsonFieldParser.readFieldWithExpression(context, logger, data, "property", TYPE_HELPER_PROPERTY, allowOverride, parent?.property, EntityWithStringEnumProperty.Property.FROM_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithStringEnumPropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "property", field = value.property, converter = EntityWithStringEnumProperty.Property.TO_STRING)
            data.write(key = "type", value = EntityWithStringEnumProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithStringEnumPropertyTemplate, EntityWithStringEnumProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithStringEnumPropertyTemplate, data: JSONObject): EntityWithStringEnumProperty {
            val logger = context.logger
            return EntityWithStringEnumProperty(
                property = JsonFieldResolver.resolveExpression(context, logger, template.property, data, "property", TYPE_HELPER_PROPERTY, EntityWithStringEnumProperty.Property.FROM_STRING),
            )
        }
    }

    private companion object {

        @JvmField val TYPE_HELPER_PROPERTY = TypeHelper.from(default = EntityWithStringEnumProperty.Property.values().first()) { it is EntityWithStringEnumProperty.Property }
    }
}
