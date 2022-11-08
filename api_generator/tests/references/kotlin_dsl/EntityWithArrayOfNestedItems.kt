// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithArrayOfNestedItems internal constructor(
    @JsonIgnore val items: Property<List<Item>>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_array_of_nested_items"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "items" to items,
        )
    }

    class Item internal constructor(
        @JsonIgnore val entity: Property<Entity>?,
        @JsonIgnore val property: Property<String>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "entity" to entity,
                "property" to property,
            )
        }
    }
}

fun <T> TemplateContext<T>.entityWithArrayOfNestedItems(): LiteralProperty<EntityWithArrayOfNestedItems> {
    return value(EntityWithArrayOfNestedItems(
        items = null,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayOfNestedItems(
    items: Property<List<EntityWithArrayOfNestedItems.Item>>? = null,
): LiteralProperty<EntityWithArrayOfNestedItems> {
    return value(EntityWithArrayOfNestedItems(
        items = items,
    ))
}

fun <T> TemplateContext<T>.entityWithArrayOfNestedItems(
    items: List<EntityWithArrayOfNestedItems.Item>? = null,
): LiteralProperty<EntityWithArrayOfNestedItems> {
    return value(EntityWithArrayOfNestedItems(
        items = optionalValue(items),
    ))
}

fun <T> TemplateContext<T>.item(): LiteralProperty<EntityWithArrayOfNestedItems.Item> {
    return value(EntityWithArrayOfNestedItems.Item(
        entity = null,
        property = null,
    ))
}

fun <T> TemplateContext<T>.item(
    entity: Property<Entity>? = null,
    property: Property<String>? = null,
): LiteralProperty<EntityWithArrayOfNestedItems.Item> {
    return value(EntityWithArrayOfNestedItems.Item(
        entity = entity,
        property = property,
    ))
}

fun <T> TemplateContext<T>.item(
    entity: Entity? = null,
    property: String? = null,
): LiteralProperty<EntityWithArrayOfNestedItems.Item> {
    return value(EntityWithArrayOfNestedItems.Item(
        entity = optionalValue(entity),
        property = optionalValue(property),
    ))
}

fun CardContext.entityWithArrayOfNestedItems(
    items: ValueProperty<List<EntityWithArrayOfNestedItems.Item>>,
): EntityWithArrayOfNestedItems {
    return EntityWithArrayOfNestedItems(
        items = items,
    )
}

fun CardContext.entityWithArrayOfNestedItems(
    items: List<EntityWithArrayOfNestedItems.Item>,
): EntityWithArrayOfNestedItems {
    return EntityWithArrayOfNestedItems(
        items = value(items),
    )
}

fun CardContext.item(
    entity: ValueProperty<Entity>,
    property: ValueProperty<String>,
): EntityWithArrayOfNestedItems.Item {
    return EntityWithArrayOfNestedItems.Item(
        entity = entity,
        property = property,
    )
}

fun CardContext.item(
    entity: Entity,
    property: String,
): EntityWithArrayOfNestedItems.Item {
    return EntityWithArrayOfNestedItems.Item(
        entity = value(entity),
        property = value(property),
    )
}
