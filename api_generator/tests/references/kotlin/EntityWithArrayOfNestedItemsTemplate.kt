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

class EntityWithArrayOfNestedItemsTemplate(
    @JvmField val items: Field<List<ItemTemplate>>,
) : JSONSerializable, JsonTemplate<EntityWithArrayOfNestedItems> {

    constructor(
        env: ParsingEnvironment,
        parent: EntityWithArrayOfNestedItemsTemplate? = null,
        topLevel: Boolean = false,
        json: JSONObject
    ) : this(
        items = JsonTemplateParser.readListField(json, "items", topLevel, parent?.items, ItemTemplate.CREATOR, ITEMS_TEMPLATE_VALIDATOR, env.logger, env)
    )

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfNestedItems {
        return EntityWithArrayOfNestedItems(
            items = this.items.resolveTemplateList(env = env, key = "items", data = data, ITEMS_VALIDATOR, reader = ITEMS_READER)
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

    class ItemTemplate(
        @JvmField val entity: Field<EntityTemplate>,
        @JvmField val property: Field<Expression<String>>,
    ) : JSONSerializable, JsonTemplate<EntityWithArrayOfNestedItems.Item> {

        constructor(
            env: ParsingEnvironment,
            parent: ItemTemplate? = null,
            topLevel: Boolean = false,
            json: JSONObject
        ) : this(
            entity = JsonTemplateParser.readField(json, "entity", topLevel, parent?.entity, EntityTemplate.CREATOR, env.logger, env),
            property = JsonTemplateParser.readFieldWithExpression(json, "property", topLevel, parent?.property, env.logger, env, TYPE_HELPER_STRING)
        )

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfNestedItems.Item {
            return EntityWithArrayOfNestedItems.Item(
                entity = this.entity.resolveTemplate(env = env, key = "entity", data = data, reader = ENTITY_READER),
                property = this.property.resolve(env = env, key = "property", data = data, reader = PROPERTY_READER)
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
