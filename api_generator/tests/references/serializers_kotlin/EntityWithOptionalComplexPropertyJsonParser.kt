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
            return EntityWithOptionalComplexProperty(
                property = JsonPropertyParser.readOptional(context, data, "property", component.entityWithOptionalComplexPropertyPropertyJsonEntityParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexProperty): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "property", value.property, component.entityWithOptionalComplexPropertyPropertyJsonEntityParser)
            JsonPropertyParser.write(context, data, "type", EntityWithOptionalComplexProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithOptionalComplexPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithOptionalComplexPropertyTemplate?, data: JSONObject): EntityWithOptionalComplexPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithOptionalComplexPropertyTemplate(
                property = JsonFieldParser.readOptionalField(context, data, "property", allowOverride, parent?.property, component.entityWithOptionalComplexPropertyPropertyJsonTemplateParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexPropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeField(context, data, "property", value.property, component.entityWithOptionalComplexPropertyPropertyJsonTemplateParser)
            JsonPropertyParser.write(context, data, "type", EntityWithOptionalComplexProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithOptionalComplexPropertyTemplate, EntityWithOptionalComplexProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithOptionalComplexPropertyTemplate, data: JSONObject): EntityWithOptionalComplexProperty {
            return EntityWithOptionalComplexProperty(
                property = JsonFieldResolver.resolveOptional(context, template.property, data, "property", component.entityWithOptionalComplexPropertyPropertyJsonTemplateResolver, component.entityWithOptionalComplexPropertyPropertyJsonEntityParser),
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
            return EntityWithOptionalComplexProperty.Property(
                value = JsonExpressionParser.readExpression(context, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexProperty.Property): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "value", value.value, URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithOptionalComplexPropertyTemplate.PropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate?, data: JSONObject): EntityWithOptionalComplexPropertyTemplate.PropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithOptionalComplexPropertyTemplate.PropertyTemplate(
                value = JsonFieldParser.readFieldWithExpression(context, data, "value", TYPE_HELPER_URI, allowOverride, parent?.value, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "value", value.value, URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithOptionalComplexPropertyTemplate.PropertyTemplate, EntityWithOptionalComplexProperty.Property> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithOptionalComplexPropertyTemplate.PropertyTemplate, data: JSONObject): EntityWithOptionalComplexProperty.Property {
            return EntityWithOptionalComplexProperty.Property(
                value = JsonFieldResolver.resolveExpression(context, template.value, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }
    }
}
