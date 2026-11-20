// Generated code. Do not modify.

package com.yandex.div.reference

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.json.JsonArray
import com.yandex.div.core.json.JsonObject
import com.yandex.div.core.type.Color
import com.yandex.div.core.type.Url
import com.yandex.div.model.entity.*
import com.yandex.div.model.expression.*
import com.yandex.div.model.entity.EntityTemplate as Template

public class EntityWithArrayOfNestedItems @InternalApi constructor (
    @JvmField public val items: List<Item>, // at least 1 elements
) {

    @InternalApi
    public fun copy(
        items: List<Item> = this.items,
    ): EntityWithArrayOfNestedItems = EntityWithArrayOfNestedItems(
        items = items,
    )

    public companion object {
        public const val TYPE: String = "entity_with_array_of_nested_items"
    }

    public class Item @InternalApi constructor (
        @JvmField public val entity: Entity,
        @JvmField public val property: Expression<String>,
    ) {

        @InternalApi
        public fun copy(
            entity: Entity = this.entity,
            property: Expression<String> = this.property,
        ): Item = Item(
            entity = entity,
            property = property,
        )
    }
}
