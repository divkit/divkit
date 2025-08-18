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

internal class EntityWithEntityPropertyJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithEntityProperty> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithEntityProperty {
            return EntityWithEntityProperty(
                entity = JsonPropertyParser.readOptional(context, data, "entity", component.entityJsonEntityParser) ?: ENTITY_DEFAULT_VALUE,
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithEntityProperty): JSONObject {
            val data = JSONObject()
            JsonPropertyParser.write(context, data, "entity", value.entity, component.entityJsonEntityParser)
            JsonPropertyParser.write(context, data, "type", EntityWithEntityProperty.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithEntityPropertyTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithEntityPropertyTemplate?, data: JSONObject): EntityWithEntityPropertyTemplate {
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithEntityPropertyTemplate(
                entity = JsonFieldParser.readOptionalField(context, data, "entity", allowOverride, parent?.entity, component.entityJsonTemplateParser),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithEntityPropertyTemplate): JSONObject {
            val data = JSONObject()
            JsonFieldParser.writeField(context, data, "entity", value.entity, component.entityJsonTemplateParser)
            JsonPropertyParser.write(context, data, "type", EntityWithEntityProperty.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithEntityPropertyTemplate, EntityWithEntityProperty> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithEntityPropertyTemplate, data: JSONObject): EntityWithEntityProperty {
            return EntityWithEntityProperty(
                entity = JsonFieldResolver.resolveOptional(context, template.entity, data, "entity", component.entityJsonTemplateResolver, component.entityJsonEntityParser) ?: ENTITY_DEFAULT_VALUE,
            )
        }
    }

    private companion object {

        @JvmField val ENTITY_DEFAULT_VALUE = Entity.WithStringEnumProperty(EntityWithStringEnumProperty(property = Expression.constant(EntityWithStringEnumProperty.Property.SECOND)))
    }
}
