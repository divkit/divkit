@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
import divan.core.ExpressionProperty
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
 *
 * Можно создать при помощи метода [entity_with_array_of_nested_items].
 *
 * Обязательные поля: type, items
 */
@Generated
class Entity_with_array_of_nested_items internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_array_of_nested_items")
    )

    operator fun plus(additive: Properties): Entity_with_array_of_nested_items =
            Entity_with_array_of_nested_items(
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
     *
     * Можно создать при помощи метода [entity_with_array_of_nested_itemsItem].
     *
     * Обязательные поля: property, entity
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
        		property = additive.property ?: properties.property,
        		entity = additive.entity ?: properties.entity,
        	)
        )

        class Properties internal constructor(
            val property: Property<String>?,
            val entity: Property<Entity>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("property", property)
                result.tryPutProperty("entity", entity)
                return result
            }
        }
    }
}

@Generated
fun DivScope.entity_with_array_of_nested_itemsItem(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
    entity: Entity? = null,
): Entity_with_array_of_nested_items.Item = Entity_with_array_of_nested_items.Item(
	Entity_with_array_of_nested_items.Item.Properties(
		property = valueOrNull(property),
		entity = valueOrNull(entity),
	)
)

@Generated
fun DivScope.entity_with_array_of_nested_itemsItemProps(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
    entity: Entity? = null,
) = Entity_with_array_of_nested_items.Item.Properties(
	property = valueOrNull(property),
	entity = valueOrNull(entity),
)

@Generated
fun TemplateScope.entity_with_array_of_nested_itemsItemRefs(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
    entity: ReferenceProperty<Entity>? = null,
) = Entity_with_array_of_nested_items.Item.Properties(
	property = property,
	entity = entity,
)

@Generated
fun Entity_with_array_of_nested_items.Item.override(
    `use named arguments`: Guard = Guard.instance,
    property: String? = null,
    entity: Entity? = null,
): Entity_with_array_of_nested_items.Item = Entity_with_array_of_nested_items.Item(
	Entity_with_array_of_nested_items.Item.Properties(
		property = valueOrNull(property) ?: properties.property,
		entity = valueOrNull(entity) ?: properties.entity,
	)
)

@Generated
fun Entity_with_array_of_nested_items.Item.defer(
    `use named arguments`: Guard = Guard.instance,
    property: ReferenceProperty<String>? = null,
    entity: ReferenceProperty<Entity>? = null,
): Entity_with_array_of_nested_items.Item = Entity_with_array_of_nested_items.Item(
	Entity_with_array_of_nested_items.Item.Properties(
		property = property ?: properties.property,
		entity = entity ?: properties.entity,
	)
)

@Generated
fun Entity_with_array_of_nested_items.Item.evaluate(`use named arguments`: Guard =
        Guard.instance, property: ExpressionProperty<String>? = null):
        Entity_with_array_of_nested_items.Item = Entity_with_array_of_nested_items.Item(
	Entity_with_array_of_nested_items.Item.Properties(
		property = property ?: properties.property,
		entity = properties.entity,
	)
)

@Generated
fun Entity_with_array_of_nested_items.Item.asList() = listOf(this)

@Generated
fun DivScope.entity_with_array_of_nested_items(`use named arguments`: Guard = Guard.instance,
        items: List<Entity_with_array_of_nested_items.Item>? = null):
        Entity_with_array_of_nested_items = Entity_with_array_of_nested_items(
	Entity_with_array_of_nested_items.Properties(
		items = valueOrNull(items),
	)
)

@Generated
fun DivScope.entity_with_array_of_nested_itemsProps(`use named arguments`: Guard =
        Guard.instance, items: List<Entity_with_array_of_nested_items.Item>? = null) =
        Entity_with_array_of_nested_items.Properties(
	items = valueOrNull(items),
)

@Generated
fun TemplateScope.entity_with_array_of_nested_itemsRefs(`use named arguments`: Guard =
        Guard.instance, items: ReferenceProperty<List<Entity_with_array_of_nested_items.Item>>? =
        null) = Entity_with_array_of_nested_items.Properties(
	items = items,
)

@Generated
fun Entity_with_array_of_nested_items.override(`use named arguments`: Guard =
        Guard.instance, items: List<Entity_with_array_of_nested_items.Item>? = null):
        Entity_with_array_of_nested_items = Entity_with_array_of_nested_items(
	Entity_with_array_of_nested_items.Properties(
		items = valueOrNull(items) ?: properties.items,
	)
)

@Generated
fun Entity_with_array_of_nested_items.defer(`use named arguments`: Guard = Guard.instance,
        items: ReferenceProperty<List<Entity_with_array_of_nested_items.Item>>? = null):
        Entity_with_array_of_nested_items = Entity_with_array_of_nested_items(
	Entity_with_array_of_nested_items.Properties(
		items = items ?: properties.items,
	)
)
