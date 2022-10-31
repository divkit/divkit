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
 *
 * Можно создать при помощи метода [entity_with_array_of_enums].
 *
 * Обязательные поля: type, items
 */
@Generated
class Entity_with_array_of_enums internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_array_of_enums")
    )

    operator fun plus(additive: Properties): Entity_with_array_of_enums =
            Entity_with_array_of_enums(
    	Properties(
    		items = additive.items ?: properties.items,
    	)
    )

    class Properties internal constructor(
        val items: Property<List<Items.Items>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("items", items)
            return result
        }
    }

    /**
     * [first, second]
     */
    @Generated
    sealed interface Items
}

@Generated
fun Entity_with_array_of_enums.Items.Items.asList() = listOf(this)

@Generated
fun DivScope.entity_with_array_of_enums(`use named arguments`: Guard = Guard.instance,
        items: List<Entity_with_array_of_enums.Items.Items>? = null): Entity_with_array_of_enums =
        Entity_with_array_of_enums(
	Entity_with_array_of_enums.Properties(
		items = valueOrNull(items),
	)
)

@Generated
fun DivScope.entity_with_array_of_enumsProps(`use named arguments`: Guard = Guard.instance,
        items: List<Entity_with_array_of_enums.Items.Items>? = null) =
        Entity_with_array_of_enums.Properties(
	items = valueOrNull(items),
)

@Generated
fun TemplateScope.entity_with_array_of_enumsRefs(`use named arguments`: Guard =
        Guard.instance, items: ReferenceProperty<List<Entity_with_array_of_enums.Items.Items>>? =
        null) = Entity_with_array_of_enums.Properties(
	items = items,
)

@Generated
fun Entity_with_array_of_enums.override(`use named arguments`: Guard = Guard.instance,
        items: List<Entity_with_array_of_enums.Items.Items>? = null): Entity_with_array_of_enums =
        Entity_with_array_of_enums(
	Entity_with_array_of_enums.Properties(
		items = valueOrNull(items) ?: properties.items,
	)
)

@Generated
fun Entity_with_array_of_enums.defer(`use named arguments`: Guard = Guard.instance,
        items: ReferenceProperty<List<Entity_with_array_of_enums.Items.Items>>? = null):
        Entity_with_array_of_enums = Entity_with_array_of_enums(
	Entity_with_array_of_enums.Properties(
		items = items ?: properties.items,
	)
)
