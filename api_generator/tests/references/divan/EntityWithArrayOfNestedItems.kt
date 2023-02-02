@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
import divan.core.Guard
import divan.core.Property
import divan.core.ReferenceProperty
import divan.core.tryPutProperty
import divan.core.valueOrNull
import divan.scope.DivScope
import divan.scope.TemplateScope
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Can be created using the method [`entityWithArrayOfNestedItems`].
 * 
 * Required properties: `type, items`.
 */
@Generated
class EntityWithArrayOfNestedItems internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_of_nested_items")
    )

    operator fun plus(additive: Properties): EntityWithArrayOfNestedItems = EntityWithArrayOfNestedItems(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    class Properties internal constructor(
        val items: Property<List<Item>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("items", items)
            return result
        }
    }

    /**
     * Can be created using the method [`entityWithArrayOfNestedItemsItem`].
     * 
     * Required properties: `property, entity`.
     */
    @Generated
    class Item internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Item = Item(
            Properties(
                entity = additive.entity ?: properties.entity,
                property = additive.property ?: properties.property,
            )
        )

        class Properties internal constructor(
            val entity: Property<Entity>?,
            val property: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("entity", entity)
                result.tryPutProperty("property", property)
                return result
            }
        }
    }

}

@Generated
fun DivScope.entityWithArrayOfNestedItems(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfNestedItems.Item>,
): EntityWithArrayOfNestedItems = EntityWithArrayOfNestedItems(
    EntityWithArrayOfNestedItems.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.entityWithArrayOfNestedItemsProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfNestedItems.Item>? = null,
) = EntityWithArrayOfNestedItems.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.entityWithArrayOfNestedItemsRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<EntityWithArrayOfNestedItems.Item>>? = null,
) = EntityWithArrayOfNestedItems.Properties(
    items = items,
)

@Generated
fun EntityWithArrayOfNestedItems.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfNestedItems.Item>? = null,
): EntityWithArrayOfNestedItems = EntityWithArrayOfNestedItems(
    EntityWithArrayOfNestedItems.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun Component<EntityWithArrayOfNestedItems>.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfNestedItems.Item>? = null,
): Component<EntityWithArrayOfNestedItems> = Component(
    template = template,
    properties = EntityWithArrayOfNestedItems.Properties(
        items = valueOrNull(items),
    ).mergeWith(properties)
)

@Generated
fun EntityWithArrayOfNestedItems.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<EntityWithArrayOfNestedItems.Item>>? = null,
): EntityWithArrayOfNestedItems = EntityWithArrayOfNestedItems(
    EntityWithArrayOfNestedItems.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun Component<EntityWithArrayOfNestedItems>.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<EntityWithArrayOfNestedItems.Item>>? = null,
): Component<EntityWithArrayOfNestedItems> = Component(
    template = template,
    properties = EntityWithArrayOfNestedItems.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithArrayOfNestedItems>.plus(additive: Properties): Component<EntityWithArrayOfNestedItems> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithArrayOfNestedItems.asList() = listOf(this)

@Generated
fun DivScope.entityWithArrayOfNestedItemsItem(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity,
    property: String,
): EntityWithArrayOfNestedItems.Item = EntityWithArrayOfNestedItems.Item(
    EntityWithArrayOfNestedItems.Item.Properties(
        entity = valueOrNull(entity),
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.entityWithArrayOfNestedItemsItemProps(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
    property: String? = null,
) = EntityWithArrayOfNestedItems.Item.Properties(
    entity = valueOrNull(entity),
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.entityWithArrayOfNestedItemsItemRefs(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
    property: ReferenceProperty<String>? = null,
) = EntityWithArrayOfNestedItems.Item.Properties(
    entity = entity,
    property = property,
)

@Generated
fun EntityWithArrayOfNestedItems.Item.override(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
    property: String? = null,
): EntityWithArrayOfNestedItems.Item = EntityWithArrayOfNestedItems.Item(
    EntityWithArrayOfNestedItems.Item.Properties(
        entity = valueOrNull(entity) ?: properties.entity,
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun Component<EntityWithArrayOfNestedItems.Item>.override(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
    property: String? = null,
): Component<EntityWithArrayOfNestedItems.Item> = Component(
    template = template,
    properties = EntityWithArrayOfNestedItems.Item.Properties(
        entity = valueOrNull(entity),
        property = valueOrNull(property),
    ).mergeWith(properties)
)

@Generated
fun EntityWithArrayOfNestedItems.Item.defer(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
    property: ReferenceProperty<String>? = null,
): EntityWithArrayOfNestedItems.Item = EntityWithArrayOfNestedItems.Item(
    EntityWithArrayOfNestedItems.Item.Properties(
        entity = entity ?: properties.entity,
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithArrayOfNestedItems.Item>.defer(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
    property: ReferenceProperty<String>? = null,
): Component<EntityWithArrayOfNestedItems.Item> = Component(
    template = template,
    properties = EntityWithArrayOfNestedItems.Item.Properties(
        entity = entity,
        property = property,
    ).mergeWith(properties)
)

@Generated
fun EntityWithArrayOfNestedItems.Item.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): EntityWithArrayOfNestedItems.Item = EntityWithArrayOfNestedItems.Item(
    EntityWithArrayOfNestedItems.Item.Properties(
        entity = properties.entity,
        property = property ?: properties.property,
    )
)

@Generated
fun Component<EntityWithArrayOfNestedItems.Item>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): Component<EntityWithArrayOfNestedItems.Item> = Component(
    template = template,
    properties = EntityWithArrayOfNestedItems.Item.Properties(
        entity = null,
        property = property,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Item>.plus(additive: Properties): Component<Item> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithArrayOfNestedItems.Item.asList() = listOf(this)
