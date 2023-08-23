// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithArrayOfNestedItems(
    @JvmField final val items: List<Item>, // at least 1 elements
) {

    fun copyWithNewArray(
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
        @JvmField final val property: Expression<String>, // at least 1 char
    ) {

        companion object {
            private val PROPERTY_TEMPLATE_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
            private val PROPERTY_VALIDATOR = ValueValidator<String> { it: String -> it.length >= 1 }
        }

    }
}
