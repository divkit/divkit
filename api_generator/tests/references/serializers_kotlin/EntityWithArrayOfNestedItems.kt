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
        return builtInParserComponent.entityWithArrayOfNestedItemsJsonEntityParser
            .value
            .serialize(context = builtInParsingContext, value = this)
    }

    companion object {
        const val TYPE = "entity_with_array_of_nested_items"

        @JvmStatic
        @JvmName("fromJson")
        operator fun invoke(env: ParsingEnvironment, json: JSONObject): EntityWithArrayOfNestedItems {
            return builtInParserComponent.entityWithArrayOfNestedItemsJsonEntityParser
                .value
                .deserialize(context = env, data = json)
        }

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
            return builtInParserComponent.entityWithArrayOfNestedItemsItemJsonEntityParser
                .value
                .serialize(context = builtInParsingContext, value = this)
        }

        companion object {
            @JvmStatic
            @JvmName("fromJson")
            operator fun invoke(env: ParsingEnvironment, json: JSONObject): Item {
                return builtInParserComponent.entityWithArrayOfNestedItemsItemJsonEntityParser
                    .value
                    .deserialize(context = env, data = json)
            }

            val CREATOR = { env: ParsingEnvironment, it: JSONObject -> Item(env, json = it) }
        }
    }
}
