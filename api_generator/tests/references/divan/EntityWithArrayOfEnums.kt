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
 * Can be created using the method [`entityWithArrayOfEnums`].
 * 
 * Required properties: `type, items`.
 */
@Generated
class EntityWithArrayOfEnums internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_of_enums")
    )

    operator fun plus(additive: Properties): EntityWithArrayOfEnums = EntityWithArrayOfEnums(
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
     * Possible values: [first, second].
     */
    @Generated
    sealed interface Item
}

@Generated
fun DivScope.entityWithArrayOfEnums(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfEnums.Item>,
): EntityWithArrayOfEnums = EntityWithArrayOfEnums(
    EntityWithArrayOfEnums.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.entityWithArrayOfEnumsProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfEnums.Item>? = null,
) = EntityWithArrayOfEnums.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.entityWithArrayOfEnumsRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<EntityWithArrayOfEnums.Item>>? = null,
) = EntityWithArrayOfEnums.Properties(
    items = items,
)

@Generated
fun EntityWithArrayOfEnums.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfEnums.Item>? = null,
): EntityWithArrayOfEnums = EntityWithArrayOfEnums(
    EntityWithArrayOfEnums.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun Component<EntityWithArrayOfEnums>.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<EntityWithArrayOfEnums.Item>? = null,
): Component<EntityWithArrayOfEnums> = Component(
    template = template,
    properties = EntityWithArrayOfEnums.Properties(
        items = valueOrNull(items),
    ).mergeWith(properties)
)

@Generated
fun EntityWithArrayOfEnums.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<EntityWithArrayOfEnums.Item>>? = null,
): EntityWithArrayOfEnums = EntityWithArrayOfEnums(
    EntityWithArrayOfEnums.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun Component<EntityWithArrayOfEnums>.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<EntityWithArrayOfEnums.Item>>? = null,
): Component<EntityWithArrayOfEnums> = Component(
    template = template,
    properties = EntityWithArrayOfEnums.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithArrayOfEnums>.plus(additive: Properties): Component<EntityWithArrayOfEnums> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithArrayOfEnums.asList() = listOf(this)
