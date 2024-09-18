// Generated code. Do not modify.

package com.yandex.div2

import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.yandex.div.json.*
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionsList
import com.yandex.div.json.schema.*
import com.yandex.div.core.annotations.Mockable
import java.io.IOException
import java.util.BitSet
import org.json.JSONObject
import com.yandex.div.data.*
import org.json.JSONArray

@Mockable
class EntityWithArrayOfNestedItemsTemplate : JSONSerializable, JsonTemplate<EntityWithArrayOfNestedItems> {
    @JvmField final val items: Field<List<ItemTemplate>> // at least 1 elements

    constructor (
        env: ParsingEnvironment,
        parent: EntityWithArrayOfNestedItemsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) {
        val logger = env.logger
        items = JsonTemplateParser.readListField(json, "items", topLevel, parent?.items, ItemTemplate.CREATOR, ITEMS_TEMPLATE_VALIDATOR, logger, env)
    }

    override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithArrayOfNestedItems {
        return EntityWithArrayOfNestedItems(
            items = items.resolveTemplateList(env = env, key = "items", data = rawData, ITEMS_VALIDATOR, reader = ITEMS_READER)
        )
    }

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.writeField(key = "items", field = items)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_of_nested_items"

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfNestedItems.Item> { it: List<*> -> it.size >= 1 }
        private val ITEMS_TEMPLATE_VALIDATOR = ListValidator<EntityWithArrayOfNestedItemsTemplate.ItemTemplate> { it: List<*> -> it.size >= 1 }

        val ITEMS_READER: Reader<List<EntityWithArrayOfNestedItems.Item>> = { key, json, env -> JsonParser.readList(json, key, EntityWithArrayOfNestedItems.Item.CREATOR, ITEMS_VALIDATOR, env.logger, env) }
        val TYPE_READER: Reader<String> = { key, json, env -> JsonParser.read(json, key, env.logger, env) }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfNestedItemsTemplate(env, json = it) }
    }

    @Mockable
    class ItemTemplate : JSONSerializable, JsonTemplate<EntityWithArrayOfNestedItems.Item> {
        @JvmField final val entity: Field<EntityTemplate>
        @JvmField final val property: Field<Expression<String>>

        constructor (
            env: ParsingEnvironment,
            parent: ItemTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) {
            val logger = env.logger
            entity = JsonTemplateParser.readField(json, "entity", topLevel, parent?.entity, EntityTemplate.CREATOR, logger, env)
            property = JsonTemplateParser.readFieldWithExpression(json, "property", topLevel, parent?.property, logger, env, TYPE_HELPER_STRING)
        }

        override fun resolve(env: ParsingEnvironment, rawData: JSONObject): EntityWithArrayOfNestedItems.Item {
            return EntityWithArrayOfNestedItems.Item(
                entity = entity.resolveTemplate(env = env, key = "entity", data = rawData, reader = ENTITY_READER),
                property = property.resolve(env = env, key = "property", data = rawData, reader = PROPERTY_READER)
            )
        }

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.writeField(key = "entity", field = entity)
            json.writeFieldWithExpression(key = "property", field = property)
            return json
        }

        companion object {
            val ENTITY_READER: Reader<Entity> = { key, json, env -> JsonParser.read(json, key, Entity.CREATOR, env.logger, env) }
            val PROPERTY_READER: Reader<Expression<String>> = { key, json, env -> JsonParser.readExpression(json, key, env.logger, env, TYPE_HELPER_STRING) }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> ItemTemplate(env, json = it) }
        }
    }
}
