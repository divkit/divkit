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

class EntityWithArrayOfNestedItems(
    @JvmField val items: List<Item>, // at least 1 elements
) : JSONSerializable, Hashable {

    private var _propertiesHash: Int? = null 
    private var _hash: Int? = null 

    override fun propertiesHash(): Int {
        _propertiesHash?.let {
            return it
        }
        val propertiesHash = this::class.hashCode()
        _propertiesHash = propertiesHash
        return propertiesHash
    }

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            propertiesHash() +
            items.sumOf { it.hash() }
        _hash = hash
        return hash
    }

    fun equals(other: EntityWithArrayOfNestedItems?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
        other ?: return false
        return items.compareWith(other.items) { a, b -> a.equals(b, resolver, otherResolver) }
    }

    fun copy(
        items: List<Item> = this.items,
    ) = EntityWithArrayOfNestedItems(
        items = items,
    )

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "items", value = items)
        json.write(key = "type", value = TYPE)
        return json
    }

    companion object {
        const val TYPE = "entity_with_array_of_nested_items"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayOfNestedItems {
            val logger = env.logger
            return EntityWithArrayOfNestedItems(
                items = JsonParser.readList(json, "items", Item.CREATOR, ITEMS_VALIDATOR, logger, env)
            )
        }

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfNestedItems.Item> { it: List<*> -> it.size >= 1 }

        val CREATOR = { env: ParsingEnvironment, it: JSONObject -> EntityWithArrayOfNestedItems(env, json = it) }
    }

    class Item(
        @JvmField val entity: Entity,
        @JvmField val property: Expression<String>,
    ) : JSONSerializable, Hashable {

        private var _hash: Int? = null 

        override fun hash(): Int {
            _hash?.let {
                return it
            }
            val hash = 
                this::class.hashCode() +
                entity.hash() +
                property.hashCode()
            _hash = hash
            return hash
        }

        fun equals(other: Item?, resolver: ExpressionResolver, otherResolver: ExpressionResolver): Boolean {
            other ?: return false
            return entity.equals(other.entity, resolver, otherResolver) &&
                property.evaluate(resolver) == other.property.evaluate(otherResolver)
        }

        fun copy(
            entity: Entity = this.entity,
            property: Expression<String> = this.property,
        ) = Item(
            entity = entity,
            property = property,
        )

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.write(key = "entity", value = entity)
            json.writeExpression(key = "property", value = property)
            return json
        }

        companion object {
            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Item {
                val logger = env.logger
                return Item(
                    entity = JsonParser.read(json, "entity", Entity.CREATOR, logger, env),
                    property = JsonParser.readExpression(json, "property", logger, env, TYPE_HELPER_STRING)
                )
            }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Item(env, json = it) }
        }
    }
}
