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

internal class EntityWithArrayOfNestedItemsJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithArrayOfNestedItems> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithArrayOfNestedItems {
            val logger = context.logger
            return EntityWithArrayOfNestedItems(
                items = JsonPropertyParser.readList(context, logger, data, "items", component.entityWithArrayOfNestedItemsItemJsonEntityParser, ITEMS_VALIDATOR),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfNestedItems): JSONObject {
            val data = JSONObject()
            data.write(key = "items", value = component.entityWithArrayOfNestedItemsItemJsonEntityParser.value.serialize(context, value.items))
            data.write(key = "type", value = EntityWithArrayOfNestedItems.TYPE)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayOfNestedItemsTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayOfNestedItemsTemplate?, data: JSONObject): EntityWithArrayOfNestedItemsTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayOfNestedItemsTemplate(
                items = JsonFieldParser.readListField(context, logger, data, "items", allowOverride, parent?.items, component.entityWithArrayOfNestedItemsItemJsonTemplateParser, ITEMS_VALIDATOR.cast()),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfNestedItemsTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "items", field = value.items, converter = component.entityWithArrayOfNestedItemsItemJsonTemplateParser.value.asConverter(context))
            data.write(key = "type", value = EntityWithArrayOfNestedItems.TYPE)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayOfNestedItemsTemplate, EntityWithArrayOfNestedItems> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayOfNestedItemsTemplate, data: JSONObject): EntityWithArrayOfNestedItems {
            val logger = context.logger
            return EntityWithArrayOfNestedItems(
                items = JsonFieldResolver.resolveList(context, logger, template.items, data, "items", component.entityWithArrayOfNestedItemsItemJsonTemplateResolver, component.entityWithArrayOfNestedItemsItemJsonEntityParser, ITEMS_VALIDATOR),
            )
        }
    }

    private companion object {

        @JvmField val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfNestedItems.Item> { it: List<*> -> it.size >= 1 }
    }
}

internal class EntityWithArrayOfNestedItemsItemJsonParser(
    private val component: JsonParserComponent
) {

    class EntityParserImpl(
        private val component: JsonParserComponent
    ) : Parser<JSONObject, EntityWithArrayOfNestedItems.Item> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, data: JSONObject): EntityWithArrayOfNestedItems.Item {
            val logger = context.logger
            return EntityWithArrayOfNestedItems.Item(
                entity = JsonPropertyParser.read(context, logger, data, "entity", component.entityJsonEntityParser),
                property = JsonExpressionParser.readExpression(context, logger, data, "property", TYPE_HELPER_STRING),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfNestedItems.Item): JSONObject {
            val data = JSONObject()
            data.write(key = "entity", value = component.entityJsonEntityParser.value.serialize(context, value.entity))
            data.writeExpression(key = "property", value = value.property)
            return data
        }
    }

    class TemplateParserImpl(
        private val component: JsonParserComponent
    ) : TemplateParser<JSONObject, EntityWithArrayOfNestedItemsTemplate.ItemTemplate> {

        @Throws(ParsingException::class)
        override fun deserialize(context: ParsingContext, parent: EntityWithArrayOfNestedItemsTemplate.ItemTemplate?, data: JSONObject): EntityWithArrayOfNestedItemsTemplate.ItemTemplate {
            val logger = context.logger
            val allowOverride = context.allowPropertyOverride
            @Suppress("NAME_SHADOWING") val context = context.restrictPropertyOverride()
            return EntityWithArrayOfNestedItemsTemplate.ItemTemplate(
                entity = JsonFieldParser.readField(context, logger, data, "entity", allowOverride, parent?.entity, component.entityJsonTemplateParser),
                property = JsonFieldParser.readFieldWithExpression(context, logger, data, "property", TYPE_HELPER_STRING, allowOverride, parent?.property),
            )
        }

        @Throws(ParsingException::class)
        override fun serialize(context: ParsingContext, value: EntityWithArrayOfNestedItemsTemplate.ItemTemplate): JSONObject {
            val data = JSONObject()
            data.writeField(key = "entity", field = value.entity, converter = component.entityJsonTemplateParser.value.asConverter(context))
            data.writeFieldWithExpression(key = "property", field = value.property)
          return data
        }
    }

    class TemplateResolverImpl(
        private val component: JsonParserComponent
    ) : TemplateResolver<JSONObject, EntityWithArrayOfNestedItemsTemplate.ItemTemplate, EntityWithArrayOfNestedItems.Item> {

        @Throws(ParsingException::class)
        override fun resolve(context: ParsingContext, template: EntityWithArrayOfNestedItemsTemplate.ItemTemplate, data: JSONObject): EntityWithArrayOfNestedItems.Item {
            val logger = context.logger
            return EntityWithArrayOfNestedItems.Item(
                entity = JsonFieldResolver.resolve(context, logger, template.entity, data, "entity", component.entityJsonTemplateResolver, component.entityJsonEntityParser),
                property = JsonFieldResolver.resolveExpression(context, logger, template.property, data, "property", TYPE_HELPER_STRING),
            )
        }
    }
}
