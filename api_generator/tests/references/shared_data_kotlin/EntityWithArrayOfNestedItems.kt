// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayOfNestedItems(
    @JvmField final val items: List<Item>, // at least 1 elements
) {

    fun copyWithNewProperties(
        items: List<EntityWithArrayOfNestedItems.Item>,
    ) = EntityWithArrayOfNestedItems(
        items,
    )

    companion object {
        const val TYPE = "entity_with_array_of_nested_items"

        private val ITEMS_VALIDATOR = ListValidator<EntityWithArrayOfNestedItems.Item> { it: List<*> -> it.size >= 1 }
    }


    class Item(
        @JvmField final val entity: Entity,
        @JvmField final val property: Expression<String>,
    ) {
    }
}
