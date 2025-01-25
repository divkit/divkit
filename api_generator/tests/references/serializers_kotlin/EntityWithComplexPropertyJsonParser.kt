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

internal class EntityWithComplexPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithComplexProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithComplexProperty {
            val logger = context.logger
            return EntityWithComplexProperty(
                property = JsonPropertyParser.read(context, logger, data, "property", component.entityWithComplexPropertyPropertyJsonEntityParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexProperty): JSONObject {
            val data = JSONObject()
            data.write(key = "property", value = component.entityWithComplexPropertyPropertyJsonEntityParser.value.serialize(context, value.property))
            data.write(key = "type", value = EntityWithComplexProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithComplexPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithComplexPropertyTemplate?, data: JSONObject): EntityWithComplexPropertyTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithComplexPropertyTemplate(
                property = JsonFieldParser.readField(context, logger, data, "property", allowOverride, parent?.property, component.entityWithComplexPropertyPropertyJsonTemplateParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "property", field = value.property, converter = component.entityWithComplexPropertyPropertyJsonTemplateParser.value.asConverter(context))
            data.write(key = "type", value = EntityWithComplexProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithComplexPropertyTemplate, EntityWithComplexProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithComplexPropertyTemplate, data: JSONObject): EntityWithComplexProperty {
            val logger = context.logger
            return EntityWithComplexProperty(
                property = JsonFieldResolver.resolve(context, logger, template.property, data, "property", component.entityWithComplexPropertyPropertyJsonTemplateResolver, component.entityWithComplexPropertyPropertyJsonEntityParser),
            )
        }
    }
}

internal class EntityWithComplexPropertyPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithComplexProperty.Property> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithComplexProperty.Property {
            val logger = context.logger
            return EntityWithComplexProperty.Property(
                value = JsonExpressionParser.readExpression(context, logger, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexProperty.Property): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "value", value = value.value, converter = URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithComplexPropertyTemplate.PropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithComplexPropertyTemplate.PropertyTemplate?, data: JSONObject): EntityWithComplexPropertyTemplate.PropertyTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithComplexPropertyTemplate.PropertyTemplate(
                value = JsonFieldParser.readFieldWithExpression(context, logger, data, "value", TYPE_HELPER_URI, allowOverride, parent?.value, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyTemplate.PropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "value", field = value.value, converter = URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithComplexPropertyTemplate.PropertyTemplate, EntityWithComplexProperty.Property> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithComplexPropertyTemplate.PropertyTemplate, data: JSONObject): EntityWithComplexProperty.Property {
            val logger = context.logger
            return EntityWithComplexProperty.Property(
                value = JsonFieldResolver.resolveExpression(context, logger, template.value, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }
    }
}
