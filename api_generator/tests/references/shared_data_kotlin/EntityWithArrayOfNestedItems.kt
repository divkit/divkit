// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayOfNestedItems(
    @JvmField final val items: List<Item>, // at least 1 elements
) : Hashable {

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

    fun copy(
        items: List<Item> = this.items,
    ) = EntityWithArrayOfNestedItems(
        items = items,
    )

    companion object {
        const val TYPE = "entity_with_array_of_nested_items"

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfNestedItems.Item> { it: List<*> -> it.size >= 1 }
    }


    class Item(
        @JvmField final val entity: Entity,
        @JvmField final val property: Expression<String>,
    ) : Hashable {

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

        fun copy(
            entity: Entity = this.entity,
            property: Expression<String> = this.property,
        ) = Item(
            entity = entity,
            property = property,
        )
    }
}
