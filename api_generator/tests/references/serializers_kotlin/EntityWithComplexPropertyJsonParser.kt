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
            return EntityWithComplexProperty(
                property = JsonPropertyParser.read(context, data, "property", component.entityWithComplexPropertyPropertyJsonEntityParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexProperty): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "property", value.property, component.entityWithComplexPropertyPropertyJsonEntityParser)
            JsonPropertyParser.write(context, data, "type", EntityWithComplexProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithComplexPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithComplexPropertyTemplate?, data: JSONObject): EntityWithComplexPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithComplexPropertyTemplate(
                property = JsonFieldParser.readField(context, data, "property", allowOverride, parent?.property, component.entityWithComplexPropertyPropertyJsonTemplateParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeField(context, data, "property", value.property, component.entityWithComplexPropertyPropertyJsonTemplateParser)
            JsonPropertyParser.write(context, data, "type", EntityWithComplexProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithComplexPropertyTemplate, EntityWithComplexProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithComplexPropertyTemplate, data: JSONObject): EntityWithComplexProperty {
            return EntityWithComplexProperty(
                property = JsonFieldResolver.resolve(context, template.property, data, "property", component.entityWithComplexPropertyPropertyJsonTemplateResolver, component.entityWithComplexPropertyPropertyJsonEntityParser),
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
            return EntityWithComplexProperty.Property(
                value = JsonExpressionParser.readExpression(context, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexProperty.Property): JSONObject {
            val data = JSONObject()
            JsonExpressionParser.writeExpression(context, data, "value", value.value, URI_TO_STRING)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithComplexPropertyTemplate.PropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithComplexPropertyTemplate.PropertyTemplate?, data: JSONObject): EntityWithComplexPropertyTemplate.PropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithComplexPropertyTemplate.PropertyTemplate(
                value = JsonFieldParser.readFieldWithExpression(context, data, "value", TYPE_HELPER_URI, allowOverride, parent?.value, ANY_TO_URI),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyTemplate.PropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeExpressionField(context, data, "value", value.value, URI_TO_STRING)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithComplexPropertyTemplate.PropertyTemplate, EntityWithComplexProperty.Property> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithComplexPropertyTemplate.PropertyTemplate, data: JSONObject): EntityWithComplexProperty.Property {
            return EntityWithComplexProperty.Property(
                value = JsonFieldResolver.resolveExpression(context, template.value, data, "value", TYPE_HELPER_URI, ANY_TO_URI),
            )
        }
    }
}
