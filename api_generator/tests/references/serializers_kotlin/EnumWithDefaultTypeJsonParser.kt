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

internal class EnumWithDefaultTypeJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EnumWithDefaultType> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EnumWithDefaultType {
            val type: String = JsonPropertyParser.readString(context, data, "type")
            when (type) {
                WithDefault.TYPE -> return EnumWithDefaultType.WithDefaultCase(component.withDefaultJsonEntityParser.value.deserialize(context, data))
                WithoutDefault.TYPE -> return EnumWithDefaultType.WithoutDefaultCase(component.withoutDefaultJsonEntityParser.value.deserialize(context, data))
            }

            val template = context.templates.getOrThrow(type, data) as? EnumWithDefaultTypeTemplate
            if (template != null) {
                return component.enumWithDefaultTypeJsonTemplateResolver
                    .value
                    .resolve(context, template, data)
            } else {
                throw typeMismatch(json = data, key = "type", value = type)
            }
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EnumWithDefaultType): JSONObject {
            return when (value) {
                is EnumWithDefaultType.WithDefaultCase -> component.withDefaultJsonEntityParser.value.serialize(context, value.value)
                is EnumWithDefaultType.WithoutDefaultCase -> component.withoutDefaultJsonEntityParser.value.serialize(context, value.value)
            }
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EnumWithDefaultTypeTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EnumWithDefaultTypeTemplate {
            val extendedType = JsonPropertyParser.readString(context, data, "type")
            val parent = context.templates[extendedType] as? EnumWithDefaultTypeTemplate
            val type = parent?.type ?: extendedType
            when (type) {
                WithDefaultTemplate.TYPE -> return EnumWithDefaultTypeTemplate.WithDefaultCase(component.withDefaultJsonTemplateParser.value.deserialize(context, parent?.value() as WithDefaultTemplate?, data))
                WithoutDefaultTemplate.TYPE -> return EnumWithDefaultTypeTemplate.WithoutDefaultCase(component.withoutDefaultJsonTemplateParser.value.deserialize(context, parent?.value() as WithoutDefaultTemplate?, data))
                else -> throw typeMismatch(json = data, key = "type", value = type)
            }
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EnumWithDefaultTypeTemplate): JSONObject {
            return when (value) {
                is EnumWithDefaultTypeTemplate.WithDefaultCase -> component.withDefaultJsonTemplateParser.value.serialize(context, value.value)
                is EnumWithDefaultTypeTemplate.WithoutDefaultCase -> component.withoutDefaultJsonTemplateParser.value.serialize(context, value.value)
            }
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EnumWithDefaultTypeTemplate, EnumWithDefaultType> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EnumWithDefaultTypeTemplate, data: JSONObject): EnumWithDefaultType {
            return when (template) {
                is EnumWithDefaultTypeTemplate.WithDefaultCase -> EnumWithDefaultType.WithDefaultCase(component.withDefaultJsonTemplateResolver.value.resolve(context, template.value, data))
                is EnumWithDefaultTypeTemplate.WithoutDefaultCase -> EnumWithDefaultType.WithoutDefaultCase(component.withoutDefaultJsonTemplateResolver.value.resolve(context, template.value, data))
            }
        }
    }
}
