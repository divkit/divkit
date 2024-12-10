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

internal class EntityWithComplexPropertyWithDefaultValueJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithComplexPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithComplexPropertyWithDefaultValue {
            return EntityWithComplexPropertyWithDefaultValue(
                property = JsonPropertyParser.readOptional(context, data, "property", component.entityWithComplexPropertyWithDefaultValuePropertyJsonEntityParser) ?: PROPERTY_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyWithDefaultValue): JSONObject {
            val data = JSONObject()
            data.write(key = "property", value = component.entityWithComplexPropertyWithDefaultValuePropertyJsonEntityParser.value.serialize(context, value.property))
            data.write(key = "type", value = EntityWithComplexPropertyWithDefaultValue.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithComplexPropertyWithDefaultValueTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithComplexPropertyWithDefaultValueTemplate?, data: JSONObject): EntityWithComplexPropertyWithDefaultValueTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithComplexPropertyWithDefaultValueTemplate(
                property = JsonFieldParser.readOptionalField(context, data, "property", allowOverride, parent?.property, component.entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyWithDefaultValueTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "property", field = value.property, converter = component.entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateParser.value.asConverter(context))
            data.write(key = "type", value = EntityWithComplexPropertyWithDefaultValue.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithComplexPropertyWithDefaultValueTemplate, EntityWithComplexPropertyWithDefaultValue> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithComplexPropertyWithDefaultValueTemplate, data: JSONObject): EntityWithComplexPropertyWithDefaultValue {
            return EntityWithComplexPropertyWithDefaultValue(
                property = JsonFieldResolver.resolveOptional(context, template.property, data, "property", component.entityWithComplexPropertyWithDefaultValuePropertyJsonTemplateResolver, component.entityWithComplexPropertyWithDefaultValuePropertyJsonEntityParser) ?: PROPERTY_DEFAULT_VALUE,
            )
        }
    }

    private companion object {

        @JvmField val PROPERTY_DEFAULT_VALUE = EntityWithComplexPropertyWithDefaultValue.Property(value = Expression.constant("Default text"))
    }
}

internal class EntityWithComplexPropertyWithDefaultValuePropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithComplexPropertyWithDefaultValue.Property> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithComplexPropertyWithDefaultValue.Property {
            return EntityWithComplexPropertyWithDefaultValue.Property(
                value = JsonExpressionParser.readExpression(context, data, "value", TYPE_HELPER_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyWithDefaultValue.Property): JSONObject {
            val data = JSONObject()
            data.writeExpression(key = "value", value = value.value)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate?, data: JSONObject): EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate(
                value = JsonFieldParser.readFieldWithExpression(context, data, "value", TYPE_HELPER_STRING, allowOverride, parent?.value),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate): JSONObject {
            val data = JSONObject()
            data.writeFieldWithExpression(key = "value", field = value.value)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate, EntityWithComplexPropertyWithDefaultValue.Property> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithComplexPropertyWithDefaultValueTemplate.PropertyTemplate, data: JSONObject): EntityWithComplexPropertyWithDefaultValue.Property {
            return EntityWithComplexPropertyWithDefaultValue.Property(
                value = JsonFieldResolver.resolveExpression(context, template.value, data, "value", TYPE_HELPER_STRING),
            )
        }
    }
}
