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
 * Can be created using the method [`entityWithArray`].
 * 
 * Required properties: `type, array`.
 */
@Generated
class EntityWithArray internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array")
    )

    operator fun plus(additive: Properties): EntityWithArray = EntityWithArray(
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
fun DivScope.entityWithArray(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>,
): EntityWithArray = EntityWithArray(
    EntityWithArray.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.entityWithArrayProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
) = EntityWithArray.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.entityWithArrayRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
) = EntityWithArray.Properties(
    array = array,
)

@Generated
fun EntityWithArray.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): EntityWithArray = EntityWithArray(
    EntityWithArray.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun Component<EntityWithArray>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Entity>? = null,
): Component<EntityWithArray> = Component(
    template = template,
    properties = EntityWithArray.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun EntityWithArray.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): EntityWithArray = EntityWithArray(
    EntityWithArray.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<EntityWithArray>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Entity>>? = null,
): Component<EntityWithArray> = Component(
    template = template,
    properties = EntityWithArray.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithArray>.plus(additive: Properties): Component<EntityWithArray> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithArray.asList() = listOf(this)
