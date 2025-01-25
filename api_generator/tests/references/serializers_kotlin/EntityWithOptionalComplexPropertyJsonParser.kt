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

internal class EntityWithOptionalComplexPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithOptionalComplexProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithOptionalComplexProperty {
            val logger = context.logger
            return EntityWithOptionalComplexProperty(
                property = JsonPropertyParser.readOptional(context, logger, data, "property", component.entityWithOptionalComplexPropertyPropertyJsonEntityParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexProperty): JSONObject {
            val data = JSONObject()
            data.write(key = "property", value = component.entityWithOptionalComplexPropertyPropertyJsonEntityParser.value.serialize(context, value.property))
            data.write(key = "type", value = EntityWithOptionalComplexProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithOptionalComplexPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithOptionalComplexPropertyTemplate?, data: JSONObject): EntityWithOptionalComplexPropertyTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithOptionalComplexPropertyTemplate(
                property = JsonFieldParser.readOptionalField(context, logger, data, "property", allowOverride, parent?.property, component.entityWithOptionalComplexPropertyPropertyJsonTemplateParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexPropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "property", field = value.property, converter = component.entityWithOptionalComplexPropertyPropertyJsonTemplateParser.value.asConverter(context))
            data.write(key = "type", value = EntityWithOptionalComplexProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithOptionalComplexPropertyTemplate, EntityWithOptionalComplexProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithOptionalComplexPropertyTemplate, data: JSONObject): EntityWithOptionalComplexProperty {
            val logger = context.logger
            return EntityWithOptionalComplexProperty(
                property = JsonFieldResolver.resolveOptional(context, logger, template.property, data, "property", component.entityWithOptionalComplexPropertyPropertyJsonTemplateResolver, component.entityWithOptionalComplexPropertyPropertyJsonEntityParser),
            )
        }
    }
}

internal class EntityWithOptionalComplexPropertyPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithOptionalComplexProperty.Property> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithOptionalComplexProperty.Property {
            val logger = context.logger
            return EntityWithOptionalComplexProperty.Property(
                value = JsonExpressionParser.readExpression(context, logger, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexProperty.Property): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "value", value = value.value, converter = URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithOptionalComplexPropertyTemplate.PropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate?, data: JSONObject): EntityWithOptionalComplexPropertyTemplate.PropertyTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithOptionalComplexPropertyTemplate.PropertyTemplate(
                value = JsonFieldParser.readFieldWithExpression(context, logger, data, "value", TYPE_HELPER_URI, allowOverride, parent?.value, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "value", field = value.value, converter = URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithOptionalComplexPropertyTemplate.PropertyTemplate, EntityWithOptionalComplexProperty.Property> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate, data: JSONObject): EntityWithOptionalComplexProperty.Property {
            val logger = context.logger
            return EntityWithOptionalComplexProperty.Property(
                value = JsonFieldResolver.resolveExpression(context, logger, template.value, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }
    }
}
