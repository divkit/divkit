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
 * Can be created using the method [`entityWithStrictArray`].
 * 
 * Required properties: `type, array`.
 */
@Generated
class EntityWithStrictArray internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_strict_array")
    )

    operator fun plus(additive: Properties): EntityWithStrictArray = EntityWithStrictArray(
        Properties(
            array = additive.array ?: properties.array,
        )
    )

    class Properties internal constructor(
        val array: Property<List<Entity>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("array", array)
            return result
        }
    }
}

@Generated
fun DivScope.entityWithStrictArray(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>,
): EntityWithStrictArray = EntityWithStrictArray(
    EntityWithStrictArray.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.entityWithStrictArrayProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
) = EntityWithStrictArray.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.entityWithStrictArrayRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
) = EntityWithStrictArray.Properties(
    array = array,
)

@Generated
fun EntityWithStrictArray.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): EntityWithStrictArray = EntityWithStrictArray(
    EntityWithStrictArray.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun Component<EntityWithStrictArray>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): Component<EntityWithStrictArray> = Component(
    template = template,
    properties = EntityWithStrictArray.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun EntityWithStrictArray.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): EntityWithStrictArray = EntityWithStrictArray(
    EntityWithStrictArray.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<EntityWithStrictArray>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): Component<EntityWithStrictArray> = Component(
    template = template,
    properties = EntityWithStrictArray.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithStrictArray>.plus(additive: Properties): Component<EntityWithStrictArray> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithStrictArray.asList() = listOf(this)
