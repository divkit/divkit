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
        items = Field.nullField(false),
    ) {
        throw UnsupportedOperationException("Do not use this constructor directly.")
    }

    override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfNestedItems {
        return builtInParserComponent.entityWithArrayOfNestedItemsJsonTemplateResolver
            .value
            .resolve(context = env, template = this, data = data)
    }

    override fun writeToJSON(): JSONObject {
        return builtInParserComponent.entityWithArrayOfNestedItemsJsonTemplateParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array_of_nested_items"

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
            entity = Field.nullField(false),
            property = Field.nullField(false),
        ) {
            throw UnsupportedOperationException("Do not use this constructor directly.")
        }

        override fun resolve(env: ParsingEnvironment, data: JSONObject): EntityWithArrayOfNestedItems.Item {
            return builtInParserComponent.entityWithArrayOfNestedItemsItemJsonTemplateResolver
                .value
                .resolve(context = env, template = this, data = data)
        }

        override fun writeToJSON(): JSONObject {
            return builtInParserComponent.entityWithArrayOfNestedItemsItemJsonTemplateParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> ItemTemplate(env, json = it) }
        }
    }
}
