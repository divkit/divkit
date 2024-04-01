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
class EntityWithArrayOfNestedItems(
    @JvmField final val items: List<Item>, // at least 1 elements
) : JSONSerializable, Hashable {

    private var _propertiesHash: Int? = null 
    private var _hash: Int? = null 

    override fun propertiesHash(): Int {
        _propertiesHash?.let {
            return it
        }
        val propertiesHash = javaClass.hashCode()
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

    override fun writeToJSON(): JSONObject {
        val json = JSONObject()
        json.write(key = "items", value = items)
        json.write(key = "type", value = TYPE)
        return json
    }

    fun copy(
        items: List<Item> = this.items,
    ) = EntityWithArrayOfNestedItems(
        items = items,
    )

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


    @Mockable
    class Item(
        @JvmField final val entity: Entity,
        @JvmField final val property: Expression<String>,
    ) : JSONSerializable, Hashable {

        private var _hash: Int? = null 

        override fun hash(): Int {
            _hash?.let {
                return it
            }
            val hash = 
                entity.hash() +
                property.hashCode()
            _hash = hash
            return hash
        }

        override fun writeToJSON(): JSONObject {
            val json = JSONObject()
            json.write(key = "entity", value = entity)
            json.writeExpression(key = "property", value = property)
            return json
        }

        fun copy(
            entity: Entity = this.entity,
            property: Expression<String> = this.property,
        ) = Item(
            entity = entity,
            property = property,
        )

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
