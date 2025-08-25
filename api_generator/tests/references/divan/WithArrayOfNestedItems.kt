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
 * Can be created using the method [withArrayOfNestedItems].
 * 
 * Required parameters: `type, items`.
 */
@Generated
@ExposedCopyVisibility
data class WithArrayOfNestedItems internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_of_nested_items")
    )

    operator fun plus(additive: Properties): WithArrayOfNestedItems = WithArrayOfNestedItems(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
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
     * Can be created using the method [withArrayOfNestedItemsItem].
     * 
     * Required parameters: `property, entity`.
     */
    @Generated
    @ExposedCopyVisibility
    data class Item internal constructor(
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

        @ExposedCopyVisibility
        data class Properties internal constructor(
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
fun DivScope.withArrayOfNestedItems(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfNestedItems.Item>? = null,
): WithArrayOfNestedItems = WithArrayOfNestedItems(
    WithArrayOfNestedItems.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.withArrayOfNestedItemsProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfNestedItems.Item>? = null,
) = WithArrayOfNestedItems.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.withArrayOfNestedItemsRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<WithArrayOfNestedItems.Item>>? = null,
) = WithArrayOfNestedItems.Properties(
    items = items,
)

@Generated
fun WithArrayOfNestedItems.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfNestedItems.Item>? = null,
): WithArrayOfNestedItems = WithArrayOfNestedItems(
    WithArrayOfNestedItems.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun WithArrayOfNestedItems.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<WithArrayOfNestedItems.Item>>? = null,
): WithArrayOfNestedItems = WithArrayOfNestedItems(
    WithArrayOfNestedItems.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun WithArrayOfNestedItems.modify(
    `use named arguments`: Guard = Guard.instance,
    items: Property<List<WithArrayOfNestedItems.Item>>? = null,
): WithArrayOfNestedItems = WithArrayOfNestedItems(
    WithArrayOfNestedItems.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun Component<WithArrayOfNestedItems>.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfNestedItems.Item>? = null,
): Component<WithArrayOfNestedItems> = Component(
    template = template,
    properties = WithArrayOfNestedItems.Properties(
        items = valueOrNull(items),
    ).mergeWith(properties)
)

@Generated
fun Component<WithArrayOfNestedItems>.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<WithArrayOfNestedItems.Item>>? = null,
): Component<WithArrayOfNestedItems> = Component(
    template = template,
    properties = WithArrayOfNestedItems.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
fun Component<WithArrayOfNestedItems>.modify(
    `use named arguments`: Guard = Guard.instance,
    items: Property<List<WithArrayOfNestedItems.Item>>? = null,
): Component<WithArrayOfNestedItems> = Component(
    template = template,
    properties = WithArrayOfNestedItems.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithArrayOfNestedItems>.plus(additive: WithArrayOfNestedItems.Properties): Component<WithArrayOfNestedItems> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithArrayOfNestedItems.asList() = listOf(this)

@Generated
fun DivScope.withArrayOfNestedItemsItem(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
    property: String? = null,
): WithArrayOfNestedItems.Item = WithArrayOfNestedItems.Item(
    WithArrayOfNestedItems.Item.Properties(
        entity = valueOrNull(entity),
        property = valueOrNull(property),
    )
)

@Generated
fun DivScope.withArrayOfNestedItemsItemProps(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
    property: String? = null,
) = WithArrayOfNestedItems.Item.Properties(
    entity = valueOrNull(entity),
    property = valueOrNull(property),
)

@Generated
fun TemplateScope.withArrayOfNestedItemsItemRefs(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
    property: ReferenceProperty<String>? = null,
) = WithArrayOfNestedItems.Item.Properties(
    entity = entity,
    property = property,
)

@Generated
fun WithArrayOfNestedItems.Item.override(
    `use named arguments`: Guard = Guard.instance,
    entity: Entity? = null,
    property: String? = null,
): WithArrayOfNestedItems.Item = WithArrayOfNestedItems.Item(
    WithArrayOfNestedItems.Item.Properties(
        entity = valueOrNull(entity) ?: properties.entity,
        property = valueOrNull(property) ?: properties.property,
    )
)

@Generated
fun WithArrayOfNestedItems.Item.defer(
    `use named arguments`: Guard = Guard.instance,
    entity: ReferenceProperty<Entity>? = null,
    property: ReferenceProperty<String>? = null,
): WithArrayOfNestedItems.Item = WithArrayOfNestedItems.Item(
    WithArrayOfNestedItems.Item.Properties(
        entity = entity ?: properties.entity,
        property = property ?: properties.property,
    )
)

@Generated
fun WithArrayOfNestedItems.Item.modify(
    `use named arguments`: Guard = Guard.instance,
    entity: Property<Entity>? = null,
    property: Property<String>? = null,
): WithArrayOfNestedItems.Item = WithArrayOfNestedItems.Item(
    WithArrayOfNestedItems.Item.Properties(
        entity = entity ?: properties.entity,
        property = property ?: properties.property,
    )
)

@Generated
fun WithArrayOfNestedItems.Item.evaluate(
    `use named arguments`: Guard = Guard.instance,
    property: ExpressionProperty<String>? = null,
): WithArrayOfNestedItems.Item = WithArrayOfNestedItems.Item(
    WithArrayOfNestedItems.Item.Properties(
        entity = properties.entity,
        property = property ?: properties.property,
    )
)

@Generated
fun WithArrayOfNestedItems.Item.asList() = listOf(this)
